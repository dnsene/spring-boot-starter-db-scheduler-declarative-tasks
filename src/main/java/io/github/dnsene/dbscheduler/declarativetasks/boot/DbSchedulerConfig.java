package io.github.dnsene.dbscheduler.declarativetasks.boot;


import io.github.dnsene.dbscheduler.declarativetasks.api.TaskScheduler;
import io.github.dnsene.dbscheduler.declarativetasks.boot.bfpp.TaskBeanRegistrarBeanFactoryPostProcessor;
import io.github.dnsene.dbscheduler.declarativetasks.boot.endpoint.TaskModelProvider;
import io.github.dnsene.dbscheduler.declarativetasks.boot.endpoint.TasksEndpoint;
import io.github.dnsene.dbscheduler.declarativetasks.boot.endpoint.TaskModelBuilder;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringstatic.RecurringStaticTaskBeanManager;
import com.github.kagkarlsson.scheduler.Scheduler;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.onetime.OneTimeTaskBeanManager;
import io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.recurringdynamic.RecurringDynamicTaskBeanManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;


public class DbSchedulerConfig {


    @Bean
    @ConditionalOnWebApplication
    public TasksEndpoint tasksEndpoint(){
        return new TasksEndpoint();
    }


    @Bean
    @ConditionalOnWebApplication
    public TaskModelProvider taskModelProvider(){
        return new TaskModelProvider();
    }

    @Bean
    @ConditionalOnWebApplication
    public TaskModelBuilder taskModelBuilder(){
        return new TaskModelBuilder();
    }


     @Bean
     public OneTimeTaskBeanManager oneTimeTaskBeanManager(){
         return new OneTimeTaskBeanManager();
     }


     @Bean
     public RecurringStaticTaskBeanManager recurringStaticTaskBeanManager(){
         return new RecurringStaticTaskBeanManager();
     }

     @Bean
     public RecurringDynamicTaskBeanManager recurringDynamicTaskBeanManager(){
         return new RecurringDynamicTaskBeanManager();
     }


    @Bean
    public TaskScheduler dbSchedulerTaskScheduler(Scheduler scheduler,
                                                  OneTimeTaskBeanManager oneTimeTaskBeanManager,
                                                  RecurringDynamicTaskBeanManager recurringDynamicTaskBeanManager){
         return new TaskSchedulerImpl(scheduler, oneTimeTaskBeanManager, recurringDynamicTaskBeanManager);
     }


    @Bean
    public TaskBeanRegistrarBeanFactoryPostProcessor customBeanFactoryPostProcessor(){
        return new TaskBeanRegistrarBeanFactoryPostProcessor();
    }

}
