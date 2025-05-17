package com.sid.chitchathub.chat;

import com.sid.chitchathub.user.User;
import com.sid.chitchathub.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    //This will return all the chats in which user is present as a sender or receiver
    public List<ChatResponse> getChatsByReceiverId(Authentication currentUser) {
        final String currentUserId = currentUser.getName();  //Id By keycloak
        return chatRepository.findBySenderId(currentUserId).stream()
                .map(chat -> chatMapper.toChatResponse(chat, currentUserId))
                .toList();
    }

    //This will create a chat if not already exist ,otherwise return the existing
    public String createChat(String senderId, String receiverId) {
        Optional<Chat> existingChat = chatRepository.findChatBySenderIdAndReceiverId(senderId, receiverId);
        if (existingChat.isPresent()) {
            return existingChat.get().getId();
        }
        User sender = userRepository.findByPublicId(senderId).orElseThrow(() -> new EntityNotFoundException("User with" + senderId + " not found"));
        User receiver = userRepository.findByPublicId(receiverId).orElseThrow(() -> new EntityNotFoundException("User with" + receiverId + " not found"));
        Chat chat = Chat.builder().sender(sender).receiver(receiver).build();
        return chatRepository.save(chat).getId();

    }


}