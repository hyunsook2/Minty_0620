package com.Reboot.Minty.chat.service;

import com.Reboot.Minty.chat.Entity.ChatRoom;
import com.Reboot.Minty.chat.repository.ChatRoomRepository;
import com.Reboot.Minty.member.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserAndGroupService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ChatRoomRepository chatRoomRepository;


//    public List<Map<String,Object>> fetchAll(String myId) {
//        List<Map<String,Object>> getAllUser = jdbcTemplate.queryForList("SELECT * FROM chat_room WHERE my = ? OR other = ? ORDER BY id DESC", myId, myId);
//        return getAllUser;
//    }

    public List<Map<String,Object>> fetchAll(String myId) {
        String query = "SELECT cr.id, " +
                "umy.nick_name AS myNickName, " +
                "uother.nick_name AS otherNickName, " +
                "cr.my AS my, " +
                "cr.other AS other, " +
                "tb.title AS title, " +
                "tb.content AS content, " +
                "tb.price AS price, " +
                "tb.thumbnail AS thumbnail " +
                "FROM chat_room cr " +
                "LEFT JOIN user umy ON cr.my = umy.id " +
                "LEFT JOIN user uother ON cr.other = uother.id " +
                "LEFT JOIN tradeboard tb ON cr.trade_board_id = tb.id " +
                "WHERE cr.my = ? OR cr.other = ? " +
                "ORDER BY cr.id DESC";

        List<Map<String,Object>> getAllUser = jdbcTemplate.queryForList(query, myId, myId);

        for (Map<String, Object> row : getAllUser) {
            Long id = (Long) row.get("id");
            String myNickName = (String) row.get("myNickName");
            String otherNickName = (String) row.get("otherNickName");
            Long myIdValue = (Long) row.get("my");
            Long otherIdValue = (Long) row.get("other");
            String title = (String) row.get("title");
            String content = (String) row.get("content");
            int price = (int) row.get("price");
            String thumbnail = (String) row.get("thumbnail");

            System.out.println("ID: " + id + ", My ID: " + myIdValue + ", My Nickname: " + myNickName + ", Other ID: " + otherIdValue + ", Other Nickname: " + otherNickName + ", Title: " + title + ", Content: " + content + ", Price: " + price + ", Thumbnail: " + thumbnail);
        }

        return getAllUser;
    }


    public List<ChatRoom> getChatRoomList(User userId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findByOtherOrMyOrderByIdDesc(userId,userId);

        return chatRoomList;
    }


    public List<Map<String,Object>> fetchAllGroup(String groupId) {
        List<Map<String,Object>> getAllUser=jdbcTemplate.queryForList("SELECT gr.* FROM `groupss` gr " +
                "join group_members gm on gm.group_id=gr.id and gm.user_id=?",groupId);

        return getAllUser;
    }
}
