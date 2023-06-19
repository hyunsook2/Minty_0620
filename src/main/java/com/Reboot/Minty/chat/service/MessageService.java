package com.Reboot.Minty.chat.service;

import com.Reboot.Minty.chat.dto.MessageDTO;
import com.Reboot.Minty.chat.dto.MessageGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void sendMessage(String to, MessageDTO message) {

        jdbcTemplate.update("insert into messages (message_text,message_from,message_to,created_date_time) " +
                "values (?,?,?,current_time )",message.getMessage(),message.getFromLogin(),to);

        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);

    }

    public List<Map<String,Object>> getListMessage(@PathVariable("from") Integer from, @PathVariable("to") Integer to){
        return jdbcTemplate.queryForList("select * from messages where (message_from=? and message_to=?) " +
                "or (message_to=? and message_from=?) order by created_date_time asc",from,to,from,to);
    }


    public List<Map<String,Object>> getListMessageGroups(@PathVariable("groupid") Integer groupid){
        return jdbcTemplate.queryForList("select gm.*,us.name as name from group_messages gm " +
                "join user us on us.id=gm.user_id " +
                "where gm.group_id=? order by created_date_time asc",groupid);
    }


    public void sendMessageGroup(Integer to, MessageGroupDTO message) {

        jdbcTemplate.update("INSERT INTO `group_messages`(`group_id`, `user_id`, `messages`, `created_date_time`) " +
                "VALUES (?,?,?,current_timestamp )",to,message.getFromLogin(),message.getMessage());
        message.setGroupId(to);
        simpMessagingTemplate.convertAndSend("/topic/messages/group/" + to, message);

    }



}
