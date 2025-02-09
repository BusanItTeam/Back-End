package com.shop.backend.controller;

import jakarta.persistence.Id;

public class user {
    @Id
    private int id;

    private String username;
    private String password;

}
