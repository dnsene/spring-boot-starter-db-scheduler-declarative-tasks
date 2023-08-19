package io.github.dnsene.kagkarlssondbscheduler.boot.bfpp;




import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;



public class TaskDeclaration {

    private String taskName;
    private String handlerBeanName;
    private Method handlerMethod;
    private Class<?> dataClass;
    private Optional<String> cronExpression;
    private Optional<Duration> fixedDelay;


    public TaskDeclaration(String taskName, String handlerBeanName, Method handlerMethod, Class<?> dataClass, Optional<String> cronExpression, Optional<Duration> fixedDelay) {
        this.taskName = taskName;
        this.handlerBeanName = handlerBeanName;
        this.handlerMethod = handlerMethod;
        this.dataClass = dataClass;
        this.cronExpression = cronExpression;
        this.fixedDelay = fixedDelay;
    }

    public boolean isStatic() {
       return cronExpression.isPresent() || fixedDelay.isPresent();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getHandlerBeanName() {
        return handlerBeanName;
    }

    public void setHandlerBeanName(String handlerBeanName) {
        this.handlerBeanName = handlerBeanName;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(Method handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    public Optional<String> getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(Optional<String> cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Optional<Duration> getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(Optional<Duration> fixedDelay) {
        this.fixedDelay = fixedDelay;
    }
}
