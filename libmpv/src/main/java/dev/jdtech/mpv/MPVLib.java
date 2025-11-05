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

    private static volatile Boolean nativeLoadComplete = false;
    private final List<EventObserver> observers = new ArrayList<>();
    private final List<LogObserver> log_observers = new ArrayList<>();

    private boolean swallowExceptions;

    private long nativeInstance;
    private boolean created;
    private boolean inited;
    private boolean destroyed;

    public static void loadNativeLibraries() {
        if (!nativeLoadComplete) {
            System.loadLibrary("mpv");
            System.loadLibrary("player");
            nativeLoadComplete = true;
        }
    }

    public MPVLib(boolean swallow) {
        swallowExceptions = swallow;
        loadNativeLibraries();
        nativeInstance = 0;
    }

    public void create(Context appctx) {
        if (created) {
            return;
        }
        try {
            nativeInstance = nativeCreate(this, appctx);
            created = true;

        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native long nativeCreate(MPVLib thiz, Context appctx);

    public void init() {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeInit(nativeInstance);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeInit(long instance);

    public void destroy() {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeDestroy(nativeInstance);
            nativeInstance = 0;
            destroyed = true;
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeDestroy(long instance);

    public void attachSurface(Surface surface) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeAttachSurface(nativeInstance, surface);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeAttachSurface(long instance, Surface surface);

    public void detachSurface() {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeDetachSurface(nativeInstance);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeDetachSurface(long instance);

    public void command(@NonNull String[] cmd) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeCommand(nativeInstance, cmd);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeCommand(long instance, @NonNull String[] cmd);

    public int setOptionString(@NonNull String name, @NonNull String value) {
        if (!created || destroyed) {
            return -1;
        }
        try {
            return nativeSetOptionString(nativeInstance, name, value);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
            return -1;
        }
    }

    private native int nativeSetOptionString(long instance, @NonNull String name, @NonNull String value);

    public Integer getPropertyInt(@NonNull String property) {
        if (!created || destroyed) {
            return null;
        }
        try {
            return nativeGetPropertyInt(nativeInstance, property);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
            return null;
        }
    }

    private native Integer nativeGetPropertyInt(long instance, @NonNull String property);

    public void setPropertyInt(@NonNull String property, @NonNull Integer value) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeSetPropertyInt(nativeInstance, property, value);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native int nativeSetPropertyInt(long instance, @NonNull String property, @NonNull Integer value);

    public Double getPropertyDouble(@NonNull String property) {
        if (!created || destroyed) {
            return null;
        }
        try {
            return nativeGetPropertyDouble(nativeInstance, property);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
            return null;
        }
    }

    private native Double nativeGetPropertyDouble(long instance, @NonNull String property);

    public void setPropertyDouble(@NonNull String property, @NonNull Double value) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeSetPropertyDouble(nativeInstance, property, value);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeSetPropertyDouble(long instance, @NonNull String property, @NonNull Double value);

    public Boolean getPropertyBoolean(@NonNull String property) {
        if (!created || destroyed) {
            return null;
        }
        try {
            return nativeGetPropertyBoolean(nativeInstance, property);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
            return null;
        }
    }

    private native Boolean nativeGetPropertyBoolean(long instance, @NonNull String property);

    public void setPropertyBoolean(@NonNull String property, @NonNull Boolean value) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeSetPropertyBoolean(nativeInstance, property, value);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeSetPropertyBoolean(long instance, @NonNull String property, @NonNull Boolean value);

    public String getPropertyString(@NonNull String property) {
        if (!created || destroyed) {
            return null;
        }
        try {
            return nativeGetPropertyString(nativeInstance, property);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
            return null;
        }
    }

    private native String nativeGetPropertyString(long instance, @NonNull String property);

    public void setPropertyString(@NonNull String property, @NonNull String value) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeSetPropertyString(nativeInstance, property, value);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeSetPropertyString(long instance, @NonNull String property, @NonNull String value);

    public void observeProperty(@NonNull String property, @Format int format) {
        if (!created || destroyed) {
            return;
        }
        try {
            nativeObserveProperty(nativeInstance, property, format);
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    private native void nativeObserveProperty(long instance, @NonNull String property, @Format int format);

    public void addObserver(EventObserver o) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                observers.add(o);
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void removeObserver(EventObserver o) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                observers.remove(o);
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void removeObservers() {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                observers.clear();
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void eventProperty(String property, long value) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void eventProperty(String property, double value) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void eventProperty(String property, boolean value) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void eventProperty(String property, String value) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void eventProperty(String property) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void event(@Event int eventId) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.event(eventId);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void addLogObserver(LogObserver o) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (log_observers) {
                log_observers.add(o);
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void removeLogObserver(LogObserver o) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (log_observers) {
                log_observers.remove(o);
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void removeLogObservers() {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (log_observers) {
                log_observers.clear();
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
            }
        }
    }

    public void logMessage(String prefix, @LogLevel int level, String text) {
        if (!created || destroyed) {
            return;
        }
        try {
            synchronized (log_observers) {
                for (LogObserver o : log_observers) {
                    o.logMessage(prefix, level, text);
                }
            }
        } catch (Exception e) {
            if (!swallowExceptions) {
                throw e;
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
