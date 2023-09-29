package io.github.dnsene.dbscheduler.declarativetasks.api;


import java.time.Duration;
import java.util.Objects;


public class FixedDelay {

    private final Duration duration;

    private FixedDelay(Duration duration) {
        this.duration = Objects.requireNonNull(duration);
    }

    public static FixedDelay of(Duration duration) {
        return new FixedDelay(duration);
    }

    public static FixedDelay ofMillis(long millis) {
        validateDuration(millis);
        return new FixedDelay(Duration.ofMillis(millis));
    }

    public static FixedDelay ofSeconds(int seconds) {
        validateDuration(seconds);
        return new FixedDelay(Duration.ofSeconds(seconds));
    }

    public static FixedDelay ofMinutes(int minutes) {
        validateDuration(minutes);
        return new FixedDelay(Duration.ofMinutes(minutes));
    }

    public static FixedDelay ofHours(int hours) {
        validateDuration(hours);
        return new FixedDelay(Duration.ofHours(hours));
    }

    private static void validateDuration(long seconds) {
        if (seconds <= 0L) {
            throw new IllegalArgumentException("argument must be greater than 0");
        }
    }

    public Duration getDuration() {
        return duration;
    }
}
