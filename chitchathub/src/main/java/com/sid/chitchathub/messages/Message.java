package com.sid.chitchathub.messages;

import com.sid.chitchathub.chat.Chat;
import com.sid.chitchathub.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.sid.chitchathub.messages.MessageConstant.FIND_MESSAEGES_BY_CHAT_ID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "messages")

@NamedQuery(
        name = FIND_MESSAEGES_BY_CHAT_ID,
        query = "SELECT m FROM Message m WHERE m.chat.id= :chatId ORDER BY m.createdDate "
)
@NamedQuery(
        name = MessageConstant.SET_MESSAGE_TO_SEEN_BY_CHAT,
        query = "UPDATE Message SET state=:newState WHERE chat.id= :chatId "
)

public class Message extends BaseAuditingEntity {

    @Id
    @SequenceGenerator(name = "msg_seq", sequenceName = "msg_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq")
    private Long id;

    @Column(columnDefinition = "TEXT")  //no limit like VARCHAR(255)
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageState state;
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "sender_id", nullable = false)
    private String senderID;

    @Column(name = "receiver_id", nullable = false)
    private String receiverID;

    private String mediaFilePath;

}
