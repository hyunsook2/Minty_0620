package com.Reboot.Minty.categories.entity;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="top_categories")
@Getter
@Setter
@Transactional(readOnly = true)
@ToString
public class TopCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;


}
