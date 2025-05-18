package com.sid.chitchathub.messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sid.chitchathub.messages.MessageConstant.FIND_MESSAEGES_BY_CHAT_ID;
import static com.sid.chitchathub.messages.MessageConstant.SET_MESSAGE_TO_SEEN_BY_CHAT;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query(name = FIND_MESSAEGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(@Param("chatId") String chatId);

    @Query(name = SET_MESSAGE_TO_SEEN_BY_CHAT)
    @Modifying
    void setAllMessageToSeenByChatId(@Param("chatId") String chatId, @Param(("newState")) MessageState state);

}
