package com.Reboot.Minty.support.repository;

import com.Reboot.Minty.support.entity.UserSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends JpaRepository<UserSupport, Long> {
    // 이름으로 게시글 목록 조회
    Page<UserSupport> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    // 닉네임으로 게시글 목록 조회
    Page<UserSupport> findAllByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

    // 전체 게시글 목록 조회 (페이징)
    Page<UserSupport> findAll(Pageable pageable);

    // 특정 사용자의 이름으로 게시글 목록 조회
    Page<UserSupport> findByUserIdAndNameContainingIgnoreCase(Long userId, String name, Pageable pageable);

    // 특정 사용자의 닉네임으로 게시글 목록 조회
    Page<UserSupport> findByUserIdAndNicknameContainingIgnoreCase(Long userId, String nickname, Pageable pageable);

    // 특정 사용자의 게시글 목록 조회 (페이징)
    Page<UserSupport> findByUserId(Long userId, Pageable pageable);

    Page<UserSupport> findAllByNameContainingIgnoreCaseAndVerifyReply(String name, String verifyReply, Pageable pageable);

    Page<UserSupport> findAllByNicknameContainingIgnoreCaseAndVerifyReply(String nickname, String verifyReply, Pageable pageable);

    Page<UserSupport> findAllByVerifyReply(String verifyReply, Pageable pageable);

    Page<UserSupport> findByUserIdAndNameContainingIgnoreCaseAndVerifyReply(Long userId, String name, String verifyReply, Pageable pageable);

    Page<UserSupport> findByUserIdAndNicknameContainingIgnoreCaseAndVerifyReply(Long userId, String nickname, String verifyReply, Pageable pageable);

    Page<UserSupport> findByUserIdAndVerifyReply(Long userId, String verifyReply, Pageable pageable);

}
