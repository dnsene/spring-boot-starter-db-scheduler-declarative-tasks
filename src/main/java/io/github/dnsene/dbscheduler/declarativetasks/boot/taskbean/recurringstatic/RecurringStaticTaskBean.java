package io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringstatic;

import io.github.dnsene.dbscheduler.declarativetasks.api.TaskException;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.schedule.CronSchedule;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;


public class RecurringStaticTaskBean extends RecurringTask<Void>  {

    private static final Logger log = LoggerFactory.getLogger(RecurringStaticTaskBean.class);

    private final String handlerBeanName;
    private final Method handlerMethod;
    private final BeanFactory beanFactory;

    public RecurringStaticTaskBean(String taskName, String handlerBeanName, Method handlerMethod, CronSchedule cronSchedule, BeanFactory beanFactory) {
        super(taskName, cronSchedule, Void.class);
        this.handlerBeanName = handlerBeanName;
        this.handlerMethod = handlerMethod;
        this.beanFactory = beanFactory;
    }

    public RecurringStaticTaskBean(String taskName, String handlerBeanName, Method handlerMethod, Duration fixedDelay, BeanFactory beanFactory) {
        super(taskName, Schedules.fixedDelay(fixedDelay), Void.class);
        this.handlerBeanName = handlerBeanName;
        this.handlerMethod = handlerMethod;
        this.beanFactory = beanFactory;
    }




    private Object getHandlerBean() {
        return beanFactory.getBean(handlerBeanName);
    }


    @Override
    public void executeRecurringly(TaskInstance<Void> taskInstance, ExecutionContext executionContext) {
        log.info("Starting Execution of  recurring static task : {}", getTaskSimpleName());
        try {
            Object bean = getHandlerBean();
            handlerMethod.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new TaskException(e);
        }
        log.info("Finished Execution of recurring dynamic task : {}", getTaskSimpleName());
    }

    private String getTaskSimpleName() {
        return getName().substring(0, getName().lastIndexOf(RecurringStaticTaskBeanManager.BEAN_NAME_SUFFIX));
    }
}

