package io.github.dnsene.dbscheduler.declarativetasks.boot;


import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstanceId;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.PlainScheduleAndData;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTaskWithPersistentSchedule;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import io.github.dnsene.dbscheduler.declarativetasks.api.FixedDelay;
import io.github.dnsene.dbscheduler.declarativetasks.api.TaskException;
import io.github.dnsene.dbscheduler.declarativetasks.api.TaskScheduler;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.onetime.OneTimeTaskBeanManager;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringdynamic.RecurringDynamicTaskBeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.Function;



public class TaskSchedulerImpl implements TaskScheduler {


    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerImpl.class);

    private final Scheduler scheduler;
    private final OneTimeTaskBeanManager oneTimeTaskBeanManager;
    private final RecurringDynamicTaskBeanManager recurringDynamicTaskBeanManager;

    public TaskSchedulerImpl(Scheduler scheduler, OneTimeTaskBeanManager oneTimeTaskBeanManager, RecurringDynamicTaskBeanManager recurringDynamicTaskBeanManager) {
        this.scheduler = scheduler;
        this.oneTimeTaskBeanManager = oneTimeTaskBeanManager;
        this.recurringDynamicTaskBeanManager = recurringDynamicTaskBeanManager;
    }

    @Override
    public String startNow(String taskName) {
        String taskId =  schedule(taskName,  LocalDateTime.now());
        triggerCheckForDueExecutionsIfInTransaction();
        return taskId;
    }

    @Override
    public String startNow(String taskName, Object taskData) {
        String taskId =  schedule(taskName,  LocalDateTime.now(), taskData);
        triggerCheckForDueExecutionsIfInTransaction();
        return taskId;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String schedule(String taskName, LocalDateTime executionTime) {
        OneTimeTask oneTimeTask = (OneTimeTask) getTaskBean(oneTimeTaskBeanManager::getBean, taskName);
        String taskInstanceId = generateTaskInstanceId();
        scheduler.schedule(oneTimeTask.instance(taskInstanceId), executionTime.atZone(ZoneId.systemDefault()).toInstant());
        log.info("New task instance (name: {}, id: {}) scheduled at {}", taskName, taskInstanceId, executionTime);
        return taskInstanceId;
    }


    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String schedule(String taskName, LocalDateTime executionTime, Object taskData) {
        OneTimeTask oneTimeTask = (OneTimeTask) getTaskBean(oneTimeTaskBeanManager::getBean, taskName);
        String taskInstanceId = generateTaskInstanceId();
        scheduler.schedule(oneTimeTask.instance(taskInstanceId, taskData), executionTime.atZone(ZoneId.systemDefault()).toInstant());
        log.info("New task instance (name: {}, id: {}) scheduled at {}", taskName, taskInstanceId, executionTime);
        return taskInstanceId;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public String schedule(String taskName, FixedDelay fixedDelay) {
        RecurringTaskWithPersistentSchedule<PlainScheduleAndData> task = (RecurringTaskWithPersistentSchedule<PlainScheduleAndData>) getTaskBean(recurringDynamicTaskBeanManager::getBean, taskName);
        String taskInstanceId = generateTaskInstanceId();
        PlainScheduleAndData data = new PlainScheduleAndData(Schedules.fixedDelay(fixedDelay.getDuration()));
        scheduler.schedule(task.schedulableInstance(taskInstanceId, data));
        log.info("New task instance (name: {}, id: {}) scheduled at fixed delay {}", taskName, taskInstanceId, fixedDelay);
        return taskInstanceId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String schedule(String taskName, FixedDelay fixedDelay, Object taskData) {
        RecurringTaskWithPersistentSchedule<PlainScheduleAndData> task = (RecurringTaskWithPersistentSchedule<PlainScheduleAndData>) getTaskBean(recurringDynamicTaskBeanManager::getBean, taskName);
        String taskInstanceId = generateTaskInstanceId();
        PlainScheduleAndData data = new PlainScheduleAndData(Schedules.fixedDelay(fixedDelay.getDuration()), taskData);
        scheduler.schedule(task.schedulableInstance(taskInstanceId, data));
        log.info("New task instance (name: {}, id: {}) scheduled at fixed delay {}", taskName, taskInstanceId, fixedDelay);
        return taskInstanceId;
    }


    @Override
    @SuppressWarnings("unchecked")
    public String schedule(String taskName, String cronExpression) {
        RecurringTaskWithPersistentSchedule<PlainScheduleAndData> task = (RecurringTaskWithPersistentSchedule<PlainScheduleAndData>) getTaskBean(recurringDynamicTaskBeanManager::getBean, taskName);
        String taskInstanceId = generateTaskInstanceId();
        PlainScheduleAndData data = new PlainScheduleAndData(Schedules.cron(cronExpression));
        scheduler.schedule(task.schedulableInstance(taskInstanceId, data));
        log.info("New task instance (name: {}, id: {}) scheduled with cron schedule {}", taskName, taskInstanceId, cronExpression);
        return taskInstanceId;
    }


    @Override
    @SuppressWarnings("unchecked")
    public String schedule(String taskName, String cronExpression, Object taskData) {
        RecurringTaskWithPersistentSchedule<PlainScheduleAndData> task = (RecurringTaskWithPersistentSchedule<PlainScheduleAndData>) getTaskBean(recurringDynamicTaskBeanManager::getBean, taskName);
        String taskInstanceId = generateTaskInstanceId();
        PlainScheduleAndData data = new PlainScheduleAndData(Schedules.cron(cronExpression), taskData);
        scheduler.schedule(task.schedulableInstance(taskInstanceId, data));
        log.info("New task instance (name: {}, id: {}) scheduled with cron schedule {}", taskName, taskInstanceId, cronExpression);
        return taskInstanceId;
    }

    @Override
    public void cancel(String taskName, String taskInstanceId) {
        TaskInstanceId id = TaskInstanceId.of(taskName, taskInstanceId);
        try {
            scheduler.cancel(id);
        }catch (Exception e){
            throw new TaskException(e);
        }
        log.info("Task instance (name: {}, id: {}) canceled", taskName, taskInstanceId);
    }

    @Override
    public void reschedule(String taskName, String taskInstanceId, LocalDateTime newExecutionTime) {
        TaskInstanceId id = TaskInstanceId.of(taskName, taskInstanceId);
        try {
            scheduler.reschedule(id, newExecutionTime.atZone(ZoneId.systemDefault()).toInstant());
        }catch (Exception e){
            throw new TaskException(e);
        }
        log.info("Task instance (name: {}, id: {}) rescheduled at {}", taskName, taskInstanceId, newExecutionTime);
    }


    private String generateTaskInstanceId() {
        return String.valueOf(UUID.randomUUID()).split("-")[4];
    }

    private void triggerCheckForDueExecutionsIfInTransaction() {
        if(TransactionSynchronizationManager.isActualTransactionActive()){
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
                @Override
                public void afterCommit() {
                    scheduler.triggerCheckForDueExecutions();
                }
            });
        }
    }

    public Task<?> getTaskBean(Function<String,Task<?>> taskBeanSupplier, String taskName){
        try {
            return taskBeanSupplier.apply(taskName);
        }catch (NoSuchBeanDefinitionException e){
            throw new TaskException("Task not found (task name: "+taskName+")");
        }
    }
}

