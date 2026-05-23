package com.example.Live_Chat.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.Live_Chat.Modal.ChatMessage;
import com.example.Live_Chat.Modal.MessageType;

@Controller
public class ChatController {

    @GetMapping("/")
    public String home() {
        return "chat";
    }

   @MessageMapping("/chat")
    @SendTo("/topic/messages")

    public ChatMessage receiveMessage(ChatMessage message){
        System.out.println("Received message: " + message.getContent());
        return message;
    }

    @MessageMapping("/chat.addUser")
@SendTo("/topic/users")
public ChatMessage addUser(ChatMessage message) {
    message.setType(MessageType.JOIN);
    return message;
}

@MessageMapping("/chat.send")
@SendTo("/topic/messages")
public ChatMessage sendMessage(ChatMessage message) {
    return message;
}
}
