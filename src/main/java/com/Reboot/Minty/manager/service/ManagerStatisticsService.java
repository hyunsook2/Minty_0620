package com.Reboot.Minty.manager.service;

import com.Reboot.Minty.manager.dto.ManagerStatisticsDto;
import com.Reboot.Minty.manager.entity.ManagerStatistics;
import com.Reboot.Minty.manager.repository.ManagerStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerStatisticsService {
    private final ManagerStatisticsRepository managerStatisticsRepository;

    @Autowired
    public ManagerStatisticsService(ManagerStatisticsRepository managerStatisticsRepository) {
        this.managerStatisticsRepository = managerStatisticsRepository;
    }

    public List<ManagerStatisticsDto> getAllManagerStatistics() {
        List<ManagerStatistics> managerStatisticsList = managerStatisticsRepository.findAll();
        return mapToDto(managerStatisticsList);
    }

    private List<ManagerStatisticsDto> mapToDto(List<ManagerStatistics> managerStatisticsList) {
        return managerStatisticsList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ManagerStatisticsDto convertToDto(ManagerStatistics managerStatistics) {
        ManagerStatisticsDto dto = new ManagerStatisticsDto();
        dto.setVisitDate(managerStatistics.getVisitDate());
        dto.setTransaction(managerStatistics.getTransaction());
        dto.setSales(managerStatistics.getSales());
        dto.setVisitor(managerStatistics.getVisitor());
        dto.setRegister(managerStatistics.getRegister());
        dto.setInquiry(managerStatistics.getInquiry());
        dto.setReview(managerStatistics.getReview());
        return dto;
    }

    public ManagerStatisticsDto getManagerStatisticsByDate(LocalDate date) {
        ManagerStatistics managerStatistics = managerStatisticsRepository.findByVisitDate(date);
        return convertToDto(managerStatistics);
    }

    @Transactional
    public ManagerStatisticsDto updateVisitorCountByDate(LocalDate date) {
        ManagerStatistics managerStatistics = managerStatisticsRepository.findByVisitDate(date);

        if (managerStatistics == null) {
            managerStatistics = new ManagerStatistics();
            managerStatistics.setVisitDate(date);
            managerStatistics.setVisitor(1);
            managerStatistics = managerStatisticsRepository.save(managerStatistics);
        } else {
            int visitorCount = managerStatistics.getVisitor() + 1;
            managerStatistics.setVisitor(visitorCount);
        }

        return convertToDto(managerStatistics);
    }

    public ManagerStatistics getStatisticsByVisitDate(LocalDate date) {
        return managerStatisticsRepository.findByVisitDate(date);
    }

    public void saveStatistics(ManagerStatistics statistics) {
        managerStatisticsRepository.save(statistics);
    }
}