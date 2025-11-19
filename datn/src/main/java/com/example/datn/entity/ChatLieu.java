package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ChatLieu")
public class ChatLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ChatLieu")
    private Integer id;

    @Column(name = "MaChatLieu")
    private String maChatLieu;

    @Column(name = "TenChatLieu")
    private String tenChatLieu;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}
