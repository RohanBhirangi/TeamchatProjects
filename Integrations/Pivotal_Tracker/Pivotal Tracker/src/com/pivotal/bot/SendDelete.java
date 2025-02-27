package com.pivotal.bot;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendDelete {
	

	
	public String sendDelete(String url, String User_agent, String urlParameters,String token) {
		
		StringBuffer response = new StringBuffer();
		URL obj;
		try {
			obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("DELETE");
			con.setRequestProperty("User-Agent", User_agent);
			con.setRequestProperty("X-TrackerToken", token);

			// Send post request
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			con.connect();
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'DELETE' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString();

	}
}
