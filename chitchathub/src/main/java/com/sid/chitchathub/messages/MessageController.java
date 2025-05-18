package com.sid.chitchathub.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;
    private final MessageRepository messageRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void savemessage(@RequestBody MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
    }

    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMedia(@RequestParam("chat-id") String chatid,
                            //TODO @Parameter from swagger
                            @RequestParam("file") MultipartFile file,
                            Authentication authentication
    ) {
        messageService.uploadMediaMessage(chatid, file, authentication);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void setMessagesToSeen(@RequestParam("chat-id") String chatId, Authentication authentication) {
        messageService.setMessagesToSeen(chatId, authentication);

    }

    @GetMapping("/chat/{chat-id}")
    public ResponseEntity<List<MessageResponse>> getMessaged(@PathVariable("chat-id") String chatId) {
        return new ResponseEntity<>(messageService.getAllMessagesByChatId(chatId), HttpStatus.OK);

    }

}
