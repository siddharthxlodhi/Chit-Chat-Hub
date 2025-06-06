package com.sid.chitchathub.messages;

import lombok.*;

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
    private String media;
    private String senderId;
    private String receiverId;
}
