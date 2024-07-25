package com.css.one.services;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenAIClient {

	private static final String API_URL = "https://api.openai.com/v1/completions";
	private static final String API_KEY = "sk-proj-SzVWtogZrPv2A5gxWDBfT3BlbkFJ9gzMygPlRvBcoBYnJwGI";

	public String getCompletion(String prompt) throws IOException {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost request = new HttpPost(API_URL);
			request.setHeader("Authorization", "Bearer " + API_KEY);
			request.setHeader("Content-Type", "application/json");

			Map<String, Object> data = new HashMap<>();
			data.put("model", "gpt-4o-mini");
			data.put("prompt", prompt);
			data.put("max_tokens", 50);

			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(data);
			request.setEntity(new StringEntity(json));

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				return new String(response.getEntity().getContent().readAllBytes());
			}
		}
	}
}
