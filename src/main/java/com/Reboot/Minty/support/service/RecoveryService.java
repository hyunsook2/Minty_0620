package com.Reboot.Minty.support.service;

import com.Reboot.Minty.member.constant.Role;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecoveryService {

    @Autowired
    UserRepository userRepository;

    public void withdrawalDateById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        user.setWithdrawalDateToNow();
        user.setRole(Role.WITHDRAWAL);
        userRepository.save(user);
    }

//    public void recoveryById(String email){
//        User user = userRepository.findByEmail(email);
//        if(user != null) {
//            user.setWithdrawalDate(null);
//            user.setRole(Role.USER);
//            userRepository.save(user);
//        }
//    }

}
