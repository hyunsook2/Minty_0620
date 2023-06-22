package com.Reboot.Minty.support.entity;

import com.Reboot.Minty.support.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="reply")
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ReplyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String replyTitle;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String replyContent;
    @Column
    private String nickname;
    @JoinColumn(name = "support_id")
    @OneToOne
    private UserSupport userSupport;
    @JoinColumn(name = "report_id")
    @OneToOne
    private Report report;

    @Builder
    public ReplyEntity( String replyContent, String replyTitle, String nickname ) {
        this.replyContent = replyContent;
        this.replyTitle = replyTitle;
        this.nickname = nickname;
    }
}
