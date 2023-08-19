package io.github.dnsene.kagkarlssondbscheduler.boot.endpoint;

import java.time.Instant;


public class TaskModel {

    private Type taskType;
    private String taskName;
    private String taskId;
    private Instant lastSuccess;
    private Instant lastFailure;
    private Instant executionTime;
    private int consecutiveFailures;
    private boolean isPicked;
    private String pickedBy;
    private String taskData;
    private String schedule;

    public enum Type { onetime, recurring_dynamic, recurring_static}

    public TaskModel() {
    }

    public Type getTaskType() {
        return taskType;
    }

    public void setTaskType(Type taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Instant getLastSuccess() {
        return lastSuccess;
    }

    public void setLastSuccess(Instant lastSuccess) {
        this.lastSuccess = lastSuccess;
    }

    public Instant getLastFailure() {
        return lastFailure;
    }

    public void setLastFailure(Instant lastFailure) {
        this.lastFailure = lastFailure;
    }

    public Instant getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Instant executionTime) {
        this.executionTime = executionTime;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void setConsecutiveFailures(int consecutiveFailures) {
        this.consecutiveFailures = consecutiveFailures;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public String getPickedBy() {
        return pickedBy;
    }

    public void setPickedBy(String pickedBy) {
        this.pickedBy = pickedBy;
    }

    public String getTaskData() {
        return taskData;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
