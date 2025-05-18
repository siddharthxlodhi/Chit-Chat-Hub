package com.sid.chitchathub.messages;

import com.sid.chitchathub.chat.Chat;
import com.sid.chitchathub.chat.ChatRepository;
import com.sid.chitchathub.file.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.sid.chitchathub.messages.MessageState.SEEN;
import static com.sid.chitchathub.messages.MessageState.SENT;
import static com.sid.chitchathub.messages.MessageType.IMAGE;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageMapper mapper;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final FileService fileService;

    public void saveMessage(MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        Message message = Message.builder()
                .content(messageRequest.getContent())
                .chat(chat)
                .senderID(messageRequest.getSenderId())
                .receiverID(messageRequest.getReceiverId())
                .type(messageRequest.getType())
                .state(SENT).build();
        messageRepository.save(message);
        //todo Notification send
    }

    //This will return all the messages of a particular chat
    public List<MessageResponse> getAllMessagesByChatId(String chatId) {
        return messageRepository.findMessagesByChatId(chatId).stream()
                .map(mapper::toMessageResponse).toList();
    }


    //This will make all the messages to seen and notify other user
    @Transactional
    public void setMessagesToSeen(String chatId, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        final String recipientId = geRecipientId(chat, authentication);

        messageRepository.setAllMessageToSeenByChatId(chatId, SEEN);

        //todo notification

    }

    private String geRecipientId(Chat chat, Authentication authentication) {
        if (chat.getSender().getId().equals(authentication.getName())) {
            return chat.getReceiver().getId();
        }
        return chat.getSender().getId();

    }

    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        final String senderId = authentication.getName();
        final String recipientId = geRecipientId(chat, authentication);

        final String filePath = fileService.saveFile(senderId, file);
        Message message = Message.builder()
                .chat(chat)
                .senderID(senderId)
                .receiverID(recipientId)
                .type(IMAGE)
                .state(SENT)
                .mediaFilePath(filePath)
                .build();
        messageRepository.save(message);

        //todo notification
    }


}
