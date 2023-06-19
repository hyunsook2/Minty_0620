package com.Reboot.Minty.manager.listener;

import com.Reboot.Minty.manager.dto.VisitorDto;
import com.Reboot.Minty.manager.entity.Visitor;
import com.Reboot.Minty.manager.service.VisitorService;
import com.Reboot.Minty.manager.service.ManagerStatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Component
public class SessionListener implements HttpSessionListener {

    private final VisitorService visitorService;
    private final ManagerStatisticsService managerStatisticsService;

    @Autowired
    public SessionListener(VisitorService visitorService, ManagerStatisticsService managerStatisticsService) {
        this.visitorService = visitorService;
        this.managerStatisticsService = managerStatisticsService;
    }

    @Override
    @Transactional
    public void sessionCreated(HttpSessionEvent se) {
        // 세션이 생성될 때 방문 기록을 DB에 저장
        HttpSession session = se.getSession();
        String ipAddress = getIpAddressFromRequest();
        session.setAttribute("ipAddress", ipAddress);

        Visitor visitor = new Visitor();
        visitor.setIpAddress(ipAddress);
        visitor.setVisitTime(LocalDateTime.now());

        visitorService.createVisitor(visitor);

        // 방문자 수 업데이트
        LocalDate currentDate = LocalDate.now();
        managerStatisticsService.updateVisitorCountByDate(currentDate);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // 세션이 종료될 때 추가적인 작업이 필요한 경우 여기에 구현
    }

    private String getIpAddressFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}