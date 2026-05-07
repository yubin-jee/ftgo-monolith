package io.eventuate.util.test.async;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Eventually {

    private static final int DEFAULT_ITERATIONS = 20;
    private static final long DEFAULT_TIMEOUT = 500;
    private static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

    private Eventually() {
    }

    public static void eventually(Runnable body) {
        eventually("eventually", body);
    }

    public static void eventually(String message, Runnable body) {
        eventually(message, DEFAULT_ITERATIONS, DEFAULT_TIMEOUT, DEFAULT_UNIT, body);
    }

    public static void eventually(String message, int iterations, long timeout, TimeUnit unit, Runnable body) {
        eventuallyReturning(message, iterations, timeout, unit, () -> {
            body.run();
            return null;
        });
    }

    public static <T> T eventuallyReturning(Supplier<T> body) {
        return eventuallyReturning("eventuallyReturning", DEFAULT_ITERATIONS, DEFAULT_TIMEOUT, DEFAULT_UNIT, body);
    }

    public static <T> T eventuallyReturning(String message, int iterations, long timeout, TimeUnit unit, Supplier<T> body) {
        Throwable lastError = null;
        for (int i = 0; i < iterations; i++) {
            try {
                return body.get();
            } catch (Throwable e) {
                lastError = e;
                if (i < iterations - 1) {
                    sleepQuietly(timeout, unit);
                }
            }
        }
        throw new RuntimeException(message + ": failed after " + iterations + " iterations", lastError);
    }

    private static void sleepQuietly(long timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }
}
