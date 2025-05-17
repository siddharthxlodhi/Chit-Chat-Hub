package com.sid.chitchathub.chat;

import org.springframework.stereotype.Service;

@Service
public class ChatMapper {
    public ChatResponse toChatResponse(Chat chat, String currentUserId) {
        return ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getChatName(currentUserId))
                .unreadCount(chat.getUnReadMessageCount(currentUserId))
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getlastMessageTime())
                .isRecipientOnline(chat.isRecipientOnline(currentUserId))
                .senderId(chat.getSender().getId())
                .receiverId(chat.getReceiver().getId()).build();
    }
}
