package com.Reboot.Minty.community.repository;

import com.Reboot.Minty.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
}
