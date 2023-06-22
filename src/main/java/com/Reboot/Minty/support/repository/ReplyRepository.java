package com.Reboot.Minty.support.repository;

import com.Reboot.Minty.support.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    Optional<ReplyEntity> findByUserSupportId(Long userSupportId);

    void deleteByUserSupportId(Long userSupportId);

    Optional<ReplyEntity> findByReportId(Long reportId);

    void deleteByReportId(Long reportId);
}
