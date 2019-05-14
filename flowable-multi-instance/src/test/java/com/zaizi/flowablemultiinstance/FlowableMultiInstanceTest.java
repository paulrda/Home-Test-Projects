package com.zaizi.flowablemultiinstance;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.http.util.EntityUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlowableMultiInstanceTest {

	ProcessEngine processEngine;
	RepositoryService repositoryService;
	RuntimeService runtimeService;
	ProcessInstance processInstance;
	TaskService taskService;
	FormService formService;

	@Before
	public void createInstance() {

		processEngine = ProcessEngines.getDefaultProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		formService = processEngine.getFormService();
		repositoryService.createDeployment().name("MultiInstanceTestForm")
				.addClasspathResource("processes/multi_instance_test-form.bpmn").deploy();
		processInstance = runtimeService.startProcessInstanceByKey("MultiInstanceTestForm");
	}

	@Test
	public void testMultiInstanceTaskVariables() {

		List<Task> taskList = gettaskList();

		Task firstTask = taskList.get(0);
		Task secondTask = taskList.get(1);

		assertEquals("item 1", taskService.getVariable(firstTask.getId(), "item"));
		assertEquals("item 2", taskService.getVariable(secondTask.getId(), "item"));

	}

	@Test
	public void testMultiInstaceFormVariables() throws ClientProtocolException, IOException, ParseException {

		List<Task> taskList = gettaskList();

		Task firstTask = taskList.get(0);
		Task secondTask = taskList.get(1);

		String firstFormValue = getFormValue(firstTask.getId());
		String secondFormValue = getFormValue(secondTask.getId());

		assertEquals("item 1", firstFormValue);
		assertEquals("item 2", secondFormValue);

	}

	/*
	 * return active tasks list for given process instance
	 */
	List<Task> gettaskList() {
		TaskQuery quesry = taskService.createTaskQuery().processInstanceId(processInstance.getId());
		return quesry.list();
	}

	/*
	 * return the value of form field. Flowable rest api was used to get form
	 * details of runing tasks.
	 */
	String getFormValue(String taskId) throws ClientProtocolException, IOException, ParseException {

		HttpUriRequest request = new HttpGet("http://localhost:8090/process-api/runtime/tasks/" + taskId + "/form");
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String responseBody = EntityUtils.toString(httpResponse.getEntity());
		Object obj = new JSONParser().parse(responseBody);
		JSONObject jsonObj = (JSONObject) obj;
		JSONArray jsonArray = (JSONArray) jsonObj.get("fields");
		return ((Map) jsonArray.get(0)).get("value").toString();
	}

}
