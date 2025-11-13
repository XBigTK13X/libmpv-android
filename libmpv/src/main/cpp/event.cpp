#include <jni.h>
#include <mpv/client.h>

#include "globals.h"
#include "jni_utils.h"
#include "log.h"

static inline void log_and_clear_exception(JNIEnv* env, const char* where) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        ALOGE("%s -> Java exception was thrown and cleared", where);
    }
}

static void sendPropertyUpdateToJava(JNIEnv *env, MPVInstance* instance, mpv_event_property *prop) {
    if (!prop || !prop->name) {
        ALOGW("sendPropertyUpdateToJava -> null property or name");
        return;
    }

    jstring jprop = env->NewStringUTF(prop->name);
    jstring jvalue = nullptr;

    switch (prop->format) {
    case MPV_FORMAT_NONE:
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_S, jprop);
        log_and_clear_exception(env, "eventProperty NONE");
        break;

    case MPV_FORMAT_FLAG: {
        int flag = 0;
        if (prop->data) flag = *(int*)prop->data;
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_Sb, jprop, (jboolean)flag);
        log_and_clear_exception(env, "eventProperty FLAG");
        break;
    }

    case MPV_FORMAT_INT64: {
        int64_t val = 0;
        if (prop->data) val = *(int64_t*)prop->data;
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_Sl, jprop, (jlong)val);
        log_and_clear_exception(env, "eventProperty INT64");
        break;
    }

    case MPV_FORMAT_DOUBLE: {
        double d = 0.0;
        if (prop->data) d = *(double*)prop->data;
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_Sd, jprop, (jdouble)d);
        log_and_clear_exception(env, "eventProperty DOUBLE");
        break;
    }

    case MPV_FORMAT_STRING: {
        const char* s = nullptr;
        if (prop->data) s = *(const char**)prop->data;
        jvalue = env->NewStringUTF(s ? s : "");
        env->CallVoidMethod(instance->javaObject, mpv_MPVLib_eventProperty_SS, jprop, jvalue);
        log_and_clear_exception(env, "eventProperty STRING");
        break;
    }

    default:
        ALOGE("sendPropertyUpdateToJava -> Unknown property format: %d", prop->format);
        break;
    }

    if (jprop)  env->DeleteLocalRef(jprop);
    if (jvalue) env->DeleteLocalRef(jvalue);
}

static void sendEventToJava(JNIEnv *env, MPVInstance* instance, int event) {
    env->CallVoidMethod(instance->javaObject, mpv_MPVLib_event, (jint)event);
    log_and_clear_exception(env, "sendEventToJava");
}

static inline bool invalid_utf8(unsigned char c) {
    return c == 0xc0 || c == 0xc1 || c >= 0xf5;
}

static void sendLogMessageToJava(JNIEnv *env, MPVInstance* instance, mpv_event_log_message *msg) {
    if (!msg) return;

    int invalid = 0;
    if (msg->text) {
        for (int i = 0; msg->text[i]; i++) {
            invalid |= invalid_utf8((unsigned char) msg->text[i]);
        }
    }
    if (invalid) return;

    jstring jprefix = env->NewStringUTF(msg->prefix ? msg->prefix : "");
    jstring jtext   = env->NewStringUTF(msg->text   ? msg->text   : "");

    env->CallVoidMethod(instance->javaObject, mpv_MPVLib_logMessage_SiS,
                        jprefix, (jint) msg->log_level, jtext);
    log_and_clear_exception(env, "sendLogMessageToJava");

    if (jprefix) env->DeleteLocalRef(jprefix);
    if (jtext)   env->DeleteLocalRef(jtext);
}

void *event_thread(void *arg) {
    auto instance = static_cast<MPVInstance*>(arg);
    if (!instance || !instance->vm || !instance->mpv) {
        ALOGE("event_thread -> invalid instance/vm/mpv");
        return nullptr;
    }

    JNIEnv *env = nullptr;
    acquire_jni_env(instance->vm, &env);
    if (!env) {
        ALOGE("event_thread -> failed to acquire JNIEnv");
        return nullptr;
    }

    while (true) {
        mpv_event *mp_event = mpv_wait_event(instance->mpv, -1.0);

        if (instance->event_thread_request_exit) break;
        if (!mp_event) continue;

        if (mp_event->event_id == MPV_EVENT_NONE) continue;

        switch (mp_event->event_id) {
        case MPV_EVENT_LOG_MESSAGE: {
            auto *msg = (mpv_event_log_message*)mp_event->data;
            sendLogMessageToJava(env, instance, msg);
            break;
        }
        case MPV_EVENT_PROPERTY_CHANGE: {
            auto *mp_property = (mpv_event_property*)mp_event->data;
            sendPropertyUpdateToJava(env, instance, mp_property);
            break;
        }
        default:
            sendEventToJava(env, instance, (int)mp_event->event_id);
            break;
        }
    }

    instance->vm->DetachCurrentThread();
    return nullptr;
}
