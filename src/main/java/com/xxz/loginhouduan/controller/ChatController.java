package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.repository.UserRepository;
import com.xxz.loginhouduan.service.OpenAIService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:8080")
public class ChatController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private UserRepository userRepository;

    private final Gson gson = new Gson();

    private String formatAiResponse(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "<p class='ai-response'>No response received.</p>";
        }

        StringBuilder formatted = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.matches("^[0-9]+\\..*")) {
                formatted.append("<h3 class='ai-heading'>").append(line).append("</h3>");
            } else {
                formatted.append("<p class='ai-response'>").append(line).append("</p>");
            }
        }

        return formatted.toString();
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody(required = false) Map<String, String> request) {
        // 🛠 1. 确保请求参数不为空
        if (request == null || !request.containsKey("loginName") || !request.containsKey("userMessage")) {
            throw new IllegalArgumentException("Invalid request: missing loginName or userMessage");
        }

        String loginName = request.get("loginName");
        String userMessage = request.get("userMessage");

        // 🛠 2. 确保 loginName 和 userMessage 不能为空
        if (loginName == null || loginName.trim().isEmpty()) {
            throw new IllegalArgumentException("loginName cannot be empty");
        }
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("userMessage cannot be empty");
        }

        // 🛠 3. 查找用户，避免 null
        SysUserEntity user = userRepository.findByLoginName(loginName);
        if (user == null) {
            throw new RuntimeException("User not found: " + loginName);
        }

        // 🛠 4. 获取用户聊天历史，确保不为 null
        List<Map<String, String>> chatHistory = new ArrayList<>();
        if (user.getChatHistory() != null && !user.getChatHistory().trim().isEmpty()) {
            chatHistory = gson.fromJson(user.getChatHistory(), new TypeToken<List<Map<String, String>>>() {}.getType());
        }

        // 添加用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        chatHistory.add(userMsg);

        // 🛠 5. 确保 AI 回复不会是 null
        String aiResponse = openAIService.getChatResponse(userMessage);
        if (aiResponse == null) {
            aiResponse = "Sorry, I couldn't process your request.";
        }

        // 添加 AI 回复
        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "assistant");
        aiMsg.put("content", formatAiResponse(aiResponse));
        chatHistory.add(aiMsg);

        // 🛠 6. 存储新的聊天记录
        user.setChatHistory(gson.toJson(chatHistory));
        userRepository.updateChatHistory(user.getId(), user.getChatHistory());

        return aiMsg;
    }

    @GetMapping("/{loginName}")
    public List<Map<String, String>> getChatHistory(@PathVariable String loginName) {
        if (loginName == null || loginName.trim().isEmpty()) {
            throw new IllegalArgumentException("loginName cannot be empty");
        }

        SysUserEntity user = userRepository.findByLoginName(loginName);
        if (user == null) {
            throw new RuntimeException("User not found: " + loginName);
        }

        if (user.getChatHistory() == null || user.getChatHistory().trim().isEmpty()) {
            return new ArrayList<>();
        }

        return gson.fromJson(user.getChatHistory(), new TypeToken<List<Map<String, String>>>() {}.getType());
    }
}