package com.xxz.loginhouduan.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIService {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    // 用于保存会话上下文
    private final List<JsonObject> messages = new ArrayList<>();

    public OpenAIService() {
        // 初始化系统角色提示，可以根据需求修改
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are a helpful assistant.");
        messages.add(systemMessage);
    }

    public String getChatResponse(String userMessage) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(OPENAI_API_URL);

            // 设置请求头
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + API_KEY);

            // 添加用户消息到上下文
            JsonObject userMessageObject = new JsonObject();
            userMessageObject.addProperty("role", "user");
            userMessageObject.addProperty("content", userMessage);
            messages.add(userMessageObject);


            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-4o");

            JsonArray messagesArray = new JsonArray();
            for (JsonObject message : messages) {
                messagesArray.add(message);
            }
            requestBody.add("messages", messagesArray);

            StringEntity entity = new StringEntity(requestBody.toString(), "UTF-8");
            request.setEntity(entity);


            // 发送请求
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // 解析响应
                JsonObject jsonResponse = new com.google.gson.JsonParser().parse(result.toString()).getAsJsonObject();
                String assistantMessage = jsonResponse
                        .getAsJsonArray("choices")
                        .get(0)
                        .getAsJsonObject()
                        .get("message")
                        .getAsJsonObject()
                        .get("content")
                        .getAsString();

                // 将助手回复添加到上下文
                JsonObject assistantMessageObject = new JsonObject();
                assistantMessageObject.addProperty("role", "assistant");
                assistantMessageObject.addProperty("content", assistantMessage);
                messages.add(assistantMessageObject);

                return assistantMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}



