package io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringdynamic;


import io.github.dnsene.dbscheduler.declarativetasks.boot.bfpp.TaskDeclaration;
import io.github.dnsene.dbscheduler.declarativetasks.boot.util.Pair;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.helper.RecurringTaskWithPersistentSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;


public class RecurringDynamicTaskBeanManager {


    public static final String BEAN_NAME_SUFFIX = "_recurring_dynamic";

    @Autowired
    private ApplicationContext context;

    public static Pair<String, Task<?>> createBean(TaskDeclaration taskDeclaration, ConfigurableListableBeanFactory beanFactory) {
        String internalTaskName = getInternalTaskName(taskDeclaration.getTaskName());

        RecurringDynamicTaskBean bean = new RecurringDynamicTaskBean(
                internalTaskName,
                taskDeclaration.getHandlerBeanName(),
                taskDeclaration.getHandlerMethod(),
                beanFactory
        );

        return Pair.of(internalTaskName, bean);
    }

    public RecurringTaskWithPersistentSchedule<?> getBean(String taskName){
        return context.getBean(getInternalTaskName(taskName), RecurringDynamicTaskBean.class);
    }

    private static String getInternalTaskName(String taskName) {
        return taskName + BEAN_NAME_SUFFIX;
    }
}
