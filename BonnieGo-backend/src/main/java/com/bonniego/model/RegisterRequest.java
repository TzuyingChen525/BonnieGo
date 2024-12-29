package com.bonniego.model;

import lombok.Data;

@Data
public class RegisterRequest {

        private String username;
        private String password;
        private String email;
        private String phone;
        private String fullname;
        private String gender;
        private String address;

}
