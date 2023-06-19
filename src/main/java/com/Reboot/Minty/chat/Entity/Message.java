package com.Reboot.Minty.chat.Entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="message_text")
    private String message;

    @ManyToOne
    @JoinColumn(name = "message_from")
    private User fromId;

    @ManyToOne
    @JoinColumn(name = "message_to")
    private User toId;

    @Column(name="created_dateTime")
    private LocalDateTime createTime;

}
