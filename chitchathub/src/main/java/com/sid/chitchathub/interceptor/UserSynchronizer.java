package com.sid.chitchathub.interceptor;

import com.sid.chitchathub.user.User;
import com.sid.chitchathub.user.UserMapper;
import com.sid.chitchathub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void synchronizeUserWithIdp(Jwt token) {
        log.info("Synchronizing user with idp");
        Optional<String> email = extractUserEmail(token);
        email.ifPresent(s -> {
            Optional<User> user = userRepository.findByEmail(s);
            if (user.isPresent()) {
                user.get().setLastSeen(LocalDateTime.now());
                userRepository.save(user.get());
                log.info("Updated last seen for existing user with email {}", email);
            } else {
                User user1 = userMapper.fromToken(token.getClaims());
                userRepository.save(user1);
                log.info("New user created with email {}", email);
            }
        });
    }


    //Extracting email from token
    private Optional<String> extractUserEmail(Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (claims.containsKey("email")) {
            return Optional.of(claims.get("email").toString());
        }
        return Optional.empty();
    }
}
