package com.inrsystem.dao;

import lombok.Data;

@Data
public class Administrators {
    private String account;
    private String password;
    private Integer id;
    private String name;
    private Integer role;
}
