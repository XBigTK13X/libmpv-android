#include <jni.h>
#include <android/native_window_jni.h>
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
    if (!mpv_instance || !mpv_instance->mpv) {
        throwJavaException(env, "nativeAttachSurface -> mpv is not initialized");
        return;
    }

    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }
    if (mpv_instance->native_window) {
        ANativeWindow_release(mpv_instance->native_window);
        mpv_instance->native_window = nullptr;
    }

    mpv_instance->surface = env->NewGlobalRef(surface);
    mpv_instance->native_window = ANativeWindow_fromSurface(env, surface);
    if (!mpv_instance->native_window) {
        throwJavaException(env, "nativeAttachSurface -> failed to get ANativeWindow");
        return;
    }

    int64_t wid = reinterpret_cast<int64_t>(mpv_instance->native_window);
    mpv_set_option(mpv_instance->mpv, "wid", MPV_FORMAT_INT64, &wid);
}

jni_func(void, nativeDetachSurface, jlong instance) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance || !mpv_instance->mpv) {
        ALOGE("nativeDetachSurface -> mpv is not initialized");
        return;
    }

    int64_t wid = 0;
    mpv_set_option(mpv_instance->mpv, "wid", MPV_FORMAT_INT64, &wid);

    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }
    if (mpv_instance->native_window) {
        ANativeWindow_release(mpv_instance->native_window);
        mpv_instance->native_window = nullptr;
    }
}
