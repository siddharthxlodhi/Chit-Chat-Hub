package com.sid.chitchathub.messages;


import com.sid.chitchathub.file.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
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
                .senderId(message.getSenderID())
                .receiverId(message.getReceiverID())
                .media(message.getMediaFilePath())  //if media type is TEXT it will contain byte[0]
                .build();
    }

}
