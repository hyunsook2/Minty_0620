package com.Reboot.Minty.support.entity;

import com.Reboot.Minty.support.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "report")
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column
    private String verifyReply;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @Column
    private String reportNickname;

    @Column
    private Long userId;

    @Builder
    public Report(Long id, String title, String name, String content, String verifyReply, String nickname
            , Long userId, String reportNickname){
        this.id = id;
        this.title = title;
        this.name = name;
        this.content = content;
        this.verifyReply = verifyReply;
        this.nickname = nickname;
        this.userId = userId;
        this.reportNickname = reportNickname;
    }

    public void addFile(File file) {
        this.files.add(file);
        file.setReport(this);
    }
}
