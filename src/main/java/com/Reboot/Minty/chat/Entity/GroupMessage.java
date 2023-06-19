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
@Table(name="group_messages")
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="messages")
    private String message;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups groupsId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name="created_dateTime")
    private LocalDateTime createTime;

}
