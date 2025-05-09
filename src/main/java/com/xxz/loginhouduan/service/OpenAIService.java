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
    private static final String API_KEY = "OPENAI_API_KEY";

    // Used to store the conversation context
    private final List<JsonObject> messages = new ArrayList<>();

    public OpenAIService() {
        // Initialize system message to set up the assistant's behavior and ask for user details
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
                "You are a professional fitness and nutrition assistant. "
                        + "Your role is to help users create personalized workout routines and diet plans based on their individual needs. "
                        + "Before providing any recommendations, always ask the user for the following details:\n"
                        + "1. Age\n"
                        + "2. Gender\n"
                        + "3. Height and weight\n"
                        + "4. Fitness goal (e.g., weight loss, muscle gain, endurance improvement, general health)\n"
                        + "5. Current fitness level (beginner, intermediate, advanced)\n"
                        + "6. Any dietary restrictions or preferences\n"
                        + "7. Any existing medical conditions or injuries that may affect training\n"
                        + "Once you have gathered this information, provide tailored fitness and nutrition advice, ensuring safety and effectiveness. "
                        + "Use scientifically backed principles and always recommend consulting a certified trainer or nutritionist for medical concerns."
        );

        messages.add(systemMessage);
    }


    public String getChatResponse(String userMessage) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(OPENAI_API_URL);

            // Set request headers
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + API_KEY);

            // Add user message to context
            JsonObject userMessageObject = new JsonObject();
            userMessageObject.addProperty("role", "user");
            userMessageObject.addProperty("content", userMessage);
            messages.add(userMessageObject);

            // Prepare request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-4o");

            JsonArray messagesArray = new JsonArray();
            for (JsonObject message : messages) {
                messagesArray.add(message);
            }
            requestBody.add("messages", messagesArray);

            StringEntity entity = new StringEntity(requestBody.toString(), "UTF-8");
            request.setEntity(entity);


            // Send the request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Parse the response
                JsonObject jsonResponse = new com.google.gson.JsonParser().parse(result.toString()).getAsJsonObject();
                String assistantMessage = jsonResponse
                        .getAsJsonArray("choices")
                        .get(0)
                        .getAsJsonObject()
                        .get("message")
                        .getAsJsonObject()
                        .get("content")
                        .getAsString();

                // Add assistant's response to the context
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