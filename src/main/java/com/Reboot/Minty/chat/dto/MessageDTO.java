package com.Reboot.Minty.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private String message;
    private int fromLogin;

    @Override
    public String toString() {
        return "MessageDTO{" +
                "message='" + message + '\'' +
                ", fromLogin=" + fromLogin +
                '}';
    }
}
