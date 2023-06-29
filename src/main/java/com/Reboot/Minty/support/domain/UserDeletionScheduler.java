package com.Reboot.Minty.support.domain;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class UserDeletionScheduler {
    private final UserService userService;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public UserDeletionScheduler(UserService userService) {
        this.userService = userService;
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        List<User> users = userService.getUsersWithWithdrawalDateBefore(LocalDate.now().minusMonths(3));
        for (User user : users) {
            deleteUser(user.getId());
        }

        LocalDate withdrawalDate = LocalDate.now().plusMonths(3);
        LocalDateTime nextExecutionTime = withdrawalDate.atStartOfDay();

        Duration initialDelay = Duration.between(LocalDateTime.now(), nextExecutionTime);
        long initialDelayMillis = initialDelay.toMillis();
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);
        scheduler.scheduleAtFixedRate(this::deleteExpiredUsers, initialDelayMillis, oneDayMillis, TimeUnit.MILLISECONDS);
    }

    private void deleteUser(Long id) {
        userService.deleteUserById(id);
    }

    private void deleteExpiredUsers() {
        List<User> users = userService.getUsersWithWithdrawalDateBefore(LocalDate.now().minusMonths(3));
        for (User user : users) {
            deleteUser(user.getId());
        }
    }

    public void stop() {
        scheduler.shutdown();
    }

}
