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
    private static volatile Boolean nativeLoadComplete = false;

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
    }

    public void create(Context appctx) {
        try {
            nativeInstance = nativeCreate(this, appctx);
        } catch (Throwable swallow) {
        }
    }

    private native long nativeCreate(MPVLib thiz, Context appctx);

    public void init() {
        try {
            nativeInit(nativeInstance);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeInit(long instance);

    public void destroy() {
        try {
            nativeDestroy(nativeInstance);
            nativeInstance = 0;
        } catch (Throwable swallow) {
        }
    }

    private native void nativeDestroy(long instance);

    public void attachSurface(Surface surface) {
        try {
            nativeAttachSurface(nativeInstance, surface);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeAttachSurface(long instance, Surface surface);

    public void detachSurface() {
        try {
            nativeDetachSurface(nativeInstance);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeDetachSurface(long instance);

    public void command(@NonNull String[] cmd) {
        try {
            nativeCommand(nativeInstance, cmd);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeCommand(long instance, @NonNull String[] cmd);

    public int setOptionString(@NonNull String name, @NonNull String value) {
        try {
            return nativeSetOptionString(nativeInstance, name, value);
        } catch (Throwable swallow) {
            return -1;
        }
    }

    private native int nativeSetOptionString(long instance, @NonNull String name, @NonNull String value);

    public Integer getPropertyInt(@NonNull String property) {
        try {
            return nativeGetPropertyInt(nativeInstance, property);
        } catch (Throwable swallow) {
            return null;
        }

    }

    private native Integer nativeGetPropertyInt(long instance, @NonNull String property);

    public void setPropertyInt(@NonNull String property, @NonNull Integer value) {
        try {
            nativeSetPropertyInt(nativeInstance, property, value);
        } catch (Throwable swallow) {
        }
    }

    private native int nativeSetPropertyInt(long instance, @NonNull String property, @NonNull Integer value);

    public Double getPropertyDouble(@NonNull String property) {
        try {
            return nativeGetPropertyDouble(nativeInstance, property);
        } catch (Throwable swallow) {
            return null;
        }
    }

    private native Double nativeGetPropertyDouble(long instance, @NonNull String property);

    public void setPropertyDouble(@NonNull String property, @NonNull Double value) {
        try {
            nativeSetPropertyDouble(nativeInstance, property, value);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeSetPropertyDouble(long instance, @NonNull String property, @NonNull Double value);

    public Boolean getPropertyBoolean(@NonNull String property) {
        try {
            return nativeGetPropertyBoolean(nativeInstance, property);
        } catch (Throwable swallow) {
            return null;
        }
    }

    private native Boolean nativeGetPropertyBoolean(long instance, @NonNull String property);

    public void setPropertyBoolean(@NonNull String property, @NonNull Boolean value) {
        try {
            nativeSetPropertyBoolean(nativeInstance, property, value);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeSetPropertyBoolean(long instance, @NonNull String property, @NonNull Boolean value);

    public String getPropertyString(@NonNull String property) {
        try {
            return nativeGetPropertyString(nativeInstance, property);
        } catch (Throwable swallow) {
            return null;
        }
    }

    private native String nativeGetPropertyString(long instance, @NonNull String property);

    public void setPropertyString(@NonNull String property, @NonNull String value) {
        try {
            nativeSetPropertyString(nativeInstance, property, value);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeSetPropertyString(long instance, @NonNull String property, @NonNull String value);

    public void observeProperty(@NonNull String property, @Format int format) {
        try {
            nativeObserveProperty(nativeInstance, property, format);
        } catch (Throwable swallow) {
        }
    }

    private native void nativeObserveProperty(long instance, @NonNull String property, @Format int format);

    public void addObserver(EventObserver o) {
        try {
            synchronized (observers) {
                observers.add(o);
            }
        } catch (Throwable swallow) {
        }
    }

    public void removeObserver(EventObserver o) {
        try {
            synchronized (observers) {
                observers.remove(o);
            }
        } catch (Throwable swallow) {
        }
    }

    public void removeObservers() {
        try {
            synchronized (observers) {
                observers.clear();
            }
        } catch (Throwable swallow) {
        }
    }

    public void eventProperty(String property, long value) {
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Throwable swallow) {
        }
    }

    public void eventProperty(String property, double value) {
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Throwable swallow) {
        }
    }

    public void eventProperty(String property, boolean value) {
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Throwable swallow) {
        }
    }

    public void eventProperty(String property, String value) {
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property, value);
                }
            }
        } catch (Throwable swallow) {
        }
    }

    public void eventProperty(String property) {
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.eventProperty(property);
                }
            }
        } catch (Throwable swallow) {
        }
    }

    public void event(@Event int eventId) {
        try {
            synchronized (observers) {
                for (EventObserver o : observers) {
                    o.event(eventId);
                }
            }
        } catch (Throwable swallow) {
        }
    }

    public void addLogObserver(LogObserver o) {
        try {
            synchronized (log_observers) {
                log_observers.add(o);
            }
        } catch (Throwable swallow) {
        }
    }

    public void removeLogObserver(LogObserver o) {
        try {
            synchronized (log_observers) {
                log_observers.remove(o);
            }
        } catch (Throwable swallow) {
        }
    }

    public void removeLogObservers() {
        try {
            synchronized (log_observers) {
                log_observers.clear();
            }
        } catch (Throwable swallow) {
        }
    }

    public void logMessage(String prefix, @LogLevel int level, String text) {
        try {
            synchronized (log_observers) {
                for (LogObserver o : log_observers) {
                    o.logMessage(prefix, level, text);
                }
            }
        } catch (Throwable swallow) {
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
