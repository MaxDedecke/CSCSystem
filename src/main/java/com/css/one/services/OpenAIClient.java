package com.css.one.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OpenAIClient {

	private static final String API_URL = "https://api.openai.com/v1/chat/completions";
	private static final String API_KEY = "sk-proj-SzVWtogZrPv2A5gxWDBfT3BlbkFJ9gzMygPlRvBcoBYnJwGI";
	
	public String getCompletion(String prompt) throws IOException {		
		 HttpClient client = HttpClient.newBuilder()
                 .version(Version.HTTP_2)
                 .build();
		
		JSONObject requestBody = new JSONObject();
        try {
        	requestBody.put("model", "gpt-4"); // Set the model you want to use
			requestBody.put("messages", new JSONObject[] {
			    new JSONObject().put("role", "system").put("content", "Du bist ein helfender Assistent einer Verwaltungssoftware. Antworte ausschließlich auf deutsch!"),
			    new JSONObject().put("role", "user").put("content", prompt)
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(API_URL))
                                         .header("Content-Type", "application/json")
                                         .header("Authorization", "Bearer " + API_KEY)
                                         .POST(BodyPublishers.ofString(requestBody.toString()))
                                         .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                    String content = message.getString("content");
                    System.out.println("Response from model: " + content);
                    return content;
                } else {
                    System.out.println("No choices found in the response.");
                    return "No choices found in the response.";
                }
            } else {
            	return String.valueOf(response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
	}
}
