package com.training.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SignUp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private String password;
	private String mobileNumber;
	private String confirmationTokenForLogin;
	@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) default 'unconfirmed'")
	private String status = "unconfirmed";
	private String resetPasswordToken;
	private LocalDateTime resetPasswordExpiryToken;

}