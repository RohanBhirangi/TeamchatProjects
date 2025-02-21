/**
 * 
 */
package com.teamchat.integrations.basecamp;

//basic classes for json parsing help
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.basecamp.classes.*;
import com.basecamp.helpers.HTTP_Response;
import com.basecamp.helpers.Json_generator;
//json parsing
import com.google.gson.Gson;
import com.mysql.fabric.xmlrpc.base.Array;

/**
 * @author Puranjay Jain
 *
 */
public class Basecamp_api_handler {
	private Requests_handler rh = new Requests_handler();
	private Basecamp_basics bb;
	private Gson gson = new Gson();
	// user agent string
	private String ua = "Teamchat (http://www.teamchat.com/en/)";

	// init constructor
	public Basecamp_api_handler(Basecamp_basics bb) {
		// TODO Auto-generated constructor stub
		this.bb = bb;
	}

	// public methods for api access

	// posting data requests
	// send message and returns the reason if not posted
	public HTTP_Response sendMessage(String projectId, String subject,
			String content) {
		try {
			Map<String, String> hashMap = new TreeMap<>();
			hashMap.put("subject", subject);
			hashMap.put("content", content);
			String dataJson = new Json_generator().toJsonString(hashMap);
			return rh.sendPost_auth(bb.getHref() + "/projects/" + projectId
					+ "/messages.json", ua, dataJson, bb.getAccess_token(), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// getting data requests
	// return message
	public Message getMessage(Topic topic) {
		try {
			String response = rh.sendGet_auth(topic.getTopicable().getUrl(),
					ua, "", bb.getAccess_token());
			// put and return
			return (Message) gson.fromJson(response, Message.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// get topic from topics
	public Topic getTopic(Topic[] topics, int id) {
		for (Topic topic : topics) {
			if (id == topic.getId()) {
				return topic;
			}
		}
		return null;
	}

	// return topics
	// for a particular project
	// topictype can be "Message","Todo",etc.
	public Topic[] getProjectTopics(String projectId, String topicType) {
		try {
			String response = rh.sendGet_auth(bb.getHref() + "/projects/"
					+ projectId + "/topics.json", ua, "", bb.getAccess_token());
			// select only the responses you want
			ArrayList<Topic> topics = new ArrayList<Topic>();
			for (Topic topic : gson.fromJson(response, Topic[].class)) {
				// choose only topic types you want
				if (topicType.equalsIgnoreCase(topic.getTopicable().getType())) {
					topics.add(topic);
				}
			}
			// put response in class
			Topic[] info = topics.toArray(new Topic[topics.size()]);
			return info;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// return todos for a todolist inside a project
	// for a particular project
	public Todo[] getActiveTodos(String projectId, String todolistId) {
		try {
			String response = rh.sendGet_auth(bb.getHref() + "/projects/"
					+ projectId + "/todolists/" + todolistId + "/todos.json",
					ua, "", bb.getAccess_token());
			// put response in class
			return (Todo[]) gson.fromJson(response, Todo[].class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// return todolists list
	// for a particular project
	public Todolist[] getActiveTodoLists(String projectId) {
		try {
			String response = rh.sendGet_auth(bb.getHref() + "/projects/"
					+ projectId + "/todolists.json", ua, "",
					bb.getAccess_token());
			// put response in class
			return (Todolist[]) gson.fromJson(response, Todolist[].class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// return active project list
	public Project[] getActiveProjects() {
		try {
			String response = rh.sendGet_auth(bb.getHref() + "/projects.json",
					ua, "", bb.getAccess_token());
			// put response in class
			return (Project[]) gson.fromJson(response, Project[].class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// return active project list (names)
	public String[] getActiveProjects_names() {
		ArrayList<String> data = new ArrayList<String>();
		try {
			String response = rh.sendGet_auth(bb.getHref() + "/projects.json",
					ua, "", bb.getAccess_token());
			// put response in class
			Project[] projects = gson.fromJson(response, Project[].class);
			for (Project project : projects) {
				data.add(project.getName());
			}
			String[] info = data.toArray(new String[data.size()]);
			return info;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
