package com.Reboot.Minty.manager.repository;

import com.Reboot.Minty.manager.entity.ManagerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ManagerStatisticsRepository extends JpaRepository<ManagerStatistics, LocalDate> {
    ManagerStatistics findByVisitDate(LocalDate date);
    // Add other methods as needed
}