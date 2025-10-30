
#include <jni.h>
#include <mpv/client.h>

#include "globals.h"
#include "jni_utils.h"
#include "log.h"

static void sendPropertyUpdateToJava(JNIEnv *env, MPVInstance* instance, mpv_event_property *prop) {
    if (!env || !instance || instance->disposed.load() || !instance->javaObject) return;
    jstring jprop = env->NewStringUTF(prop->name ? prop->name : "");
    jstring jvalue = nullptr;
    switch (prop->format) {
    case MPV_FORMAT_NONE:
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_S, jprop);
        break;
    case MPV_FORMAT_FLAG: {
        int flag = *(int*)prop->data;
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_Sb, jprop, (jboolean)(flag != 0));
        break;
    }
    case MPV_FORMAT_INT64: {
        int64_t i64 = *(int64_t*)prop->data;
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_Sl, jprop, (jlong)i64);
        break;
    }
    case MPV_FORMAT_DOUBLE: {
        double d = *(double*)prop->data;
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_Sd, jprop, (jdouble)d);
        break;
    }
    case MPV_FORMAT_STRING: {
        const char *s = *(const char**)prop->data;
        jvalue = env->NewStringUTF(s ? s : "");
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_SS, jprop, jvalue);
        env->DeleteLocalRef(jvalue);
        break;
    }
    default:
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_S, jprop);
        break;
    }
    env->DeleteLocalRef(jprop);
}

static void sendEventToJava(JNIEnv *env, MPVInstance* instance, int eventId) {
    if (!env || !instance || instance->disposed.load() || !instance->javaObject) return;
    env->CallVoidMethod(instance->javaObject, mpv_MPVLib_event, (jint)eventId);
}

extern "C" void *event_thread(void *arg) {
    MPVInstance *instance = (MPVInstance*)arg;
    if (!instance) return nullptr;

    JNIEnv *env = nullptr;
    if (!instance->vm || !acquire_jni_env(instance->vm, &env)) {
        return nullptr;
    }

    while (!instance->disposed.load() && !instance->event_thread_request_exit.load()) {
        if (!instance->mpv) break;
        mpv_event *mp_event = mpv_wait_event(instance->mpv, 0.1);
        if (!mp_event) continue;
        if (mp_event->event_id == MPV_EVENT_NONE) continue;

        switch (mp_event->event_id) {
        case MPV_EVENT_PROPERTY_CHANGE: {
            mpv_event_property *mp_property = (mpv_event_property*)mp_event->data;
            if (mp_property) sendPropertyUpdateToJava(env, instance, mp_property);
            break;
        }
        default:
            sendEventToJava(env, instance, mp_event->event_id);
            break;
        }
    }

    if (instance->vm) {
        instance->vm->DetachCurrentThread();
    }
    return nullptr;
}
