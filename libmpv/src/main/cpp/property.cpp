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

jni_func(jint, nativeSetOptionString, jlong jinstance, jstring joption, jstring jvalue) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    if (!mpv_instance->mpv)
        die("mpv is not initialized");

    const char *option = env->GetStringUTFChars(joption, nullptr);
    const char *value = env->GetStringUTFChars(jvalue, nullptr);

    int result = mpv_set_option_string(mpv_instance->mpv, option, value);

    env->ReleaseStringUTFChars(joption, option);
    env->ReleaseStringUTFChars(jvalue, value);

    return result;
}

static int common_get_property(JNIEnv *env, mpv_handle* mpv, jstring jproperty, mpv_format jformat, void *output) {
    if (!mpv)
        die("get_property called but mpv is not initialized");

    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    int result = mpv_get_property(mpv, prop, jformat, output);
    if (result < 0)
        ALOGE("mpv_get_property(%s) format %d returned error %s", prop, jformat, mpv_error_string(result));
    env->ReleaseStringUTFChars(jproperty, prop);

    return result;
}

static int common_set_property(JNIEnv *env, mpv_handle* mpv, jstring jproperty, mpv_format jformat, void *jvalue) {
    if (!mpv)
        die("set_property called but mpv is not initialized");

    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    int result = mpv_set_property(mpv, prop, jformat, jvalue);
    if (result < 0)
        ALOGE("mpv_set_property(%s, %p) format %d returned error %s", prop, jvalue, jformat, mpv_error_string(result));
    env->ReleaseStringUTFChars(jproperty, prop);

    return result;
}

jni_func(jobject, nativeGetPropertyInt, jlong jinstance, jstring jproperty) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    int64_t value = 0;
    if (common_get_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_INT64, &value) < 0)
        return nullptr;
    return env->NewObject(java_Integer, java_Integer_init, (jint)value);
}

jni_func(jobject, nativeGetPropertyDouble, jlong jinstance, jstring jproperty) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    double value = 0;
    if (common_get_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_DOUBLE, &value) < 0)
        return nullptr;
    return env->NewObject(java_Double, java_Double_init, (jdouble)value);
}

jni_func(jobject, nativeGetPropertyBoolean, jlong jinstance, jstring jproperty) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    int value = 0;
    if (common_get_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_FLAG, &value) < 0)
        return nullptr;
    return env->NewObject(java_Boolean, java_Boolean_init, (jboolean)value);
}

jni_func(jstring, nativeGetPropertyString, jlong jinstance, jstring jproperty) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    char *value;
    if (common_get_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_STRING, &value) < 0)
        return nullptr;
    jstring jvalue = env->NewStringUTF(value);
    mpv_free(value);
    return jvalue;
}

jni_func(void, nativeSetPropertyInt, jlong jinstance, jstring jproperty, jobject jvalue) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    int64_t value = env->CallIntMethod(jvalue, java_Integer_intValue);
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_INT64, &value);
}

jni_func(void, nativeSetPropertyDouble, jlong jinstance, jstring jproperty, jobject jvalue) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    double value = env->CallDoubleMethod(jvalue, java_Double_doubleValue);
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_DOUBLE, &value);
}

jni_func(void, nativeSetPropertyBoolean, jlong jinstance, jstring jproperty, jobject jvalue) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    int value = env->CallBooleanMethod(jvalue, java_Boolean_booleanValue);
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_FLAG, &value);
}

jni_func(void, nativeSetPropertyString, jlong jinstance, jstring jproperty, jstring jvalue) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    const char *value = env->GetStringUTFChars(jvalue, nullptr);
    common_set_property(env, mpv_instance->mpv, jproperty, MPV_FORMAT_STRING, &value);
    env->ReleaseStringUTFChars(jvalue, value);
}

jni_func(void, nativeObserveProperty, jlong jinstance, jstring jproperty, jint jformat) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(jinstance);
    if (!mpv_instance->mpv)
        die("mpv is not initialized");
    const char *prop = env->GetStringUTFChars(jproperty, nullptr);
    mpv_observe_property(mpv_instance->mpv, 0, prop, (mpv_format)jformat);
    env->ReleaseStringUTFChars(jproperty, prop);
}