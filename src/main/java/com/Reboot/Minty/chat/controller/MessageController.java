package com.Reboot.Minty.chat.controller;

import com.Reboot.Minty.chat.dto.MessageDTO;
import com.Reboot.Minty.chat.dto.MessageGroupDTO;
import com.Reboot.Minty.chat.service.MessageService;
import com.Reboot.Minty.chat.service.UserAndGroupService;
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

    @MessageMapping("/chat/{to}")
    public void sendMessagePersonal(@DestinationVariable String to, MessageDTO message) {

        messageService.sendMessage(to,message);

    }

    @GetMapping("listmessage/{from}/{to}")
    public List<Map<String,Object>> getListMessageChat(@PathVariable("from") Integer from, @PathVariable("to") Integer to){
        return messageService.getListMessage(from, to);
    }

    @MessageMapping("/chat/group/{to}")
    public void sendMessageToGroup(@DestinationVariable Integer to, MessageGroupDTO message) {
        messageService.sendMessageGroup(to,message);

    }

    @GetMapping("listmessage/group/{groupid}")
    public List<Map<String,Object>> getListMessageGroupChat(@PathVariable("groupid") Integer groupid){
        return messageService.getListMessageGroups(groupid);
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


}
