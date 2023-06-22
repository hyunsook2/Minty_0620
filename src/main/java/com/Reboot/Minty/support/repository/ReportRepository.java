package com.Reboot.Minty.support.repository;

import com.Reboot.Minty.support.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 이름으로 게시글 목록 조회
    Page<Report> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    // 닉네임으로 게시글 목록 조회
    Page<Report> findAllByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

    // 전체 게시글 목록 조회 (페이징)
    Page<Report> findAll(Pageable pageable);

    // 특정 사용자의 이름으로 게시글 목록 조회
    Page<Report> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);

    // 특정 사용자의 닉네임으로 게시글 목록 조회
    Page<Report> findByUserIdAndNicknameContainingIgnoreCase(Long userId, String nickname, Pageable pageable);

    // 특정 사용자의 게시글 목록 조회 (페이징)
    Page<Report> findByUserId(Long userId, Pageable pageable);

    Page<Report> findAllByNameContainingIgnoreCaseAndVerifyReply(String name, String verifyReply, Pageable pageable);

    Page<Report> findAllByNicknameContainingIgnoreCaseAndVerifyReply(String nickname, String verifyReply, Pageable pageable);

    Page<Report> findAllByVerifyReply(String verifyReply, Pageable pageable);

    Page<Report> findByUserIdAndNameContainingIgnoreCaseAndVerifyReply(Long userId, String name, String verifyReply, Pageable pageable);

    Page<Report> findByUserIdAndNicknameContainingIgnoreCaseAndVerifyReply(Long userId, String nickname, String verifyReply, Pageable pageable);

    Page<Report> findByUserIdAndVerifyReply(Long userId, String verifyReply, Pageable pageable);
}
