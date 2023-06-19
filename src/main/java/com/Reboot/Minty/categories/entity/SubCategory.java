package com.Reboot.Minty.categories.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name="sub_categories")
@Getter
@Transactional(readOnly = true)

public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "top_category_id")
    private TopCategory topCategory;
}
