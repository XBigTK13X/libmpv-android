#include <jni.h>
#include <thread>
#include <mpv/client.h>

#include "jni_utils.h"
#include "log.h"
#include "globals.h"

extern "C" {
    jni_func(void, nativeAttachSurface, jlong instance, jobject surface);
    jni_func(void, nativeDetachSurface, jlong instance);
}

jni_func(void, nativeAttachSurface, jlong instance, jobject surface) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance->mpv) {
        throwJavaException(env, "nativeAttachSurface -> mpv is not initialized");
        return;
    }

    mpv_instance->surface = env->NewGlobalRef(surface);
    int64_t wid = reinterpret_cast<int64_t>(mpv_instance->surface);
    mpv_set_option(mpv_instance->mpv, "wid", MPV_FORMAT_INT64, &wid);
}

jni_func(void, nativeDetachSurface, jlong instance) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance->mpv) {
        ALOGE("nativeDetachSurface -> mpv is not initialized");
        return;
    }

    mpv_handle* mpv = mpv_instance->mpv;

    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }

    std::thread([mpv]() {
        int64_t wid = 0;
        mpv_set_option(mpv, "wid", MPV_FORMAT_INT64, &wid);
    }).detach();
}
