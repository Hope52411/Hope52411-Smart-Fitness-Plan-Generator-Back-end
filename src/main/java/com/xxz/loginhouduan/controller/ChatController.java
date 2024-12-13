package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:8080") // 前端的地址
public class ChatController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping
    public String chat(@RequestBody String userMessage) {
        return openAIService.getChatResponse(userMessage);
    }
}



