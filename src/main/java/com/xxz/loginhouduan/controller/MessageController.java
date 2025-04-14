package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.Message;
import com.xxz.loginhouduan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public List<Message> getMessages(@RequestParam String sender,
                                     @RequestParam String receiverId) {
        return messageService.getMessages(sender, receiverId);
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        System.out.println("📩 收到消息: " + message);
        if (message.getSender() == null || message.getReceiver() == null || message.getContent() == null) {
            throw new IllegalArgumentException("sender/receiver/content 不能为空！");
        }
        return messageService.saveMessage(message);
    }


}

