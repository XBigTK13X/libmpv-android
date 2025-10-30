package dev.jdtech.mpv;

// Wrapper for native library
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Surface;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class MPVLib {

    private long nativeInstance;
    private volatile boolean disposed;

    private final List<EventObserver> observers = new ArrayList<>();
    private final List<LogObserver> log_observers = new ArrayList<>();
    private static Boolean nativeLoadComplete = false;

    public static void loadNativeLibraries() {
        if (!nativeLoadComplete) {
            System.loadLibrary("mpv");
            System.loadLibrary("player");
            nativeLoadComplete = true;
        }
    }

    public MPVLib() {
        loadNativeLibraries();
        nativeInstance = 0;
        disposed = false;
    }

    public synchronized void create(Context appctx) {
        if (disposed || nativeInstance != 0) return;
        nativeInstance = nativeCreate(this, appctx);
    }

    private native long nativeCreate(MPVLib thiz, Context appctx);

    public synchronized void init() {
        if (disposed || nativeInstance == 0) return;
        nativeInit(nativeInstance);
    }

    private native void nativeInit(long instance);

    public synchronized void destroy() {
        if (disposed) return;
        disposed = true;
        long inst = nativeInstance;
        nativeInstance = 0;
        if (inst != 0) nativeDestroy(inst);
        observers.clear();
        log_observers.clear();
    }

    private native void nativeDestroy(long instance);

    public synchronized void attachSurface(Surface surface) {
        if (disposed || nativeInstance == 0 || surface == null) return;
        nativeAttachSurface(nativeInstance, surface);
    }

    private native void nativeAttachSurface(long instance, Surface surface);

    public synchronized void detachSurface() {
        if (disposed || nativeInstance == 0) return;
        nativeDetachSurface(nativeInstance);
    }

    private native void nativeDetachSurface(long instance);

    public synchronized void command(@NonNull String[] cmd) {
        if (disposed || nativeInstance == 0 || cmd == null) return;
        nativeCommand(nativeInstance, cmd);
    }

    private native void nativeCommand(long instance, @NonNull String[] cmd);

    public synchronized int setOptionString(@NonNull String name, @NonNull String value) {
        if (disposed || nativeInstance == 0 || name == null || value == null) return -1;
        return nativeSetOptionString(nativeInstance, name, value);
    }

    private native int nativeSetOptionString(long instance, @NonNull String name, @NonNull String value);

    public synchronized Integer getPropertyInt(@NonNull String property) {
        if (disposed || nativeInstance == 0 || property == null) return null;
        return nativeGetPropertyInt(nativeInstance, property);
    }

    private native int nativeGetPropertyInt(long instance, @NonNull String property);

    public synchronized void setPropertyInt(@NonNull String property, @NonNull Integer value) {
        if (disposed || nativeInstance == 0 || property == null || value == null) return;
        nativeSetPropertyInt(nativeInstance, property, value);
    }

    private native int nativeSetPropertyInt(long instance, @NonNull String property, @NonNull Integer value);

    public synchronized Double getPropertyDouble(@NonNull String property) {
        if (disposed || nativeInstance == 0 || property == null) return null;
        return nativeGetPropertyDouble(nativeInstance, property);
    }

    private native Double nativeGetPropertyDouble(long instance, @NonNull String property);

    public synchronized void setPropertyDouble(@NonNull String property, @NonNull Double value) {
        if (disposed || nativeInstance == 0 || property == null || value == null) return;
        nativeSetPropertyDouble(nativeInstance, property, value);
    }

    private native void nativeSetPropertyDouble(long instance, @NonNull String property, @NonNull Double value);

    public synchronized Boolean getPropertyBoolean(@NonNull String property) {
        if (disposed || nativeInstance == 0 || property == null) return null;
        return nativeGetPropertyBoolean(nativeInstance, property);
    }

    private native Boolean nativeGetPropertyBoolean(long instance, @NonNull String property);

    public synchronized void setPropertyBoolean(@NonNull String property, @NonNull Boolean value) {
        if (disposed || nativeInstance == 0 || property == null || value == null) return;
        nativeSetPropertyBoolean(nativeInstance, property, value);
    }

    private native void nativeSetPropertyBoolean(long instance, @NonNull String property, @NonNull Boolean value);

    public synchronized String getPropertyString(@NonNull String property) {
        if (disposed || nativeInstance == 0 || property == null) return null;
        return nativeGetPropertyString(nativeInstance, property);
    }

    private native String nativeGetPropertyString(long instance, @NonNull String property);

    public synchronized void setPropertyString(@NonNull String property, @NonNull String value) {
        if (disposed || nativeInstance == 0 || property == null || value == null) return;
        nativeSetPropertyString(nativeInstance, property, value);
    }

    private native void nativeSetPropertyString(long instance, @NonNull String property, @NonNull String value);

    public synchronized void observeProperty(@NonNull String property, @Format int format) {
        if (disposed || nativeInstance == 0 || property == null) return;
        nativeObserveProperty(nativeInstance, property, format);
    }

    private native void nativeObserveProperty(long instance, @NonNull String property, @Format int format);

    public void addObserver(EventObserver o) {
        if (o == null) return;
        synchronized (observers) {
            observers.add(o);
        }
    }

    public void removeObserver(EventObserver o) {
        if (o == null) return;
        synchronized (observers) {
            observers.remove(o);
        }
    }

    public void removeObservers() {
        synchronized (observers) {
            observers.clear();
        }
    }

    public void eventProperty(String property, long value) {
        synchronized (observers) {
            for (EventObserver o : observers) o.eventProperty(property, value);
        }
    }

    public void eventProperty(String property, double value) {
        synchronized (observers) {
            for (EventObserver o : observers) o.eventProperty(property, value);
        }
    }

    public void eventProperty(String property, boolean value) {
        synchronized (observers) {
            for (EventObserver o : observers) o.eventProperty(property, value);
        }
    }

    public void eventProperty(String property, String value) {
        synchronized (observers) {
            for (EventObserver o : observers) o.eventProperty(property, value);
        }
    }

    public void event(String property) {
        synchronized (observers) {
            for (EventObserver o : observers) o.eventProperty(property);
        }
    }

    public void event(@Event int eventId) {
        synchronized (observers) {
            for (EventObserver o : observers) o.event(eventId);
        }
    }

    public void addLogObserver(LogObserver o) {
        if (o == null) return;
        synchronized (log_observers) {
            log_observers.add(o);
        }
    }

    public void removeLogObserver(LogObserver o) {
        if (o == null) return;
        synchronized (log_observers) {
            log_observers.remove(o);
        }
    }

    public void removeLogObservers() {
        synchronized (log_observers) {
            log_observers.clear();
        }
    }

    public void logMessage(String prefix, @LogLevel int level, String text) {
        synchronized (log_observers) {
            for (LogObserver o : log_observers) o.logMessage(prefix, level, text);
        }
    }

    public interface EventObserver {

        void eventProperty(@NonNull String property);

        void eventProperty(@NonNull String property, long value);

        void eventProperty(@NonNull String property, double value);

        void eventProperty(@NonNull String property, boolean value);

        void eventProperty(@NonNull String property, @NonNull String value);

        void event(@Event int eventId);
    }

    public interface LogObserver {

        void logMessage(@NonNull String prefix, @LogLevel int level, @NonNull String text);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        MPV_FORMAT_NONE,
        MPV_FORMAT_STRING,
        MPV_FORMAT_OSD_STRING,
        MPV_FORMAT_FLAG,
        MPV_FORMAT_INT64,
        MPV_FORMAT_DOUBLE,
        MPV_FORMAT_NODE,
        MPV_FORMAT_NODE_ARRAY,
        MPV_FORMAT_NODE_MAP,
        MPV_FORMAT_BYTE_ARRAY
    })
    public @interface Format {
    }

    public static final int MPV_FORMAT_NONE = 0;
    public static final int MPV_FORMAT_STRING = 1;
    public static final int MPV_FORMAT_OSD_STRING = 2;
    public static final int MPV_FORMAT_FLAG = 3;
    public static final int MPV_FORMAT_INT64 = 4;
    public static final int MPV_FORMAT_DOUBLE = 5;
    public static final int MPV_FORMAT_NODE = 6;
    public static final int MPV_FORMAT_NODE_ARRAY = 7;
    public static final int MPV_FORMAT_NODE_MAP = 8;
    public static final int MPV_FORMAT_BYTE_ARRAY = 9;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        MPV_EVENT_NONE,
        MPV_EVENT_SHUTDOWN,
        MPV_EVENT_LOG_MESSAGE,
        MPV_EVENT_GET_PROPERTY_REPLY,
        MPV_EVENT_SET_PROPERTY_REPLY,
        MPV_EVENT_COMMAND_REPLY,
        MPV_EVENT_START_FILE,
        MPV_EVENT_END_FILE,
        MPV_EVENT_FILE_LOADED,
        MPV_EVENT_CLIENT_MESSAGE,
        MPV_EVENT_VIDEO_RECONFIG,
        MPV_EVENT_AUDIO_RECONFIG,
        MPV_EVENT_SEEK,
        MPV_EVENT_PLAYBACK_RESTART,
        MPV_EVENT_PROPERTY_CHANGE,
        MPV_EVENT_QUEUE_OVERFLOW,
        MPV_EVENT_HOOK
    })
    public @interface Event {
    }

    public static final int MPV_EVENT_NONE = 0;
    public static final int MPV_EVENT_SHUTDOWN = 1;
    public static final int MPV_EVENT_LOG_MESSAGE = 2;
    public static final int MPV_EVENT_GET_PROPERTY_REPLY = 3;
    public static final int MPV_EVENT_SET_PROPERTY_REPLY = 4;
    public static final int MPV_EVENT_COMMAND_REPLY = 5;
    public static final int MPV_EVENT_START_FILE = 6;
    public static final int MPV_EVENT_END_FILE = 7;
    public static final int MPV_EVENT_FILE_LOADED = 8;
    public static final int MPV_EVENT_CLIENT_MESSAGE = 16;
    public static final int MPV_EVENT_VIDEO_RECONFIG = 17;
    public static final int MPV_EVENT_AUDIO_RECONFIG = 18;
    public static final int MPV_EVENT_SEEK = 20;
    public static final int MPV_EVENT_PLAYBACK_RESTART = 21;
    public static final int MPV_EVENT_PROPERTY_CHANGE = 22;
    public static final int MPV_EVENT_QUEUE_OVERFLOW = 24;
    public static final int MPV_EVENT_HOOK = 25;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        MPV_LOG_LEVEL_NONE,
        MPV_LOG_LEVEL_FATAL,
        MPV_LOG_LEVEL_ERROR,
        MPV_LOG_LEVEL_WARN,
        MPV_LOG_LEVEL_INFO,
        MPV_LOG_LEVEL_V,
        MPV_LOG_LEVEL_DEBUG,
        MPV_LOG_LEVEL_TRACE
    })
    public @interface LogLevel {
    }

    public static final int MPV_LOG_LEVEL_NONE = 0;
    public static final int MPV_LOG_LEVEL_FATAL = 10;
    public static final int MPV_LOG_LEVEL_ERROR = 20;
    public static final int MPV_LOG_LEVEL_WARN = 30;
    public static final int MPV_LOG_LEVEL_INFO = 40;
    public static final int MPV_LOG_LEVEL_V = 50;
    public static final int MPV_LOG_LEVEL_DEBUG = 60;
    public static final int MPV_LOG_LEVEL_TRACE = 70;
}
