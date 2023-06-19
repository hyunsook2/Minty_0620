package com.Reboot.Minty.manager.repository;

import com.Reboot.Minty.manager.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Visitor 테이블 생성 및 값 저장
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

}

