package com.example.Live_Chat.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// import org.apache.logging.log4j.message.MapMessage;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ChatMessage {
    private String sender;
    private String content;
    private MessageType type;
    private String timeStamp;
}
