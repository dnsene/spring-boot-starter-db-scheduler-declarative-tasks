package io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringstatic;


import io.github.dnsene.dbscheduler.declarativetasks.api.TaskException;
import io.github.dnsene.dbscheduler.declarativetasks.boot.bfpp.TaskDeclaration;
import io.github.dnsene.dbscheduler.declarativetasks.boot.util.Pair;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTask;
import com.github.kagkarlsson.scheduler.task.schedule.CronSchedule;
import com.github.kagkarlsson.scheduler.task.schedule.Schedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;


public class RecurringStaticTaskBeanManager {


    public static final String BEAN_NAME_SUFFIX = "_recurring_static";


    @Autowired
    private ApplicationContext context;


    public static Pair<String, Task<?>> createBean(TaskDeclaration taskDeclaration, ConfigurableListableBeanFactory beanFactory) {
        String internalTaskName = getInternalTaskName(taskDeclaration.getTaskName());
        RecurringStaticTaskBean bean;
        if(taskDeclaration.getCronExpression().isPresent()){
            CronSchedule cronSchedule;
            try {
                cronSchedule = Schedules.cron(taskDeclaration.getCronExpression().get());
            }catch (IllegalArgumentException e){
                throw new TaskException(e);
            }
            bean = new RecurringStaticTaskBean(
                    internalTaskName,
                    taskDeclaration.getHandlerBeanName(),
                    taskDeclaration.getHandlerMethod(),
                    cronSchedule,
                    beanFactory
            );
        } else if (taskDeclaration.getFixedDelay().isPresent()){
            bean = new RecurringStaticTaskBean(
                    internalTaskName,
                    taskDeclaration.getHandlerBeanName(),
                    taskDeclaration.getHandlerMethod(),
                    taskDeclaration.getFixedDelay().get(),
                    beanFactory
            );
        } else {
            throw new TaskException("fixed delay and cron expression cannot be null for RecurringStaticTaskBean");
        }
        return Pair.of(internalTaskName, bean);

    }

    public RecurringTask<Void> getBean(String taskName){
        return context.getBean(getInternalTaskName(taskName), RecurringStaticTaskBean.class);
    }

    private static String getInternalTaskName(String taskName) {
        return taskName + BEAN_NAME_SUFFIX;
    }
}
