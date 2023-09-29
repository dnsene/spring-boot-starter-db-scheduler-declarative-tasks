package io.github.dnsene.dbscheduler.declarativetasks.boot.endpoint;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;



@ControllerEndpoint(id = TasksEndpoint.ID)
public class TasksEndpoint {

	public static final String ID = "tasks";

	@Autowired
	private TaskModelProvider taskModelProvider;


	@GetMapping
	@ResponseBody
	public 	Map<TaskModel.Type, List<TaskModel>> json(){
		return taskModelProvider.getTasks();
	}

	@GetMapping(produces = "text/html")
	public String html(Model model) {
		Map<TaskModel.Type, List<TaskModel>> tasks = taskModelProvider.getTasks();
		model.addAttribute("onetimetasks", tasks.get(TaskModel.Type.onetime));
		model.addAttribute("recurringdynamictasks", tasks.get(TaskModel.Type.recurring_dynamic));
		model.addAttribute("recurringstatictasks", tasks.get(TaskModel.Type.recurring_static));
		return "tasks";
	}



}
