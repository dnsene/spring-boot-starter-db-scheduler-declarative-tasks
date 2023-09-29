package io.github.dnsene.dbscheduler.declarativetasks.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RunOnFixedDelay {

    long value();

    ChronoUnit unit() default ChronoUnit.MILLIS;

}
