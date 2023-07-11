package com.Reboot.Minty.chat.repository;

import com.Reboot.Minty.chat.Entity.ChatRoom;
import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByOtherOrMyOrderByIdDesc(User my, User other);

    boolean existsByMyAndOther(User my, User other);

    boolean existsByOtherAndMy(User other, User my);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.my = :my AND cr.other = :other OR cr.other = :my AND cr.my = :other")
    ChatRoom findByMyAndOtherOrOtherAndMy(@Param("my") User my, @Param("other") User other);
}
