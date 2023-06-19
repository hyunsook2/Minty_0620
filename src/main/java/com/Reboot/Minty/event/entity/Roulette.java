package com.Reboot.Minty.event.entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Roulette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String result;
    private int point;

    public Long getUserId() {
        return user.getId();
    }
}
