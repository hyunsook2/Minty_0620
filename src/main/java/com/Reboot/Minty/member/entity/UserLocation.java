package com.Reboot.Minty.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name="userLocation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String latitude;

    private String longitude;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
