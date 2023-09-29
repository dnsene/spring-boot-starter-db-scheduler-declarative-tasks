package io.github.dnsene.dbscheduler.declarativetasks.boot.bfpp;

import io.github.dnsene.dbscheduler.declarativetasks.api.RunOnCronSchedule;
import io.github.dnsene.dbscheduler.declarativetasks.api.RunOnFixedDelay;
import io.github.dnsene.dbscheduler.declarativetasks.api.Task;
import io.github.dnsene.dbscheduler.declarativetasks.api.TaskException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ComponentAnnotatedBeanTaskFinder {


    public boolean isComponentAnnotatedAndTaskBeanDefinition(BeanDefinition beanDefinition){
        if(!(beanDefinition instanceof ScannedGenericBeanDefinition)){
            return false;
        }
        return  ((ScannedGenericBeanDefinition) beanDefinition)
                .getMetadata()
                .hasAnnotatedMethods(Task.class.getName());

    }

    public List<TaskDeclaration> getTaskDeclarations(String beanName, BeanDefinition beanDefinition) {

        return  ((ScannedGenericBeanDefinition) beanDefinition)
                .getMetadata()
                .getAnnotatedMethods(Task.class.getName())
                .stream()
                .map(methodMetadata -> {
                    String taskName = getTaskName(methodMetadata);
                    Method taskMethod = getHandlerMethod(methodMetadata, taskName );
                    Class<?> taskDataClass = getTaskDataClass(taskMethod);
                    Optional<String> taskCronExpression = getTaskCronExpression(methodMetadata);
                    Optional<Duration> taskFixedDelay = getTaskFixedDelay(methodMetadata);
                    return new TaskDeclaration(taskName, beanName, taskMethod, taskDataClass, taskCronExpression, taskFixedDelay);
               })
               .collect(Collectors.toList());
    }


    public String getTaskName(MethodMetadata methodMetadata) {
        return (String) getAnnotationValue(methodMetadata, Task.class);
    }


    private Optional<Duration> getTaskFixedDelay(MethodMetadata methodMetadata) {
        Object fixedDelay =  getAnnotationValue(methodMetadata, RunOnFixedDelay.class);
        if(fixedDelay == null || ((long)fixedDelay) <= 0){
            return Optional.empty();
        }
        ChronoUnit unit = (ChronoUnit) getAnnotationAttributeValue(methodMetadata, RunOnFixedDelay.class, "unit");
        return Optional.of(Duration.of((Long) fixedDelay, unit));
    }

    public Object getAnnotationValue(MethodMetadata methodMetadata, Class<?> annotationClass){
        Map<String, Object> annotationAttributes = methodMetadata.getAnnotationAttributes(annotationClass.getName());
        if(annotationAttributes == null) return null;
        return annotationAttributes.get("value");
    }

    public Object getAnnotationAttributeValue(MethodMetadata methodMetadata, Class<?> annotationClass, String attributName){
        Map<String, Object> annotationAttributes = methodMetadata.getAnnotationAttributes(annotationClass.getName());
        if(annotationAttributes == null) return null;
        return annotationAttributes.get(attributName);
    }

    private Optional<String> getTaskCronExpression(MethodMetadata methodMetadata) {
        Object cronExpression = getAnnotationValue(methodMetadata, RunOnCronSchedule.class);
        if(cronExpression == null || !StringUtils.hasText((CharSequence) cronExpression)){
            return Optional.empty();
        }
        return Optional.of((String) cronExpression);
    }


    private Method getHandlerMethod(MethodMetadata methodMetadata, String taskName) {
        String methodName = methodMetadata.getMethodName();
        List<Method> methodList = Arrays.stream(getaClass(methodMetadata).getDeclaredMethods())
                .filter(method ->
                        method.getName().equals(methodName) &&
                                method.getParameterCount() <= 1 &&
                                taskName.equals(method.getAnnotation(Task.class).value())
                )
                .collect(Collectors.toList());
        return methodList.get(0);
    }

    private  Class<?> getaClass(MethodMetadata methodMetadata) {
        try {
            return Class.forName(methodMetadata.getDeclaringClassName());
        } catch (ClassNotFoundException e) {
            throw new TaskException(e);
        }
    }

    private Class<?> getTaskDataClass(Method taskMethod) {
        return taskMethod.getParameterTypes().length > 0 ? taskMethod.getParameterTypes()[0] : null;

    }

}
