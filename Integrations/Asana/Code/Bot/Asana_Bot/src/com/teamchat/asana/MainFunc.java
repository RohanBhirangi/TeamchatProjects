//Owner: Aniruddha Varshney
//Task 2
//Date of Submission: Monday, June 29, 2015
package com.teamchat.asana;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import web.asana.servelet.Data;

import com.google.gson.Gson;
import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Field;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;

public class MainFunc {

	public static Asana_basics ab;
	final String USER_AGENT = "Mozilla/5.0";

	@OnKeyword("help")
	public void ConnectToAsana(TeamchatAPI api) throws IOException {
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestionHtml("<br/>Hi! This is asana Bot. I am going to guide you how to use me to do thinks in asana.<br/>Type the following commands to:<br/>1)create project: To create project<br/>2)delete project: To delete project<br/>3)create task: To add task<br/>4)delete task: to delete task<br/>BUT FIRST YOU HAVE TO LOGIN!")));
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestionHtml(
								"<html><body><a target='_blank' href='https://app.asana.com/-/oauth_authorize?client_id=37903488157876&redirect_uri=http%3A%2F%2Fintegration.teamchat.com%3A8086%2FAsana_Bot%2FRedirect_url&response_type=code'>Click here to connect your Teamchat Account to Asana</a>")
						.alias("start")));
		
	}

	@OnAlias("start")
	public void start(TeamchatAPI api) {
		api.performPostInCurrentRoom(new TextChatlet("Successfully Connected"));
	}

	@OnKeyword("create project")
	public void createProject(TeamchatAPI api) throws IOException {
		// get workspace id and name from json array
		Database_Handler db = new Database_Handler();
		System.out.println(api.context().currentSender().getEmail());
		ab = db.GetBasicStuff(api.context().currentSender().getEmail());
		GetWorkspace gwp = new GetWorkspace();
		Field f = null;
		f = gwp.getWorkspace(ab.getAccess_token(), api);

		PrimaryChatlet chtlet = new PrimaryChatlet().setQuestion(
				"Fill in details of the project").setReplyScreen(
				api.objects()
						.form()
						.addField(
								api.objects().input().label("Project Name")
										.name("project_name"))
						.addField(
								api.objects().input().label("Notes")
										.name("notes")).addField(f));

		chtlet.alias("createproject");

		api.performPostInCurrentRoom(chtlet);

	}

	@OnAlias("createproject")
	public void create_project(TeamchatAPI api) throws IOException {

		String ProjectName = api.context().currentReply()
				.getField("project_name");
		String Notes = api.context().currentReply().getField("notes");

		String[] Workspace = api.context().currentReply()
				.getField("workspace_name").split("-");
		long id = Long.valueOf(Workspace[Workspace.length - 1]);
		System.out.println(id);
		String URL = "https://app.asana.com/api/1.0/projects", URL_parameter = "name="
				+ ProjectName + "&notes=" + Notes + "&workspace=" + id;
		new SendPost().sendPost(URL, USER_AGENT, URL_parameter,
				ab.getAccess_token());
		api.perform(api.context().currentRoom()
				.post(new TextChatlet("Project Created!")));

	}

	@OnKeyword("delete project")
	public void deleteProject(TeamchatAPI api) throws ClientProtocolException,
			IOException {
		Database_Handler db = new Database_Handler();
		ab = db.GetBasicStuff(api.context().currentSender().getEmail());
		GetProject gp = new GetProject();
		Field f = null;
		f = gp.getProject(ab.getAccess_token(), api);

		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion("Select project to be deleted")
						.setReplyScreen(api.objects().form().addField(f))
						.alias("deleteproject")));

	}

	@OnAlias("deleteproject")
	public void delete_project(TeamchatAPI api) throws IOException {

		String[] Project = api.context().currentReply()
				.getField("project_name").split("-");
		long id = Long.valueOf(Project[Project.length - 1]);
		String URL = "https://app.asana.com/api/1.0/projects/" + id;
		String URL_parameter;
		URL_parameter = "";
		SendDelete sd = new SendDelete();
		sd.sendDelete(URL, USER_AGENT, URL_parameter, ab.getAccess_token());
		api.perform(api.context().currentRoom()
				.post(new TextChatlet("Project Deleted!")));

	}

	@OnKeyword("create task")
	public void createTask(TeamchatAPI api) {
		Database_Handler db = new Database_Handler();
		ab = db.GetBasicStuff(api.context().currentSender().getEmail());
		GetWorkspace gwp = new GetWorkspace();
		GetProject gp = new GetProject();
		GetUsers gu = new GetUsers();
		Field f_ws, f_pid, f_uid = null;
		f_ws = gwp.getWorkspace(ab.getAccess_token(), api);
		f_pid = gp.getProject(ab.getAccess_token(), api);
		f_uid = gu.getUser(ab.getAccess_token(), api);
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion("Fill in details of the task")
						.setReplyScreen(
								api.objects()
										.form()
										.addField(
												api.objects().input()
														.label("Task Name:")
														.name("task_name"))
										.addField(
												api.objects().input()
														.label("Notes")
														.name("notes"))
										.addField(f_ws).addField(f_pid)
										.addField(f_uid)).alias("createtask")));

	}

	@OnAlias("createtask")
	public void create_task(TeamchatAPI api) {
		String TaskName = api.context().currentReply().getField("task_name");
		String Notes = api.context().currentReply().getField("notes");

		String[] Workspace = api.context().currentReply()
				.getField("workspace_name").split("-");
		long wid = Long.valueOf(Workspace[Workspace.length - 1]);
		String[] Project = api.context().currentReply()
				.getField("project_name").split("-");
		long pid = Long.valueOf(Project[Project.length - 1]);
		String[] Users = api.context().currentReply().getField("user_name")
				.split("-");
		long uid = Long.valueOf(Users[Users.length - 1]);
		System.out.println(wid);
		String URL = "https://app.asana.com/api/1.0/tasks";
		String URL_parameter;
		URL_parameter = "assignee=" + uid + "&followers[0]=" + uid + "&name="
				+ TaskName + "&notes=" + Notes + "&projects=" + pid
				+ "&workspace=" + wid;
		SendPost sp = new SendPost();
		try {
			sp.sendPost(URL, USER_AGENT, URL_parameter, ab.getAccess_token());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		api.perform(api.context().currentRoom()
				.post(new TextChatlet("Task Created!")));
		;
	}

	@OnKeyword("delete task")
	public void deleteTask(TeamchatAPI api) {
		Database_Handler db = new Database_Handler();
		ab = db.GetBasicStuff(api.context().currentSender().getEmail());
		GetProject gp = new GetProject();
		Field f = null;
		f = gp.getProject(ab.getAccess_token(), api);

		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion("Select project in which the task exists")
						.setReplyScreen(api.objects().form().addField(f))
						.alias("selectproject")));

	}

	@OnAlias("selectproject")
	public void select_task(TeamchatAPI api) {
		String[] Project = api.context().currentReply()
				.getField("project_name").split("-");
		long id = Long.valueOf(Project[Project.length - 1]);
		String URL = "https://app.asana.com/api/1.0/projects/" + id + "/tasks";
		String URL_parameter = "";
		SendGet sg = new SendGet();
		Field field_task = null;
		try {
			String jsonData = sg.sendGet(URL, USER_AGENT, URL_parameter,
					ab.getAccess_token());
			JSONObject jsonObj = new JSONObject(jsonData);
			Gson gson = new Gson();
			Data[] Tasks = gson.fromJson(jsonObj.get("data").toString(),
					Data[].class);

			field_task = api.objects().select().name("task_name")
					.label("Tasks");
			for (Data task : Tasks) {
				field_task.addOption(task.getName() + "-" + task.getId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion("Select task to be deleted")
						.setReplyScreen(
								api.objects().form().addField(field_task))
						.alias("deleteselectedtask")));
	}

	@OnAlias("deleteselectedtask")
	public void delete_task(TeamchatAPI api) {
		System.out.println("inside delete task");
		String[] Project = api.context().currentReply().getField("task_name")
				.split("-");
		long id = Long.valueOf(Project[Project.length - 1]);
		String URL = "https://app.asana.com/api/1.0/tasks/" + id;
		String URL_parameter;
		URL_parameter = "";
		SendDelete sd = new SendDelete();
		sd.sendDelete(URL, USER_AGENT, URL_parameter, ab.getAccess_token());
		api.perform(api.context().currentRoom()
				.post(new TextChatlet("Task Deleted!")));

	}

}