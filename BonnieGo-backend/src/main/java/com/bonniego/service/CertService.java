package com.bonniego.service;

import com.bonniego.exception.CertException;
import com.bonniego.exception.PasswordInvalidException;
import com.bonniego.exception.UserNotFoundException;
import com.bonniego.model.dto.UserCert;
import com.bonniego.model.entity.User;
import com.bonniego.repository.UserRepository;
import com.bonniego.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertService {

    @Autowired
    private UserRepository userRepository;

    public UserCert getCert(String username, String password) throws CertException {
        // 1.檢查用戶是否存在
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("用戶不存在: " + username));
        // 2.比對密碼
        String passwordHash = Hash.getHash(password, user.getSalt());
        if (!passwordHash.equals(user.getPasswordHash())) {
            throw new PasswordInvalidException("密碼無效");
        }
        // 3. 簽發憑證
        return new UserCert(user.getId(), user.getUsername(), user.getRole());
    }

}
