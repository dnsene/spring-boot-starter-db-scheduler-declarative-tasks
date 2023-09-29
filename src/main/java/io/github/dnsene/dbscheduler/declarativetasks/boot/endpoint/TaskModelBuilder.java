package io.github.dnsene.dbscheduler.declarativetasks.boot.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.dnsene.dbscheduler.declarativetasks.api.TaskException;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringdynamic.RecurringDynamicTaskBeanManager;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringstatic.RecurringStaticTaskBean;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringstatic.RecurringStaticTaskBeanManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kagkarlsson.scheduler.ScheduledExecution;
import com.github.kagkarlsson.scheduler.task.Execution;
import com.github.kagkarlsson.scheduler.task.helper.PlainScheduleAndData;
import com.github.kagkarlsson.scheduler.task.schedule.CronSchedule;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import com.github.kagkarlsson.scheduler.task.schedule.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Duration;

public class TaskModelBuilder {


    @Autowired
    private ApplicationContext context;
    

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TaskModel build(ScheduledExecution<Object> x) {
        TaskModel taskModel = new TaskModel();
        taskModel.setTaskType(getTaskType(x.getTaskInstance().getTaskName()));
        taskModel.setTaskName(getTaskName(x.getTaskInstance().getTaskName()));
        taskModel.setTaskId(x.getTaskInstance().getId());
        taskModel.setConsecutiveFailures(x.getConsecutiveFailures());
        taskModel.setPickedBy(x.getPickedBy());
        taskModel.setPicked(x.isPicked());
        taskModel.setExecutionTime(x.getExecutionTime());
        taskModel.setTaskData(getTaskData(taskModel.getTaskType(), x));
        taskModel.setSchedule(getSchedule(taskModel.getTaskType(), x));
        taskModel.setLastSuccess(x.getLastSuccess());
        taskModel.setLastFailure(x.getLastFailure());
        return taskModel;
    }


    private String getSchedule(TaskModel.Type type, ScheduledExecution<Object> scheduledExecution) {
        switch (type){
            case onetime: return null;
            case recurring_static: {
                RecurringStaticTaskBean taskBean = (RecurringStaticTaskBean) context.getBean(scheduledExecution.getTaskInstance().getTaskName());
                Schedule schedule = (Schedule) getFieldValue(taskBean, RecurringStaticTaskBean.class, "schedule");
                return mapScheduleToString(schedule);
            }
            case recurring_dynamic: {
                Schedule schedule = ((PlainScheduleAndData) scheduledExecution.getData()).getSchedule();
                return mapScheduleToString(schedule);
            }
            default: throw new TaskException("unknown task type");
        }
    }

    private String mapScheduleToString(Schedule schedule) {
        if(schedule instanceof CronSchedule)
            return ((CronSchedule) schedule).getPattern();
        if(schedule instanceof FixedDelay) {
            Duration duration = (Duration) getFieldValue(schedule, FixedDelay.class, "duration");
            return "Toutes les " + humanReadableFormat(duration);
        }
        throw new TaskException("Unmanaged type of schedule");
    }

    private Object getFieldValue( Object targetObject, Class<?> targetClass, String name) {
        Field field = ReflectionUtils.findField(targetClass, name);
        if (field == null) {
            throw new IllegalArgumentException(String.format("Could not find field '%s' on %s or target class [%s]", name, targetObject, targetClass));
        } else {
            ReflectionUtils.makeAccessible(field);
            return ReflectionUtils.getField(field, targetObject);
        }
    }


    private String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    private String getTaskName(String taskName) {
        if(taskName.endsWith(RecurringStaticTaskBeanManager.BEAN_NAME_SUFFIX)){
            return taskName.substring(0, taskName.lastIndexOf(RecurringStaticTaskBeanManager.BEAN_NAME_SUFFIX));
        }
        if(taskName.endsWith(RecurringDynamicTaskBeanManager.BEAN_NAME_SUFFIX)){
            return taskName.substring(0, taskName.lastIndexOf(RecurringDynamicTaskBeanManager.BEAN_NAME_SUFFIX));
        }
        return taskName;
    }


    private String getTaskData(TaskModel.Type type, ScheduledExecution<Object> scheduledExecution) {
        Object data;
        switch (type){
            case onetime:  {
                //Hack car ScheduledExecution.getData retourne une exception lorsque data est null
                Execution execution = (Execution) getFieldValue(scheduledExecution, ScheduledExecution.class, "execution");
                data = execution.taskInstance.getData();
                break;}
            case recurring_static: {data = null; break;}
            case recurring_dynamic: { data = ((PlainScheduleAndData)scheduledExecution.getData()).getData(); break; }
            default: throw new TaskException("unknown task type");
        }
        if(data == null){
            return "---";
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new TaskException(e);
        }

    }

    private TaskModel.Type getTaskType(String taskName) {
        if(taskName.endsWith(RecurringStaticTaskBeanManager.BEAN_NAME_SUFFIX)){
            return TaskModel.Type.recurring_static;
        }
        if(taskName.endsWith(RecurringDynamicTaskBeanManager.BEAN_NAME_SUFFIX)){
            return TaskModel.Type.recurring_dynamic;
        }
        return TaskModel.Type.onetime;
    }


}
