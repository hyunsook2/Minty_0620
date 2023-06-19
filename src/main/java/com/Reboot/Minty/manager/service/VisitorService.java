package com.Reboot.Minty.manager.service;

import com.Reboot.Minty.manager.dto.VisitorDto;
import com.Reboot.Minty.manager.entity.Visitor;
import com.Reboot.Minty.manager.repository.VisitorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitorService {
    private final VisitorRepository visitorRepository;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public List<VisitorDto> getAllVisitors() {
        List<Visitor> visitors = visitorRepository.findAll();
        return visitors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void createVisitor(Visitor visitor) {
        visitorRepository.save(visitor);
    }

    private VisitorDto convertToDto(Visitor visitor) {
        VisitorDto dto = new VisitorDto();
        BeanUtils.copyProperties(visitor, dto);
        return dto;
    }

    // Add other methods as per your requirements (e.g., updateVisitor, deleteVisitor, etc.)
}
