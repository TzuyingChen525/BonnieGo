package com.bonniego.service;

//存在各種商業邏輯的方法，
// 當Controller層呼叫Service的方法後，Service會去呼叫DAO層去取DB裡面的資料；
// 待從DAO層回傳資料後，經過商業邏輯的處理後再還回傳給Controller層。

import com.bonniego.exception.CertException;
import com.bonniego.model.RegisterRequest;
import com.bonniego.model.dto.UserCert;
import com.bonniego.model.entity.Cart;
import com.bonniego.model.entity.Favorite;
import com.bonniego.model.entity.User;
import com.bonniego.repository.CartRepository;
import com.bonniego.repository.FavoriteRepository;
import com.bonniego.repository.UserRepository;
import com.bonniego.util.Hash;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertService certService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    // 1.註冊用戶
    @Transactional
    public void registerUser(RegisterRequest request) {
        // 確認 userRepository不為空正確注入
        if (userRepository == null) {
            throw new IllegalStateException("UserRepository 未正確注入");
        }
        // 檢查是否有重複的用戶名或電子郵件
        Optional<User> userByUsername = userRepository.findByUsername(request.getUsername());
        Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());

        if (userByUsername.isPresent()) {
            throw new IllegalArgumentException("帳號已被使用，請重新輸入註冊帳號");
        }
        if (userByEmail.isPresent()) {
            throw new IllegalArgumentException("電子郵件已被使用，請重新輸入電子郵件");
        }

        // 創建用戶實體並設置基本資料
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullname(request.getFullname());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());

        // 生成鹽值
        String salt = Hash.getSalt();
        // 使用鹽值對密碼進行哈希處理
        String passwordHash = Hash.getHash(request.getPassword(), salt);
        // 設置密碼哈希和鹽值
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);

        // 設置默認角色（USER）
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        // 添加更詳細的日誌輸出來確認保存前的用戶數據是正確的
        System.out.println("保存的用戶: " + user);
        System.out.println("用戶的鹽值: " + user.getSalt());
        System.out.println("用戶的密碼哈希值: " + user.getPasswordHash());


        try {
            // 保存用戶到資料庫
            userRepository.save(user);
            System.out.println("用戶保存成功: " + user.getUsername());
            // 為用戶創建購物車並儲存
            Cart cart = new Cart();
            cart.setUserId(user.getId());
            cartRepository.save(cart);
            System.out.println("用戶購物車ID: " + cart.getId());
            // 為用戶創建收藏區並儲存
            Favorite favorite = new Favorite();
            favorite.setUserId(user.getId());
            favoriteRepository.save(favorite);
            System.out.println("用戶收藏區ID: " + favorite.getId());

        } catch (DataIntegrityViolationException e) {
            System.out.println("(資料庫約束錯誤)用戶名或電子郵件重複："+e);
            throw new IllegalArgumentException("用戶名或電子郵件已被註冊，請更換後再試");
        } catch (Exception e) {
            System.out.println("保存用戶時出現錯誤："+e);
            throw new RuntimeException("用戶註冊失敗，請稍後再試", e);
        }
    }

    // 2.登入會員
    public User loginUser(String username, String password, HttpSession session) throws CertException {
        try {
            System.out.println("開始執行 loginUser 方法...");

            // 查詢用戶
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        System.err.println("錯誤: 無法找到用戶，帳號無效 -> " + username);
                        return new IllegalArgumentException("帳號無效");
                    });
            System.out.println("成功找到用戶: " + user.getUsername());

            // 使用鹽值計算密碼的哈希值
            String passwordHash = Hash.getHash(password, user.getSalt());
            System.out.println("計算出的哈希值: " + passwordHash);

            // 驗證密碼
            if (!passwordHash.equals(user.getPasswordHash())) {
                System.err.println("錯誤: 密碼無效，計算的哈希值與存儲的哈希值不匹配");
                throw new IllegalArgumentException("密碼無效");
            }

            // 發放憑證
            UserCert userCert = certService.getCert(username, password);
            System.out.println("成功生成憑證: " + userCert);

            // 將憑證存入 Session
            session.setAttribute("userCert", userCert);
            System.out.println("憑證已存入 Session: " + session.getAttribute("userCert"));
            System.out.println("存入userCert username: " + userCert.getUsername());
            System.out.println("登入Session ID: " + session.getId());

            return user;

        } catch (IllegalArgumentException ex) {
            System.err.println("參數驗證異常: " + ex.getMessage());
            throw ex; // 保持異常向上拋出
        } catch (CertException ex) {
            System.err.println("憑證生成異常: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            System.err.println("系統未知異常: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("系統錯誤，請稍後再試");
        }
    }

    // 3.登出會員
    public void logoutUser(HttpSession session) {
        // 確保 Session 不為空
        if (session != null) {
            try {
                // 清除 Session 中的資料
                session.invalidate();
                System.out.println("會員已成功登出");
            } catch (IllegalStateException e) {
                // 如果 Session 已經無效，捕捉例外但不阻止操作
                System.out.println("登出操作失敗，Session 已經無效: " + e.getMessage());
            }
        } else {
            System.out.println("無有效的 Session，無需登出");
        }
    }

    // 4.用user id查詢單一會員資料(user頁面)
    public User getUserProfile(HttpSession session) {
        System.out.println("查找會員Session ID: " + session.getId());
        // 從 Session 中取得憑證
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            System.err.println("錯誤: Session 中沒有找到 userCert！Session ID: " + session.getId()+" userCert:"+userCert);
            throw new RuntimeException("發生錯誤: 會員未登入");
        }
        System.out.println("查找單一會員，從 Session 獲取到的憑證: " + userCert);

        // 使用憑證中的 ID 查詢用戶資料
        return userRepository.findById(userCert.getId())
                .orElseThrow(() -> new RuntimeException("發生錯誤: 查無會員"));
    }

    // 5.修改單一會員資料(user頁面按鈕)
    public void updateUserProfile(HttpSession session, User updatedUser) {
        // 從 Session 中取得憑證
        UserCert userCert = (UserCert) session.getAttribute("userCert");
        if (userCert == null) {
            throw new RuntimeException("發生錯誤: 會員未登入");
        }
        // 查詢用戶
        User existingUser = userRepository.findById(userCert.getId())
                .orElseThrow(() -> new RuntimeException("發生錯誤: 查無會員"));

        // 更新允許修改的屬性
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getFullname() != null) {
            existingUser.setFullname(updatedUser.getFullname());
        }
        if (updatedUser.getGender() != null) {
            existingUser.setGender(updatedUser.getGender());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        // 保存更新後的用戶
        userRepository.save(existingUser);
    }

    // 6.刪除會員
//    public void deleteUserProfile(HttpSession session) {
//        // 從 Session 中取得憑證
//        UserCert userCert = (UserCert) session.getAttribute("userCert");
//        if (userCert == null) {
//            throw new RuntimeException("發生錯誤: 會員未登入");
//        }
//        // 刪除用戶
//        userRepository.findById(userCert.getId())
//                .orElseThrow(() -> new RuntimeException("發生錯誤: 查無會員"));
//        userRepository.deleteById(userCert.getId());
//    }


}
