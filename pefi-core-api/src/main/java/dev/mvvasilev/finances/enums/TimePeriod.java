package dev.mvvasilev.finances.enums;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public enum TimePeriod {
//    SECONDLY(Duration.of(1, ChronoUnit.SECONDS)),
//    MINUTELY(Duration.of(1, ChronoUnit.MINUTES)),
//    HOURLY(Duration.of(1, ChronoUnit.HOURS)),
    DAILY(Period.ofDays(1)),
    WEEKLY(Period.ofDays(7)),
    BIWEEKLY(Period.ofDays(14)),
    MONTHLY(Period.ofMonths(1)),
    QUARTERLY(Period.ofMonths(3)),
    YEARLY(Period.ofYears(1));

    private final Period duration;

    TimePeriod(Period duration) {
        this.duration = duration;
    }

    public Period getDuration() {
        return duration;
    }
}
