#pragma once

#include <android/log.h>

#define LOG_TAG "libmpv-android-native"

#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__)
#define ALOGW(...) __android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__)