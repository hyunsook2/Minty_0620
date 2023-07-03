package com.Reboot.Minty.addressCode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AddressCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column
    private String sido;

    @Column
    private String sigungu;

    @Column
    private String dong;

    @Column
    private String lat;

    @Column
    private String lon;



}
