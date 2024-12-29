package com.bonniego.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false, name = "password_hash")
	private String passwordHash; // 使用者Hash密碼
	
	@Column(nullable = false)
	private String salt;
	
	@Column(unique = true, nullable = false)
	private String email;

	@Column(unique = true,nullable = false)
	private String phone;
	
	@Column(nullable = false)
	private String fullname;
	
	@Column(nullable = false)
	private String gender;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String role;

}
