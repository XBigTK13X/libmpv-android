#include <jni.h>
#include <cstdlib>
#include <cstdio>
#include <ctime>
#include <clocale>
#include <atomic>
#include <thread>

#include <mpv/client.h>
#include <pthread.h>

extern "C" {
    #include <libavcodec/jni.h>
}

#include "log.h"
#include "jni_utils.h"
#include "event.h"
#include "globals.h"

#define ARRAYLEN(a) (sizeof(a)/sizeof(a[0]))

extern "C" {
    jni_func(jlong, nativeCreate, jobject thiz, jobject appctx);
    jni_func(void, nativeInit, jlong instance);
    jni_func(void, nativeDestroy, jlong instance);
    jni_func(void, nativeCommand, jlong instance, jobjectArray jarray);
};

static void prepare_environment(JNIEnv *env, jobject appctx, MPVInstance* instance) {
    setlocale(LC_NUMERIC, "C");

    if (!env->GetJavaVM(&instance->vm) && instance->vm) {
        av_jni_set_java_vm(instance->vm, nullptr);
    }

    if (!instance->methods_initialized) {
        instance->methods_initialized = init_methods_cache(env, instance->javaObject);
    }
}

jni_func(jlong, nativeCreate, jobject thiz, jobject appctx) {
    auto *instance = new MPVInstance();

    instance->event_thread_request_exit = false;
    instance->event_thread_id = pthread_t{};
    instance->mpv = nullptr;
    instance->javaObject = nullptr;
    instance->surface = nullptr;

    jobject global = env->NewGlobalRef(thiz);
    if (!global) {
        delete instance;
        throwJavaException(env, "nativeCreate -> failed to create global ref");
        return -1;
    }
    instance->javaObject = global;

    prepare_environment(env, appctx, instance);

    instance->mpv = mpv_create();
    if (!instance->mpv) {
        env->DeleteGlobalRef(instance->javaObject);
        instance->javaObject = nullptr;
        delete instance;
        throwJavaException(env, "nativeCreate -> context init failed");
        return -1;
    }

    mpv_request_log_messages(instance->mpv, "v");

    return reinterpret_cast<jlong>(instance);
}

jni_func(void, nativeInit, jlong instance) {
    auto *mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance) {
        throwJavaException(env, "nativeInit -> instance is null");
        return;
    }
    if (!mpv_instance->mpv) {
        throwJavaException(env, "nativeInit -> mpv is not created");
        return;
    }

    if (mpv_initialize(mpv_instance->mpv) < 0) {
        throwJavaException(env, "nativeInit -> mpv init failed");
        return;
    }

    mpv_instance->event_thread_request_exit = false;

    pthread_t tid{};
    int rc = pthread_create(&tid, nullptr, event_thread, mpv_instance);
    if (rc != 0) {
        throwJavaException(env, "nativeInit -> failed to start event thread");
        mpv_instance->event_thread_id = 0; // mark as not started
        return;
    }

    // Store the valid thread ID
    mpv_instance->event_thread_id = tid;
}


jni_func(void, nativeDestroy, jlong instance) {
    auto *mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance || !mpv_instance->mpv) {
        ALOGE("nativeDestroy -> mpv destroy called but it's already destroyed");
        return;
    }

    mpv_instance->event_thread_request_exit = true;
    mpv_wakeup(mpv_instance->mpv);

    pthread_t tid = mpv_instance->event_thread_id;
    if (tid != 0 && !pthread_equal(pthread_self(), tid)) {
        pthread_join(tid, nullptr);
    }

    if (mpv_instance->javaObject) {
        env->DeleteGlobalRef(mpv_instance->javaObject);
        mpv_instance->javaObject = nullptr;
    }

    mpv_terminate_destroy(mpv_instance->mpv);
    mpv_instance->mpv = nullptr;

    delete mpv_instance;
}


jni_func(void, nativeCommand, jlong instance, jobjectArray jarray) {
    auto *mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance || !mpv_instance->mpv) {
        throwJavaException(env, "nativeCommand -> mpv is not initialized");
        return;
    }
    if (mpv_instance->event_thread_request_exit) {
        ALOGW("nativeCommand -> called during destroy; ignoring");
        return;
    }

    const jsize len = env->GetArrayLength(jarray);
    if (len < 0) {
        throwJavaException(env, "nativeCommand -> invalid array length");
        return;
    }
    if (len >= 128) {
        throwJavaException(env, "nativeCommand -> too many arguments");
        return;
    }

    const char *arguments[128] = {0};
    jstring jstrs[128] = {0};

    for (jsize i = 0; i < len; ++i) {
        jstring s = static_cast<jstring>(env->GetObjectArrayElement(jarray, i));
        jstrs[i] = s;

        if (!s) {
            for (jsize k = 0; k < i; ++k) {
                if (arguments[k]) env->ReleaseStringUTFChars(jstrs[k], arguments[k]);
                if (jstrs[k]) env->DeleteLocalRef(jstrs[k]);
            }
            throwJavaException(env, "nativeCommand -> null argument element");
            return;
        }

        const char *utf = env->GetStringUTFChars(s, nullptr);
        if (!utf) {
            for (jsize k = 0; k <= i; ++k) {
                if (arguments[k]) env->ReleaseStringUTFChars(jstrs[k], arguments[k]);
                if (jstrs[k]) env->DeleteLocalRef(jstrs[k]);
            }
            throwJavaException(env, "nativeCommand -> OOM converting string");
            return;
        }

        arguments[i] = utf;
    }

    int rc = mpv_command(mpv_instance->mpv, arguments);

    for (jsize i = 0; i < len; ++i) {
        env->ReleaseStringUTFChars(jstrs[i], arguments[i]);
        env->DeleteLocalRef(jstrs[i]);
    }

    if (rc < 0) {
        throwJavaException(env, "nativeCommand -> mpv_command failed");
        return;
    }
}
