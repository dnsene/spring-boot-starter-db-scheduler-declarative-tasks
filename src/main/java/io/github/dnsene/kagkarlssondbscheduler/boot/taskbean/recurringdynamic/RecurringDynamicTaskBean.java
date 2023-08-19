package io.github.dnsene.kagkarlssondbscheduler.boot.taskbean.recurringdynamic;

import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;

import com.github.kagkarlsson.scheduler.task.helper.PlainScheduleAndData;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTaskWithPersistentSchedule;
import com.github.kagkarlsson.scheduler.task.helper.ScheduleAndData;
import io.github.dnsene.kagkarlssondbscheduler.api.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RecurringDynamicTaskBean extends RecurringTaskWithPersistentSchedule<PlainScheduleAndData>  {

    private static final Logger log = LoggerFactory.getLogger(RecurringDynamicTaskBean.class);

    private final String handlerBeanName;
    private final Method handlerMethod;
    private final boolean handlerMethodHasParams;
    private final ConfigurableListableBeanFactory beanFactory;

    public RecurringDynamicTaskBean(String taskName, String handlerBeanName, Method handlerMethod, ConfigurableListableBeanFactory beanFactory) {
        super(taskName, PlainScheduleAndData.class);
        this.handlerBeanName = handlerBeanName;
        this.handlerMethod = handlerMethod;
        this.handlerMethodHasParams = handlerMethod.getParameterCount() > 0;
        this.beanFactory = beanFactory;
    }

    private Object getHandlerBean() {
        return beanFactory.getBean(handlerBeanName);
    }


    @Override
    public CompletionHandler<PlainScheduleAndData> execute(TaskInstance<PlainScheduleAndData> taskInstance, ExecutionContext executionContext) {
        log.info("Starting Execution of  recurring dynamic task : {}", getTaskSimpleName());
        try {
            Object bean = getHandlerBean();
            if(handlerMethodHasParams){
                handlerMethod.invoke(bean, taskInstance.getData().getData());
            }else {
                handlerMethod.invoke(bean);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new TaskException(e);
        }
        log.info("Finished Execution of recurring dynamic task : {}", getTaskSimpleName());
        return (executionComplete, executionOperations) -> executionOperations.reschedule(executionComplete, ((ScheduleAndData)taskInstance.getData()).getSchedule().getNextExecutionTime(executionComplete));

    }

    private String getTaskSimpleName() {
        return getName().substring(0, getName().lastIndexOf(RecurringDynamicTaskBeanManager.BEAN_NAME_SUFFIX));
    }
}

