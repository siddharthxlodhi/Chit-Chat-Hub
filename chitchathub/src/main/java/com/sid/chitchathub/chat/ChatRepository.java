package com.sid.chitchathub.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sid.chitchathub.chat.ChatConstant.FIND_CHAT_BY_SENDER_ID;
import static com.sid.chitchathub.chat.ChatConstant.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    @Query(name = FIND_CHAT_BY_SENDER_ID)
    List<Chat> findBySenderId(@Param("senderId") String senderId);

    @Query(name = FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID)
    Optional<Chat> findChatBySenderIdAndReceiverId(@Param("senderId") String senderId,@Param("receiverId") String receiverId);
}
