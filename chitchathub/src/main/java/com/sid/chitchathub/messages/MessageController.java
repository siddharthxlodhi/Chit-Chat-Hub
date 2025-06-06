package com.sid.chitchathub.messages;

import com.sid.chitchathub.chat.StringResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Message")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void savemessage(@RequestBody MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
    }

    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StringResponse> uploadMedia(@RequestParam("chat-id") String chatid,
                                                      @RequestParam("file") MultipartFile file,
                                                      Authentication authentication
    ) {
        String imageUrl = messageService.uploadMediaMessage(chatid, file, authentication);
        return new ResponseEntity<>(new StringResponse(imageUrl), HttpStatus.OK);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void setMessagesToSeen(@RequestParam("chat-id") String chatId, Authentication authentication) {
        messageService.setMessagesToSeen(chatId, authentication);

    }

    @GetMapping("/chat/{chat-id}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable("chat-id") String chatId) {
        return new ResponseEntity<>(messageService.getAllMessagesByChatId(chatId), HttpStatus.OK);

    }

}
