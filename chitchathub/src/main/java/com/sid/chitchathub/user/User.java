package com.sid.chitchathub.user;

import com.sid.chitchathub.chat.Chat;
import com.sid.chitchathub.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sid.chitchathub.user.UserConstant.FIND_ALL_USERS_EXCEPT_SELF;
import static com.sid.chitchathub.user.UserConstant.FIND_BY_EMAIL;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")

@NamedQuery(
        name = FIND_BY_EMAIL,
        query = "SELECT u FROM User u WHERE u.email= :email")
@NamedQuery(
        name = FIND_ALL_USERS_EXCEPT_SELF,
        query = "SELECT u from User u WHERE u.id!= :publicId"
)
@NamedQuery(
        name = UserConstant.FIND_USER_BY_PUBLIC_ID,
        query = "SELECT u from User u WHERE u.id = :publicId"
)

public class User extends BaseAuditingEntity {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chat> chatAsSender;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chat> chatAsReceiver;

    //“This user was active within the last 5 minutes.”
    public boolean isUserOnline() {
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(5));
    }


}
