package com.Reboot.Minty.categories.entity;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="sub_categories")
@Getter
@Transactional(readOnly = true)

public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_category_id")
    private TopCategory topCategory;

}
