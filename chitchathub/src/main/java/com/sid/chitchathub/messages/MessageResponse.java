package com.sid.chitchathub.messages;

import lombok.*;
import org.springframework.messaging.handler.MessagingAdviceBean;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageResponse {

    private Long id;
    private String content;
    private MessageType type;
    private MessageState state;
    private LocalDateTime createdAt;
    private byte[] media;

}
