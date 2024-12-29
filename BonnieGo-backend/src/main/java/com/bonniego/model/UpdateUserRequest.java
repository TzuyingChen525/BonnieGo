package com.bonniego.model;


import lombok.Data;

@Data
public class UpdateUserRequest {
    //可修改的屬性
    private String email;
    private String phone;
    private String fullname;
    private String gender;
    private String address;
}
