package com.sid.chitchathub.messages;

import com.sid.chitchathub.chat.Chat;
import com.sid.chitchathub.chat.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sid.chitchathub.messages.MessageState.SENT;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageMapper mapper;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;


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


    public List<MessageResponse> getAllMessagesByChat(String chatId) {
        return messageRepository.findMessagesByChatId(chatId).stream()
                .map(mapper::toMessageResponse).toList();
    }

    public void setMessagesToSeen(String chatId, Authentication authentication) {

    }


}
