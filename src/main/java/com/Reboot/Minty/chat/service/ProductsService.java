package com.Reboot.Minty.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Service
public class ProductsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getListProducts(@PathVariable("from") Integer from, @PathVariable("to") Integer to) {

        return jdbcTemplate.queryForList("SELECT p.*, t.title, t.content, t.price, t.thumbnail FROM products p " +
                "JOIN tradeboard t ON p.trade_board_id = t.id " +
                "WHERE (p.my = ? AND p.other = ?) OR (p.other = ? AND p.my = ?)" +
                "ORDER BY p.created_date_time DESC", from, to, from, to);

    }
}
