package com.Reboot.Minty.member.repository;

import com.Reboot.Minty.member.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    UserLocation findByUserId(Long userId);
    long countByUserId(Long userId);

    List<UserLocation> findAllByUserId(Long userId);
}
