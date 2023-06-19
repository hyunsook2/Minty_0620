package com.Reboot.Minty.manager.controller;

import com.Reboot.Minty.manager.dto.VisitorDto;
import com.Reboot.Minty.manager.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visitors")
public class VisitorController {
    private final VisitorService visitorService;

    @Autowired
    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping
    public ResponseEntity<List<VisitorDto>> getAllVisitors() {
        List<VisitorDto> visitors = visitorService.getAllVisitors();
        return new ResponseEntity<>(visitors, HttpStatus.OK);
    }

    // Add other API endpoints as per your requirements
}
