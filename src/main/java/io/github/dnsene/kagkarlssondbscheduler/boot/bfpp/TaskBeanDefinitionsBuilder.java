package io.github.dnsene.kagkarlssondbscheduler.boot.bfpp;

import io.github.dnsene.kagkarlssondbscheduler.boot.util.Pair;
import io.github.dnsene.kagkarlssondbscheduler.boot.taskbean.onetime.OneTimeTaskBeanManager;
import io.github.dnsene.kagkarlssondbscheduler.boot.taskbean.recurringdynamic.RecurringDynamicTaskBeanManager;
import io.github.dnsene.kagkarlssondbscheduler.boot.taskbean.recurringstatic.RecurringStaticTaskBeanManager;
import com.github.kagkarlsson.scheduler.task.Task;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskBeanDefinitionsBuilder {


    private final ConfigurableListableBeanFactory beanFactory;

    public TaskBeanDefinitionsBuilder(ConfigurableListableBeanFactory beanFactory) {

        this.beanFactory = beanFactory;
    }

    public List<Pair<String, Task<?>>> createBeans(TaskDeclaration taskDeclaration){

        List<Pair<String, Task<?>>> beanDefinitions = new ArrayList<>();

        Pair<String, Task<?>> oneTimeTaskBeanDefinition = OneTimeTaskBeanManager.createBean(taskDeclaration, beanFactory);
        beanDefinitions.add(oneTimeTaskBeanDefinition);

        Pair<String, Task<?>> recurringDynamicTaskBeanDefinition = RecurringDynamicTaskBeanManager.createBean(taskDeclaration, beanFactory);
        beanDefinitions.add(recurringDynamicTaskBeanDefinition);

        if(taskDeclaration.isStatic()){
            Pair<String, Task<?>> recurringStaticTaskBeanDefinition = RecurringStaticTaskBeanManager.createBean(taskDeclaration, beanFactory);
            beanDefinitions.add(recurringStaticTaskBeanDefinition);
        }
        return beanDefinitions;
    }
}
