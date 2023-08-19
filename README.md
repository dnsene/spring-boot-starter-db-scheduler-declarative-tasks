# spring-boot-starter-kagkarlsson-db-scheduler


This library provide is a less invasive integration of the awesome persistent task library  [db-scheduler](https://github.com/kagkarlsson/db-scheduler) with spring-boot
compared to the [original spring boot starter](https://github.com/kagkarlsson/db-scheduler#spring-boot-usage) that is provided by the official maintainers.

## Maven Dependency

````xml
  <dependency>
    <groupId>io.github.dnsene</groupId>
    <artifactId>spring-boot-starter-kagkarlsson-db-scheduler</artifactId>
	<version>....</version>
  </dependency>
````

## Usage

### Defining a task
A task is a bean's method annotated with `@Task('name')`.  
It is identified by a name defined through this annotation.  
It can be parameterized and in that case, the parameter must be serializable.  
In the example below we declare two tasks named `simpleTask` and `parametizedTask`.   

````java

import io.github.dnsene.kagkarlssondbscheduler.api.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class AService {

    @Task("simpleTask")
    public void mySimpleTask(){
      log.info("Logging from task : simpleTask");
   }

    @Task("parameterizedTask")
    public void myParametizedTask(DomainObject domainObject){
      log.info("Logging from parameterizedTask with param {}" , domainObject.toString());
    }
    
}
````


### Schedule a one-time execution of a task
Task execution scheduling is done through the interface `PersistentTaskScheduler`.

```java
import io.github.dnsene.kagkarlssondbscheduler.api.Task;
import org.springframework.stereotype.Component;
import io.github.dnsene.kagkarlssondbscheduler.api.TaskScheduler;
import java.time.LocalDateTime;

@Component
public class AnotherService {

    @Autowired
    private TaskScheduler taskScheduler;

    public void domainMethod1() {
        ....
        String taskInstanceId = taskScheduler.schedule("simpleTask", LocalDateTime.parse("2023-03-01T20:00:00.00"));
        ....
    }


    public void domainMethod2() {
        ....
        DomainObject domainObject = DomainObject.of("Hello");
        String taskInstanceId = taskScheduler.schedule("parametizedTask", LocalDateTime.parse("2023-03-01T20:00:00.00"), domainObject);
        .....
    }
    
   ...
}
```

Each method of`TaskScheduler` that schedules the execution of a task returns an identifier (the task's execution id).  
This identifier can be used later either to cancel the task, reschedule it or check its status.


### Schedule a one-time and immediate execution of a task
It is possible to schedule the execution of a task so that it is executed immediately.  
To do this, you must use the `TaskScheduler.startNow` method.  
Example : 
````java
   public void domainMethod1() {
        ....
        String taskInstanceId = taskScheduler.startNow("simpleTask");
        ....
    }
    
   public void domainMethod2() {
        ....
        DomainObject domainObject = DomainObject.of("Hello");
        String taskInstanceId = taskScheduler.startNow("parametizedTask", domainObject);
        .....
    }
````

### Schedule a repeated execution of a task
In the example below, the two tasks are scheduled to run every 10 minutes

```java
import io.github.dnsene.kagkarlssondbscheduler.api.Task;
import io.github.dnsene.kagkarlssondbscheduler.api.FixedDelay;
import org.springframework.stereotype.Component;
import io.github.dnsene.persistenttasks.TaskScheduler;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class AnotherService {

    @Autowired
    private TaskScheduler taskScheduler;

    public void domainMethod1() {
        ....
        String taskInstanceId = taskScheduler.schedule("simpleTask", FixedDelay.ofMinutes(10));
        ....
    }


    public void domainMethod2() {
        ....
        DomainObject domainObject = DomainObject.of("Hello");
        String taskInstanceId = taskScheduler.schedule("parameterizedTask",  FixedDelay.ofMinutes(10), domainObject);
        .....
    }
    
   ...
}
```

It is also possible to define the execution delay through a *cron expression*.   
Example :
````java
    public void domainMethod1() {
        ....
        String taskInstanceId = taskScheduler.schedule("simpleTask", "*/15 * * * * *");
        ....
        }


    public void domainMethod2() {
        ....
        DomainObject domainObject = DomainObject.of("Hello");
        String taskInstanceId = taskScheduler.schedule("parameterizedTask",  "*/15 * * * * *", domainObject);
        .....
        }
````

### Schedule statically, a repeated execution of a task
To do that, simply annotate the task's method directly with `@RunOnFixedDelay` or `@RunOnChronSchedule`.  
Example:

````java

import io.github.dnsene.kagkarlssondbscheduler.api.Task;
import io.github.dnsene.kagkarlssondbscheduler.api.RunOnFixedDelay;
import io.github.dnsene.kagkarlssondbscheduler.api.RunOnCronSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class AService {

    @Task("simpleTask")
    @RunOnFixedDelay(value = 10, unit = ChronoUnit.SECONDS)
    public void mySimpleTask(){
      log.info("Logging from task : simpleTask");
   }

    @Task("simpleTask2")
    @RunOnChronSchedule("*/15 * * * * *")
    public void mySimpleTask2(){
        log.info("Logging from task : simpleTask2");
    }
    
}
````
With this method, an execution of the task is programmed at the start of the application.  
Note that it is impossible to statically schedule the execution of a parameterized task.

### CONFIGURATION

### MONITORING

### ERROR HANDLING
