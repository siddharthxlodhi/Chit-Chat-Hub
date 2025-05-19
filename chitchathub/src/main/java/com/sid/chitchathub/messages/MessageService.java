package com.sid.chitchathub.messages;

import com.sid.chitchathub.chat.Chat;
import com.sid.chitchathub.chat.ChatRepository;
import com.sid.chitchathub.file.FileStorageService;
import com.sid.chitchathub.file.FileUtils;
import com.sid.chitchathub.notification.Notification;
import com.sid.chitchathub.notification.NotificationService;
import com.sid.chitchathub.notification.NotificationType;
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
    private final FileStorageService fileService;
    private final NotificationService notificationService;

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

        //Sending notification

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(messageRequest.getType())
                .content(message.getContent())
                .senderId(message.getSenderID())
                .receiverId(messageRequest.getReceiverId())
                .type(NotificationType.MESSAGE)
                .chatName(chat.getChatName(message.getSenderID()))
                .build();

        notificationService.sendNotification(message.getReceiverID(), notification);
    }

    //This will return all the messages of a particular chat
    public List<MessageResponse> getAllMessagesByChatId(String chatId) {
        return messageRepository.findMessagesByChatId(chatId).stream()
                .map(mapper::toMessageResponse).toList();
    }


    //This will make all the messages to seen by chat id and find recipientId  notify it
    @Transactional
    public void setMessagesToSeen(String chatId, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        final String recipientId = geRecipientId(chat, authentication);

        messageRepository.setAllMessageToSeenByChatId(chatId, SEEN);

        //Sending notification

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .senderId(authentication.getName())
                .receiverId(recipientId)
                .type(NotificationType.SEEN)
                .build();

        notificationService.sendNotification(recipientId, notification);
    }

    //getting geRecipientId based on connected user
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

        //Sending notification

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(IMAGE)
                .senderId(senderId)
                .receiverId(recipientId)
                .type(NotificationType.IMAGE)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();

        notificationService.sendNotification(recipientId, notification);


    }


}
