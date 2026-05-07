package io.eventuate.util.test.async;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Eventually {

    public static void eventually(String message, Runnable body) {
        eventually(message, 20, 500, TimeUnit.MILLISECONDS, body);
    }

    public static void eventually(Runnable body) {
        eventually("eventually", body);
    }

    public static void eventually(String message, int iterations, long timeout, TimeUnit unit, Runnable body) {
        for (int i = 0; i < iterations; i++) {
            try {
                body.run();
                return;
            } catch (Throwable e) {
                if (i == iterations - 1) {
                    throw new RuntimeException(message + ": failed after " + iterations + " iterations", e);
                }
                try {
                    unit.sleep(timeout);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    public static <T> T eventuallyReturning(Supplier<T> body) {
        return eventuallyReturning("eventuallyReturning", 20, 500, TimeUnit.MILLISECONDS, body);
    }

    public static <T> T eventuallyReturning(String message, int iterations, long timeout, TimeUnit unit, Supplier<T> body) {
        for (int i = 0; i < iterations; i++) {
            try {
                return body.get();
            } catch (Throwable e) {
                if (i == iterations - 1) {
                    throw new RuntimeException(message + ": failed after " + iterations + " iterations", e);
                }
                try {
                    unit.sleep(timeout);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new RuntimeException("Should not reach here");
    }
}
