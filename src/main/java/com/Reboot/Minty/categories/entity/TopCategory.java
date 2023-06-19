package com.Reboot.Minty.categories.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name="top_categories")
@Getter
@Setter
@Transactional(readOnly = true)
@ToString
public class TopCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

}
