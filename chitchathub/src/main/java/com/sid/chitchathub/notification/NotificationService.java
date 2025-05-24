package com.sid.chitchathub.notification;


import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public void sendNotification(String receiverId, Notification notification) {
        log.info("Sending notification to {} ", receiverId);
        simpMessagingTemplate.convertAndSendToUser(
                receiverId, "/notification", notification
        );

    }


}
