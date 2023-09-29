package io.github.dnsene.dbscheduler.declarativetasks.boot.endpoint;

import com.github.kagkarlsson.scheduler.ScheduledExecution;
import com.github.kagkarlsson.scheduler.SchedulerClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskModelProvider {


    @Autowired
    private SchedulerClient schedulerClient;

    @Autowired
    private TaskModelBuilder taskModelBuilder;


    public Map<TaskModel.Type, List<TaskModel>> getTasks() {
        List<ScheduledExecution<Object>> scheduledExecutions = schedulerClient.getScheduledExecutions();
        return scheduledExecutions
                .stream()
                .map(x -> taskModelBuilder.build(x))
                .collect(Collectors.groupingBy(TaskModel::getTaskType));
    }
}
