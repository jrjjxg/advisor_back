package com.advisor.controller;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class ChatController {


    @Autowired
    ChatLanguageModel chatLanguageModel;

    @RequestMapping("/chat")
    public String test(@RequestParam(defaultValue = "你是谁") String message){
        String chat = chatLanguageModel.chat(message);
        return chat;
    }

}
