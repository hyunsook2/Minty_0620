package com.Reboot.Minty.member.repository;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);


    User findByNickName(String nickName);

    int countByEmail(String email);

    int countByMobile(String mobile);

    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);

    boolean existsByMobile(String mobile);
    List<User> findByRole(Role role);
    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.name = :name, u.nickName = :nickName, u.ageRange = :ageRange, u.mobile = :mobile, u.gender = :gender WHERE u.id = :userId")
    void updateUserInfo(@Param("userId") int userId, @Param("password") String password, @Param("name") String name, @Param("nickName") String nickName, @Param("ageRange") String ageRange, @Param("mobile") String mobile, @Param("gender") String gender);
}