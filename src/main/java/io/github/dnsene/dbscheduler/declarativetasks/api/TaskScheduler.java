package io.github.dnsene.dbscheduler.declarativetasks.api;


import java.time.LocalDateTime;

public interface TaskScheduler {

     String startNow(String taskName);

     String startNow(String taskName, Object taskData);

     String schedule(String taskName, LocalDateTime executionTime, Object taskData);

     String schedule(String taskName, LocalDateTime executionTime);

     String schedule(String taskName, FixedDelay fixedDelay, Object taskData);

     String schedule(String taskName, FixedDelay fixedDelay);

     String schedule(String taskName, String cronExpression, Object taskData);

     String schedule(String taskName, String cronExpression);

     void cancel(String taskName, String taskInstanceId);

     void reschedule(String taskName, String taskInstanceId, LocalDateTime newExecutionTime);
}
