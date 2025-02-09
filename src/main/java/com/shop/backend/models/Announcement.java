package com.shop.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

//공지사항
@Entity
@Data
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AnnouncementID;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private int views;
}
