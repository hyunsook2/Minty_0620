package com.Reboot.Minty.chat.repository;

import com.Reboot.Minty.chat.Entity.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageInfo, Long> {
}
