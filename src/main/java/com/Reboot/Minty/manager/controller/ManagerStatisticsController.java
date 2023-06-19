package com.Reboot.Minty.manager.controller;

import com.Reboot.Minty.manager.dto.ManagerStatisticsDto;
import com.Reboot.Minty.manager.service.ManagerStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManagerStatisticsController {

    private final ManagerStatisticsService managerStatisticsService;

    @Autowired
    public ManagerStatisticsController(ManagerStatisticsService managerStatisticsService) {
        this.managerStatisticsService = managerStatisticsService;
    }

    @GetMapping("/getManagerStatistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getManagerStatistics() {
        List<ManagerStatisticsDto> managerStatistics = managerStatisticsService.getAllManagerStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("managerStatistics", managerStatistics);

        return ResponseEntity.ok(response);
    }
}