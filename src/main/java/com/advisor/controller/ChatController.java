package com.advisor.controller;

import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.dashscope.QwenChatModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private QwenChatModel qwenChatModel;

    @RequestMapping ("/chat")
    public String chat(@RequestParam(defaultValue = "你是谁") String message) {
        String chat = qwenChatModel.generate(message);
        return chat;
    }
    
}
