
#pragma once

#include <atomic>
#include <jni.h>
#include <mpv/client.h>
#include <pthread.h>

// MPV instance state shared across JNI
struct MPVInstance {
    mpv_handle *mpv;
    JavaVM *vm;
    pthread_t event_thread_id;
    std::atomic<bool> event_thread_request_exit;
    std::atomic<bool> disposed;
    jobject javaObject;         // GlobalRef to MPVLib
    bool methods_initialized;
    jobject surface;            // GlobalRef to Surface (or nullptr)
    MPVInstance() :
        mpv(nullptr),
        vm(nullptr),
        event_thread_id(0),
        event_thread_request_exit(false),
        disposed(false),
        javaObject(nullptr),
        methods_initialized(false),
        surface(nullptr) {}
};
