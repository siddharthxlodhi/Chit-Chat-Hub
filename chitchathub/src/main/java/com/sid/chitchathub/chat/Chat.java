package com.sid.chitchathub.chat;

import com.sid.chitchathub.common.BaseAuditingEntity;
import com.sid.chitchathub.messages.Message;
import com.sid.chitchathub.messages.MessageState;
import com.sid.chitchathub.messages.MessageType;
import com.sid.chitchathub.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sid.chitchathub.chat.ChatConstant.FIND_CHAT_BY_SENDER_ID;
import static com.sid.chitchathub.chat.ChatConstant.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat")
@SuperBuilder


@NamedQuery(
        name = FIND_CHAT_BY_SENDER_ID,   //All the chat where user act as a sender or receiver
        query = "SELECT c FROM Chat c where c.sender.id=: senderId OR c.receiver.id=:senderId ORDER BY createdDate DESC "  //TO DISPLAY ALL LATEST CHATS
)
@NamedQuery(
        name = FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,   //
        query = "SELECT c FROM Chat c where (c.sender.id=:senderId AND c.receiver=:receiverId)"
)

public class Chat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate desc ")
    private List<Message> messages;

    @Transient
    public String getChatName(final String senderId) {   //senderId -- is logined user
        if (senderId.equals(sender.getId())) {
            return receiver.getFirstName() + " " + receiver.getLastName();
        } else {
            return sender.getFirstName() + " " + sender.getLastName();
        }
    }

    @Transient
    public long getUnReadMessageCount(final String senderId) {
        return messages.stream().filter(message -> (message.getReceiverID().equals(senderId) &&
                message.getState() == MessageState.SENT)).count();
    }

    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (messages.get(0).getType() != MessageType.TEXT) {
                return "Attachment";
            } else {
                return messages.get(messages.size() - 1).getContent();
            }
        }
        return null;
    }

    @Transient
    public LocalDateTime getlastMessageTime() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).getCreatedDate();
        }
        return null;
    }

    @Transient
    public boolean isRecipientOnline(final String senderId) {
        if (senderId.equals(sender.getId())) {
            return receiver.isUserOnline();
        }
        return sender.isUserOnline();
    }


}
