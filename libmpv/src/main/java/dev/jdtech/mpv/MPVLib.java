package dev.jdtech.mpv;

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
    private final List<EventObserver> observers = new ArrayList<>();
    private final List<LogObserver> log_observers = new ArrayList<>();
    private static Boolean nativeLoadComplete = false;

    private interface Supplier<T> {

        T get();
    }

    private static <T> T safeCall(Supplier<T> action, T fallback) {
        try {
            return action.get();
        } catch (Throwable t) {
            return fallback;
        }
    }

    private static void safeRun(Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
        }
    }

    public static void loadNativeLibraries() {
        if (!nativeLoadComplete) {
            safeRun(() -> {
                System.loadLibrary("mpv");
                System.loadLibrary("player");
                nativeLoadComplete = true;
            });
        }
    }

    public MPVLib() {
        loadNativeLibraries();
        nativeInstance = 0;
    }

    public void create(Context appctx) {
        nativeInstance = safeCall(() -> nativeCreate(this, appctx), 0L);
    }

    private native long nativeCreate(MPVLib thiz, Context appctx);

    public void init() {
        safeRun(() -> nativeInit(nativeInstance));
    }

    private native void nativeInit(long instance);

    public void destroy() {
        safeRun(() -> nativeDestroy(nativeInstance));
        nativeInstance = 0;
    }

    private native void nativeDestroy(long instance);

    public void attachSurface(Surface surface) {
        safeRun(() -> nativeAttachSurface(nativeInstance, surface));
    }

    private native void nativeAttachSurface(long instance, Surface surface);

    public void detachSurface() {
        safeRun(() -> nativeDetachSurface(nativeInstance));
    }

    private native void nativeDetachSurface(long instance);

    public void command(@NonNull String[] cmd) {
        safeRun(() -> nativeCommand(nativeInstance, cmd));
    }

    private native void nativeCommand(long instance, @NonNull String[] cmd);

    public int setOptionString(@NonNull String name, @NonNull String value) {
        return safeCall(() -> nativeSetOptionString(nativeInstance, name, value), 0);
    }

    private native int nativeSetOptionString(long instance, @NonNull String name, @NonNull String value);

    public Integer getPropertyInt(@NonNull String property) {
        return safeCall(() -> nativeGetPropertyInt(nativeInstance, property), null);
    }

    private native int nativeGetPropertyInt(long instance, @NonNull String property);

    public void setPropertyInt(@NonNull String property, @NonNull Integer value) {
        safeRun(() -> nativeSetPropertyInt(nativeInstance, property, value));
    }

    private native int nativeSetPropertyInt(long instance, @NonNull String property, @NonNull Integer value);

    public Double getPropertyDouble(@NonNull String property) {
        return safeCall(() -> nativeGetPropertyDouble(nativeInstance, property), null);
    }

    private native Double nativeGetPropertyDouble(long instance, @NonNull String property);

    public void setPropertyDouble(@NonNull String property, @NonNull Double value) {
        safeRun(() -> nativeSetPropertyDouble(nativeInstance, property, value));
    }

    private native void nativeSetPropertyDouble(long instance, @NonNull String property, @NonNull Double value);

    public Boolean getPropertyBoolean(@NonNull String property) {
        return safeCall(() -> nativeGetPropertyBoolean(nativeInstance, property), null);
    }

    private native Boolean nativeGetPropertyBoolean(long instance, @NonNull String property);

    public void setPropertyBoolean(@NonNull String property, @NonNull Boolean value) {
        safeRun(() -> nativeSetPropertyBoolean(nativeInstance, property, value));
    }

    private native void nativeSetPropertyBoolean(long instance, @NonNull String property, @NonNull Boolean value);

    public String getPropertyString(@NonNull String property) {
        return safeCall(() -> nativeGetPropertyString(nativeInstance, property), null);
    }

    private native String nativeGetPropertyString(long instance, @NonNull String property);

    public void setPropertyString(@NonNull String property, @NonNull String value) {
        safeRun(() -> nativeSetPropertyString(nativeInstance, property, value));
    }

    private native void nativeSetPropertyString(long instance, @NonNull String property, @NonNull String value);

    public void observeProperty(@NonNull String property, @Format int format) {
        safeRun(() -> nativeObserveProperty(nativeInstance, property, format));
    }

    private native void nativeObserveProperty(long instance, @NonNull String property, @Format int format);

    public void addObserver(EventObserver o) {
        synchronized (observers) {
            observers.add(o);
        }
    }

    public void removeObserver(EventObserver o) {
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
            for (EventObserver o : observers) {
                safeRun(() -> o.eventProperty(property, value));
            }
        }
    }

    public void eventProperty(String property, double value) {
        synchronized (observers) {
            for (EventObserver o : observers) {
                safeRun(() -> o.eventProperty(property, value));
            }
        }
    }

    public void eventProperty(String property, boolean value) {
        synchronized (observers) {
            for (EventObserver o : observers) {
                safeRun(() -> o.eventProperty(property, value));
            }
        }
    }

    public void eventProperty(String property, String value) {
        synchronized (observers) {
            for (EventObserver o : observers) {
                safeRun(() -> o.eventProperty(property, value));
            }
        }
    }

    public void eventProperty(String property) {
        synchronized (observers) {
            for (EventObserver o : observers) {
                safeRun(() -> o.eventProperty(property));
            }
        }
    }

    public void event(@Event int eventId) {
        synchronized (observers) {
            for (EventObserver o : observers) {
                safeRun(() -> o.event(eventId));
            }
        }
    }

    public void addLogObserver(LogObserver o) {
        synchronized (log_observers) {
            log_observers.add(o);
        }
    }

    public void removeLogObserver(LogObserver o) {
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
            for (LogObserver o : log_observers) {
                safeRun(() -> o.logMessage(prefix, level, text));
            }
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
