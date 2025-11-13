#include <jni.h>
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

    jobject globalSurface = env->NewGlobalRef(surface);
    if (!globalSurface) {
        throwJavaException(env, "nativeAttachSurface -> failed to create global ref for Surface");
        return;
    }
    mpv_instance->surface = globalSurface;

    int64_t surface_ptr = reinterpret_cast<int64_t>(globalSurface);
    int r = mpv_set_option(mpv_instance->mpv, "android-surface", MPV_FORMAT_INT64, &surface_ptr);
    if (r < 0) {
        ALOGE("nativeAttachSurface -> mpv_set_option(android-surface) failed: %d", r);
        env->DeleteGlobalRef(globalSurface);
        mpv_instance->surface = nullptr;
        throwJavaException(env, "nativeAttachSurface -> mpv_set_option(android-surface) failed");
    }
}

jni_func(void, nativeDetachSurface, jlong instance) {
    auto mpv_instance = reinterpret_cast<MPVInstance*>(instance);
    if (!mpv_instance || !mpv_instance->mpv) {
        ALOGE("nativeDetachSurface -> mpv is not initialized");
        return;
    }

    int64_t zero = 0;
    mpv_set_option(mpv_instance->mpv, "android-surface", MPV_FORMAT_INT64, &zero);

    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }
}
