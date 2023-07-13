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


    public List<Map<String,Object>> fetchAll(String myId) {
        String query = "SELECT cr.id, " +
                "umy.nick_name AS myNickName, " +
                "uother.nick_name AS otherNickName, " +
                "umy.image AS myProfileImage, " +
                "uother.image AS otherProfileImage, " +
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



    public List<Map<String,Object>> fetchAllGroup(String groupId) {
        List<Map<String, Object>> addresses = jdbcTemplate.queryForList("SELECT ul.address, ul.user_id, u.nick_name FROM user_location ul JOIN user u ON ul.user_id = u.id WHERE ul.user_id = ?", groupId);

        return addresses;
    }
}