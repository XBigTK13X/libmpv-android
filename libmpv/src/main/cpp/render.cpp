
#include <jni.h>

#include <mpv/client.h>

#include "jni_utils.h"
#include "log.h"
#include "globals.h"

extern "C" {
    jni_func(void, nativeAttachSurface, jlong instance, jobject surface);
    jni_func(void, nativeDetachSurface, jlong instance);
}

static inline MPVInstance* get_instance(jlong handle) {
    if (handle == 0) return nullptr;
    return reinterpret_cast<MPVInstance*>(handle);
}

jni_func(void, nativeAttachSurface, jlong handle, jobject surface) {
    auto mpv_instance = get_instance(handle);
    if (!mpv_instance || !mpv_instance->mpv || !surface) return;
    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }
    mpv_instance->surface = env->NewGlobalRef(surface);
    int64_t wid = (int64_t)(intptr_t) mpv_instance->surface;
    mpv_set_option(mpv_instance->mpv, "wid", MPV_FORMAT_INT64, (void*) &wid);
}

jni_func(void, nativeDetachSurface, jlong handle) {
    auto mpv_instance = get_instance(handle);
    if (!mpv_instance || !mpv_instance->mpv) return;
    int64_t wid = 0;
    mpv_set_option(mpv_instance->mpv, "wid", MPV_FORMAT_INT64, (void*) &wid);

    if (mpv_instance->surface) {
        env->DeleteGlobalRef(mpv_instance->surface);
        mpv_instance->surface = nullptr;
    }
}
