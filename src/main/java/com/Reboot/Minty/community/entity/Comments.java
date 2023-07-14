package com.Reboot.Minty.community.entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Getter
@Setter
@DynamicInsert
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_user")
    private User tagUser;

    @Column(nullable = false)
    private String content;

    @Column(name= "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdDate;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedDate;

    @Column(nullable = false)
    private int interesting;
}
