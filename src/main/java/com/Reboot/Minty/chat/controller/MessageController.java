package com.Reboot.Minty.chat.controller;

import com.Reboot.Minty.chat.dto.MessageDTO;
import com.Reboot.Minty.chat.dto.MessageGroupDTO;
import com.Reboot.Minty.chat.service.MessageService;
import com.Reboot.Minty.chat.service.ProductsService;
import com.Reboot.Minty.chat.service.UserAndGroupService;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/getchatting")
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserAndGroupService userAndGroupService;

    @Autowired
    ProductsService productsService;

    @Autowired
    UserService userService;

    @MessageMapping("/chat/{to}")
    public void sendMessagePersonal(@DestinationVariable String to, MessageDTO message) {

        messageService.sendMessage(to,message);

    }

    @GetMapping("listmessage/{from}/{to}")
    public List<Map<String,Object>> getListMessageChat(@PathVariable("from") Integer from, @PathVariable("to") Integer to){
        return messageService.getListMessage(from, to);
    }

    @GetMapping("listProducts/{from}/{to}")
    public List<Map<String,Object>> getListProducts(@PathVariable("from") Integer from, @PathVariable("to") Integer to){
        return productsService.getListProducts(from,to);
    }


    @MessageMapping("/chat/group/{to}")
    public void sendMessageToGroup(@DestinationVariable String to, MessageGroupDTO message) {
        messageService.sendMessageGroup(to,message);

    }

    @GetMapping("listmessage/group/{address}")
    public List<Map<String,Object>> getListMessageGroupChat(@PathVariable("address") String address){
        return messageService.getListMessageGroups(address);
    }

    @GetMapping("/fetchAllUsers/{myId}")
    public List<Map<String,Object>> fetchAll(@PathVariable("myId") String myId) {

        return userAndGroupService.fetchAll(myId);

    }

    @GetMapping("/fetchAllGroups/{groupid}")
    public List<Map<String,Object>> fetchAllGroup(@PathVariable("groupid") String groupId) {
        return  userAndGroupService.fetchAllGroup(groupId);
    }

    @GetMapping("/getUserId")
    public Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userId;
    }
    @GetMapping("/getNumber")
    public String getNumber(HttpSession session) {
        User user = userService.getUserInfoById((Long) session.getAttribute("userId"));
        String mobile = user.getMobile();
        System.out.println(mobile);

        String formattedMobile;
        if (mobile != null && mobile.length() == 10) {
            formattedMobile = "연락처 " + mobile.substring(0, 3) + "-" + mobile.substring(3, 6) + "-" + mobile.substring(6);
        } else if (mobile != null && mobile.length() == 11) {
            formattedMobile = "연락처 " + mobile.substring(0, 3) + "-" + mobile.substring(3, 7) + "-" + mobile.substring(7);
        } else {
            formattedMobile = "연락처 형식이 올바르지 않습니다.";
        }

        return formattedMobile;
    }

}
