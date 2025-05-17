package com.sid.chitchathub.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sid.chitchathub.user.UserConstant.FIND_BY_EMAIL;
import static com.sid.chitchathub.user.UserConstant.FIND_USER_BY_PUBLIC_ID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(name = FIND_BY_EMAIL)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(name = FIND_USER_BY_PUBLIC_ID)
    Optional<User> findByPublicId(String senderId);
}
