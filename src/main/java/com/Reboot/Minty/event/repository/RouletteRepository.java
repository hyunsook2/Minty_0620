package com.Reboot.Minty.event.repository;

import com.Reboot.Minty.event.entity.Roulette;
import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouletteRepository extends JpaRepository<Roulette, Long> {
    List<Roulette> findByUser(User user);

    @Query("SELECT SUM(r.point) FROM Roulette r WHERE r.user.id = :userId")
    Integer sumPointByUser(@Param("userId") Long userId);


}
