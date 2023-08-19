package io.github.dnsene.kagkarlssondbscheduler.boot.bfpp;

import io.github.dnsene.kagkarlssondbscheduler.api.RunOnCronSchedule;
import io.github.dnsene.kagkarlssondbscheduler.api.RunOnFixedDelay;
import io.github.dnsene.kagkarlssondbscheduler.api.Task;
import io.github.dnsene.kagkarlssondbscheduler.api.TaskException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;


public class BeanAnnotatedBeanTaskFinder {


    public boolean isBeanAnnotatedAndTaskBeanDefinition(BeanDefinition beanDefinition){
        if(!beanDefinition.getClass().getName().equals("org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader$ConfigurationClassBeanDefinition")){
            return false;
        }
        Class<?> beanClass = getBeanClass(beanDefinition);
        return Arrays.stream(beanClass.getDeclaredMethods())
                .anyMatch(method -> method.getAnnotation(Task.class) != null);

    }

    private  Class<?> getBeanClass(BeanDefinition beanDefinition)  {
        try {
            Field factoryMethodMetaDataField = beanDefinition.getClass().getDeclaredField("factoryMethodMetadata");
            factoryMethodMetaDataField.setAccessible(true);
            Object factoryMethodMetaData = factoryMethodMetaDataField.get(beanDefinition);
            Field returnTypeNameField = factoryMethodMetaData.getClass().getDeclaredField("returnTypeName");
            returnTypeNameField.setAccessible(true);
            String beanClassName = (String) returnTypeNameField.get(factoryMethodMetaData);
            return Class.forName(beanClassName);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new TaskException(e);
        }

    }


    public List<TaskDeclaration> getTaskDeclarations(String beanName, BeanDefinition beanDefinition) {
        Class<?> beanClass = getBeanClass(beanDefinition);
        return Arrays
                .stream(beanClass.getDeclaredMethods())
                .filter(method -> method.getAnnotation(Task.class)  != null)
                .map(method -> {
                    Task task = method.getAnnotation(Task.class);
                    String taskName = task.value();
                    Class<?> taskDataClass = getTaskDataClass(method);
                    Optional<String> taskCronExpression = getTaskCronExpression(method);
                    Optional<Duration> taskFixedDelay = getTaskFixedDelay(method);
                    return new TaskDeclaration(taskName, beanName, method, taskDataClass, taskCronExpression, taskFixedDelay);
               })
               .collect(Collectors.toList());
    }


    private Optional<Duration> getTaskFixedDelay(Method method) {
        RunOnFixedDelay runOnFixedDelay = method.getAnnotation(RunOnFixedDelay.class);
        if (runOnFixedDelay == null || runOnFixedDelay.value() <= 0) return Optional.empty();
        return Optional.of(Duration.of(runOnFixedDelay.value(), runOnFixedDelay.unit()));
    }

    private Optional<String> getTaskCronExpression(Method method) {
        RunOnCronSchedule runOnCronSchedule = method.getAnnotation(RunOnCronSchedule.class);
        if(runOnCronSchedule == null || !hasText(runOnCronSchedule.value())) return Optional.empty();
        return Optional.of(runOnCronSchedule.value());
    }


    private Class<?> getTaskDataClass(Method taskMethod) {
        return taskMethod.getParameterTypes().length > 0 ? taskMethod.getParameterTypes()[0] : null;
    }

}
