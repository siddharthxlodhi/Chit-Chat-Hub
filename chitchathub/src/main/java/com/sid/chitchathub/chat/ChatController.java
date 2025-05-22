package com.sid.chitchathub.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Chat")
@RestController
@RequestMapping("api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("create")
    public ResponseEntity<StringResponse> createChat(@RequestParam(name = "sender-id") String senderId,
                                                     @RequestParam(name = "receiver-id") String receiverId) {

        final String chatId = chatService.createChat(senderId, receiverId);
        return new ResponseEntity<>(new StringResponse(chatId), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ChatResponse>> getChatByReceiver(Authentication authentication) {
        return new ResponseEntity<>(chatService.getChatsByReceiverId(authentication), HttpStatus.OK);
    }


}
