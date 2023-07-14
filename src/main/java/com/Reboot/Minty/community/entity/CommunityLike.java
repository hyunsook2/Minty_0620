package com.Reboot.Minty.community.entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "community_like")
@Getter
@Setter
@DynamicInsert
public class CommunityLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @Column(name = "check_status", columnDefinition = "TINYINT(1)")
    private boolean checkStatus;
}
