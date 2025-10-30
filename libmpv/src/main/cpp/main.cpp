
#include <jni.h>
#include <cstdlib>
#include <cstdio>
#include <ctime>
#include <clocale>

#include <mpv/client.h>
#include <pthread.h>

extern "C" {
  #include <libavcodec/jni.h>
}

#include "log.h"
#include "jni_utils.h"
#include "globals.h"

extern "C" {
    jni_func(jlong, nativeCreate, jobject thiz, jobject appctx);
    jni_func(void, nativeInit, jlong instance);
    jni_func(void, nativeDestroy, jlong instance);
    jni_func(void, nativeCommand, jlong instance, jobjectArray jarray);
};

static inline MPVInstance* get_instance(jlong handle) {
    if (handle == 0) return nullptr;
    return reinterpret_cast<MPVInstance*>(handle);
}

static void prepare_environment(JNIEnv *env, jobject appctx, MPVInstance* instance) {
    setlocale(LC_NUMERIC, "C");

    if (!env->GetJavaVM(&instance->vm) && instance->vm)
        av_jni_set_java_vm(instance->vm, nullptr);
    if (!instance->methods_initialized) {
        instance->methods_initialized = init_methods_cache(env, instance->javaObject);
    }

    // Disable locale-specific number formatting
    setenv("LC_NUMERIC", "C", 1);
}

jni_func(jlong, nativeCreate, jobject thiz, jobject appctx) {
    auto instance = new MPVInstance();
    instance->javaObject = env->NewGlobalRef(thiz);
    instance->methods_initialized = false;
    instance->mpv = mpv_create();
    instance->surface = nullptr;
    instance->event_thread_request_exit = false;

    if (!instance->mpv) {
        env->DeleteGlobalRef(instance->javaObject);
        delete instance;
        die("context init failed");
    }

    prepare_environment(env, appctx, instance);
    mpv_request_log_messages(instance->mpv, "v");
    return reinterpret_cast<jlong>(instance);
}

static void* event_thread(void* data);

jni_func(void, nativeInit, jlong handle) {
    auto mpv_instance = get_instance(handle);
    if (!mpv_instance || !mpv_instance->mpv) return;

    if (mpv_initialize(mpv_instance->mpv) < 0) {
        die("mpv init failed");
        return;
    }

    mpv_instance->event_thread_request_exit = false;
    pthread_create(&mpv_instance->event_thread_id, nullptr, event_thread, mpv_instance);
}

jni_func(void, nativeDestroy, jlong handle) {
    auto mpv_instance = get_instance(handle);
    if (!mpv_instance) return;

    // Stop event thread
    mpv_instance->event_thread_request_exit = true;
    if (mpv_instance->mpv) {
        mpv_wakeup(mpv_instance->mpv);
    }
    if (mpv_instance->event_thread_id) {
        pthread_join(mpv_instance->event_thread_id, nullptr);
    }

    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }

    if (mpv_instance->mpv) {
        mpv_terminate_destroy(mpv_instance->mpv);
        mpv_instance->mpv = nullptr;
    }

    if (mpv_instance->javaObject) {
        env->DeleteGlobalRef(mpv_instance->javaObject);
        mpv_instance->javaObject = nullptr;
    }

    delete mpv_instance;
}

jni_func(void, nativeCommand, jlong handle, jobjectArray jarray) {
    auto mpv_instance = get_instance(handle);
    if (!mpv_instance || !mpv_instance->mpv || !jarray) return;

    const jsize len = env->GetArrayLength(jarray);
    if (len <= 0) return;
    if (len > 128) return;

    const char* arguments[129] = {0};
    jstring localRefs[129] = {0};

    for (jsize i = 0; i < len; ++i) {
        jstring s = (jstring)env->GetObjectArrayElement(jarray, i);
        localRefs[i] = s;
        if (!s) { // clean up and abort
            for (jsize k = 0; k < i; ++k) {
                env->ReleaseStringUTFChars(localRefs[k], arguments[k]);
                env->DeleteLocalRef(localRefs[k]);
            }
            return;
        }
        arguments[i] = env->GetStringUTFChars(s, nullptr);
        if (!arguments[i]) {
            for (jsize k = 0; k <= i; ++k) {
                if (arguments[k]) env->ReleaseStringUTFChars(localRefs[k], arguments[k]);
                if (localRefs[k]) env->DeleteLocalRef(localRefs[k]);
            }
            return;
        }
    }

    mpv_command(mpv_instance->mpv, arguments);

    for (jsize i = 0; i < len; ++i) {
        if (arguments[i]) env->ReleaseStringUTFChars(localRefs[i], arguments[i]);
        if (localRefs[i]) env->DeleteLocalRef(localRefs[i]);
    }
}

// Minimal event thread stub (actual implementation likely elsewhere)
static void* event_thread(void* data) {
    MPVInstance* inst = (MPVInstance*)data;
    while (!inst->event_thread_request_exit) {
        if (inst->mpv) {
            mpv_event* ev = mpv_wait_event(inst->mpv, 0.1);
            (void)ev;
        }
    }
    return nullptr;
}
