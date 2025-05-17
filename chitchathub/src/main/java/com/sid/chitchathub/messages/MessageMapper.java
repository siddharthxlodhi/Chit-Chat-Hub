package com.sid.chitchathub.messages;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageMapper {

    public MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType())
                .state(message.getState())
                .createdAt(message.getCreatedDate())
                //todo media file
                .build();
    }

}
