package com.Reboot.Minty.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageGroupDTO extends MessageDTO {
    private String address;
    private String nick_name;

}
