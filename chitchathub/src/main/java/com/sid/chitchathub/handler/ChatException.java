package com.sid.chitchathub.handler;

import lombok.*;

@Getter

public class ChatException extends RuntimeException {

    public ChatException(String message) {
        super(message);
    }

}
