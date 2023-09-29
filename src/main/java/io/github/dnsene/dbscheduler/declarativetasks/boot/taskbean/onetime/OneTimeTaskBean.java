package io.github.dnsene.dbscheduler.declarativetasks.boot.taskbean.onetime;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import io.github.dnsene.dbscheduler.declarativetasks.api.TaskException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class OneTimeTaskBean<T> extends OneTimeTask<T>  {

    private static final Logger log = LoggerFactory.getLogger(OneTimeTaskBean.class);

    private final Method handlerMethod;
    private final boolean handlerMethodHasParams;
    private final String handlerBeanName;
    private final BeanFactory beanFactory;

    public OneTimeTaskBean(String taskName, String handlerBeanName, Method handlerMethod, Class<T> dataClass, BeanFactory beanFactory) {
        super(taskName, dataClass);
        this.handlerMethod = handlerMethod;
        this.handlerMethodHasParams = handlerMethod.getParameterCount() > 0;
        this.handlerBeanName = handlerBeanName;
        this.beanFactory = beanFactory;
    }

    @Override
    public void executeOnce(TaskInstance<T> taskInstance, ExecutionContext executionContext) {
        log.info("Starting Execution of task : {}", getName());
        try {
            Object handlerBean = getHandlerBean();
            if(handlerMethodHasParams){
                handlerMethod.invoke(handlerBean, taskInstance.getData());
            }else {
                handlerMethod.invoke(handlerBean);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new TaskException(e);
        }
        log.info("Finished Executing task : {}", getName());
    }

    private Object getHandlerBean() {
        return beanFactory.getBean(handlerBeanName);
    }


}

