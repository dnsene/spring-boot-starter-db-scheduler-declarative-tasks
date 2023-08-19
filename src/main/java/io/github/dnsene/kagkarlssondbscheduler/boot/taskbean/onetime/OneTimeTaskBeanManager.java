package io.github.dnsene.kagkarlssondbscheduler.boot.taskbean.onetime;

import io.github.dnsene.kagkarlssondbscheduler.boot.bfpp.TaskDeclaration;
import io.github.dnsene.kagkarlssondbscheduler.boot.util.Pair;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class OneTimeTaskBeanManager {


    @Autowired
    private ApplicationContext context;

    public static Pair<String, Task<?>> createBean(TaskDeclaration taskDeclaration, BeanFactory beanFactory){
        String internalTaskName = getInternalTaskName(taskDeclaration.getTaskName());

        OneTimeTaskBean<?> bean = new OneTimeTaskBean<>(
                internalTaskName,
                taskDeclaration.getHandlerBeanName(),
                taskDeclaration.getHandlerMethod(),
                taskDeclaration.getDataClass(),
                beanFactory);

        return Pair.of(internalTaskName, bean);
    }


    private static String getInternalTaskName(String taskName) {
        return taskName;
    }

    public OneTimeTask<?> getBean(String taskName){
        return context.getBean(getInternalTaskName(taskName), OneTimeTaskBean.class);
    }
}
