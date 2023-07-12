package com.Reboot.Minty.member.entity;

import com.Reboot.Minty.member.constant.UserLocationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;

@Table(name="userLocation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@DynamicInsert
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String latitude;

    private String longitude;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String representativeYN;

    @Enumerated(EnumType.STRING)
    private UserLocationStatus userLocationStatus;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedDate;
}