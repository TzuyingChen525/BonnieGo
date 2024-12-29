package com.bonniego.controller;

//負責接收前端的各種http request，並驗證各種請求參數，
//當驗證通過後就會下達指令給Service層的方法說明需要何種資料後，等取得Service層回傳的結果，在透過http response返回前端

import com.bonniego.exception.CertException;
import com.bonniego.model.LoginRequest;
import com.bonniego.model.RegisterRequest;
import com.bonniego.model.UpdateUserRequest;
import com.bonniego.model.dto.UserCert;
import com.bonniego.model.entity.User;
import com.bonniego.repository.UserRepository;
import com.bonniego.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    // 1. 註冊會員
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        System.out.println(request);
        userService.registerUser(request);
        return ResponseEntity.ok("會員註冊成功");
    }

    // 2. 登入會員
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = userService.loginUser(request.getUsername(), request.getPassword(), session);
            System.out.println(user);

            System.out.println("用戶登入成功，返回用戶數據: " + user);

            return ResponseEntity.ok(user); // 返回用戶對象作為 JSON 響應
        } catch (IllegalArgumentException e) {
            System.err.println("登入失敗，錯誤原因: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage())); // 返回 JSON 格式的錯誤
        } catch (RuntimeException e) {
            System.err.println("登入失敗，系統錯誤: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "系統錯誤，請稍後再試")); // 返回 JSON 格式的錯誤
        } catch (CertException e) {
            throw new RuntimeException(e);
        }
    }

    // 3. 登出會員
    @PostMapping("/logout")
    public void logout(HttpSession session) {
        userService.logoutUser(session);
    }

    // 4. 查找單一會員資料
    @GetMapping("/me")
    public ResponseEntity<User> getUserFromSession(HttpSession session) {
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        //確認 Session
        System.out.println("Session ID: " + session.getId());
        System.out.println("UserCert: " + session.getAttribute("userCert"));

        if (userCert == null) {
            System.err.println("未找到 Session 中的 userCert");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userRepository.findById(userCert.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    // 3. 修改單一會員
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(HttpSession session, @RequestBody UpdateUserRequest updatedUser) {

        User user = new User();
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setFullname(updatedUser.getFullname());
        user.setGender(updatedUser.getGender());
        user.setAddress(updatedUser.getAddress());

        userService.updateUserProfile(session, user);
        return ResponseEntity.ok("User updated successfully");
    }

    // 4. 刪除單一會員
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteUser(HttpSession session) {
//        userService.deleteUserProfile(session);
//        return ResponseEntity.ok("User deleted successfully");
//    }

}
