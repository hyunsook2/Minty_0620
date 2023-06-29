package com.Reboot.Minty.trade.entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
@Entity
@Table(name = "ScheduleDay")
public class ScheduleDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private int sunday;

    private int monday;

    private int tuesday;

    private int wednesday;

    private int thursday;

    private int friday;

    private int saturday;

}
