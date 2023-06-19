package com.Reboot.Minty.trade.entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="Schedule")
@ToString
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private String hopeArea;

    private DayOfWeek hopeDay;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_duration_id")
    private ScheduleDuration scheduleDuration;

    private String introduction;
}
