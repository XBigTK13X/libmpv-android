
#include <jni.h>
#include <cstdlib>

#include <mpv/client.h>

#include "jni_utils.h"
#include "log.h"
#include "globals.h"

extern "C" {
    jni_func(jint, nativeSetOptionString, jlong jinstance, jstring joption, jstring jvalue);
    jni_func(jobject, nativeGetPropertyInt, jlong jinstance, jstring jproperty);
    jni_func(void, nativeSetPropertyInt, jlong jinstance, jstring jproperty, jobject jvalue);
    jni_func(jobject, nativeGetPropertyDouble, jlong jinstance, jstring jproperty);
    jni_func(void, nativeSetPropertyDouble, jlong jinstance, jstring jproperty, jobject jvalue);
    jni_func(jobject, nativeGetPropertyBoolean, jlong jinstance, jstring jproperty);
    jni_func(void, nativeSetPropertyBoolean, jlong jinstance, jstring jproperty, jobject jvalue);
    jni_func(jstring, nativeGetPropertyString, jlong jinstance, jstring jproperty);
    jni_func(void, nativeSetPropertyString, jlong jinstance, jstring jproperty, jstring jvalue);
    jni_func(void, nativeObserveProperty, jlong jinstance, jstring jproperty, jint jformat);
}

static inline MPVInstance* get_instance(jlong handle) {
    if (handle == 0) return nullptr;
    return reinterpret_cast<MPVInstance*>(handle);
}

static void common_set_property(JNIEnv* env, mpv_handle* mpv, jstring jproperty, mpv_format format, void* data) {
    if (!mpv || !jproperty) return;
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    if (!prop) return;
    mpv_set_property(mpv, prop, format, data);
    env->ReleaseStringUTFChars(jproperty, prop);
}

jni_func(jint, nativeSetOptionString, jlong jinstance, jstring joption, jstring jvalue) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !joption || !jvalue) return -1;
    const char *option = env->GetStringUTFChars(joption, nullptr);
    const char *value  = env->GetStringUTFChars(jvalue,  nullptr);
    if (!option || !value) {
        if (option) env->ReleaseStringUTFChars(joption, option);
        if (value)  env->ReleaseStringUTFChars(jvalue,  value);
        return -1;
    }
    int rc = mpv_set_option_string(mpv_instance->mpv, option, value);
    env->ReleaseStringUTFChars(joption, option);
    env->ReleaseStringUTFChars(jvalue,  value);
    return rc;
}

jni_func(jobject, nativeGetPropertyInt, jlong jinstance, jstring jproperty) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty) return nullptr;
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    if (!prop) return nullptr;
    int64_t val = 0;
    mpv_get_property(mpv_instance->mpv, prop, MPV_FORMAT_INT64, &val);
    env->ReleaseStringUTFChars(jproperty, prop);
    jclass cls = env->FindClass("java/lang/Integer");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(I)V");
    return env->NewObject(cls, ctor, (jint)val);
}

jni_func(void, nativeSetPropertyInt, jlong jinstance, jstring jproperty, jobject jvalue) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty || !jvalue) return;
    jclass cls = env->FindClass("java/lang/Integer");
    jmethodID mid = env->GetMethodID(cls, "intValue", "()I");
    jint v = env->CallIntMethod(jvalue, mid);
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_INT64, &v);
}

jni_func(jobject, nativeGetPropertyDouble, jlong jinstance, jstring jproperty) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty) return nullptr;
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    if (!prop) return nullptr;
    double val = 0.0;
    mpv_get_property(mpv_instance->mpv, prop, MPV_FORMAT_DOUBLE, &val);
    env->ReleaseStringUTFChars(jproperty, prop);
    jclass cls = env->FindClass("java/lang/Double");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(D)V");
    return env->NewObject(cls, ctor, (jdouble)val);
}

jni_func(void, nativeSetPropertyDouble, jlong jinstance, jstring jproperty, jobject jvalue) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty || !jvalue) return;
    jclass cls = env->FindClass("java/lang/Double");
    jmethodID mid = env->GetMethodID(cls, "doubleValue", "()D");
    jdouble v = env->CallDoubleMethod(jvalue, mid);
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_DOUBLE, &v);
}

jni_func(jobject, nativeGetPropertyBoolean, jlong jinstance, jstring jproperty) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty) return nullptr;
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    if (!prop) return nullptr;
    int flag = 0;
    mpv_get_property(mpv_instance->mpv, prop, MPV_FORMAT_FLAG, &flag);
    env->ReleaseStringUTFChars(jproperty, prop);
    jclass cls = env->FindClass("java/lang/Boolean");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "(Z)V");
    return env->NewObject(cls, ctor, (jboolean)(flag != 0));
}

jni_func(void, nativeSetPropertyBoolean, jlong jinstance, jstring jproperty, jobject jvalue) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty || !jvalue) return;
    jclass cls = env->FindClass("java/lang/Boolean");
    jmethodID mid = env->GetMethodID(cls, "booleanValue", "()Z");
    jboolean v = env->CallBooleanMethod(jvalue, mid);
    int flag = v ? 1 : 0;
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_FLAG, &flag);
}

jni_func(jstring, nativeGetPropertyString, jlong jinstance, jstring jproperty) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty) return nullptr;
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    if (!prop) return nullptr;
    char *out = nullptr;
    mpv_get_property(mpv_instance->mpv, prop, MPV_FORMAT_STRING, &out);
    env->ReleaseStringUTFChars(jproperty, prop);
    if (!out) return nullptr;
    jstring result = env->NewStringUTF(out);
    mpv_free(out);
    return result;
}

jni_func(void, nativeSetPropertyString, jlong jinstance, jstring jproperty, jstring jvalue) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty || !jvalue) return;
    const char *value = env->GetStringUTFChars(jvalue, nullptr);
    if (!value) return;
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_STRING, (void*)&value);
    env->ReleaseStringUTFChars(jvalue, value);
}

jni_func(void, nativeObserveProperty, jlong jinstance, jstring jproperty, jint jformat) {
    auto mpv_instance = get_instance(jinstance);
    if (!mpv_instance || !mpv_instance->mpv || !jproperty) return;
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    if (!prop) return;
    mpv_observe_property(mpv_instance->mpv, 0, prop, (mpv_format)jformat);
    env->ReleaseStringUTFChars(jproperty, prop);
}
