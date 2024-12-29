package com.bonniego.model.dto;

import lombok.*;

// 使用者憑證
// 登入成功後會得到的憑證資料(只有getter)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class UserCert {

    private Long id; // 使用者ID
    private String username; // 使用者名稱
    private String role; // 角色權限

}

