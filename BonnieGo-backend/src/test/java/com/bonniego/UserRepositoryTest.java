package com.bonniego;

import com.bonniego.model.entity.User;
import com.bonniego.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        Optional<User> user = userRepository.findByUsername("user");
        assertTrue(user.isPresent(), "User should exist");
        System.out.println("找到的用戶: " + user.get().getUsername());
    }
}

