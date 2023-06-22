package com.Reboot.Minty.support.repository;

import com.Reboot.Minty.support.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findById(Long id);
}
