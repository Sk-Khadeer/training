package com.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.training.entity.SignUp;


@Repository
public interface SignUpRepository extends JpaRepository<SignUp, Long> {
	SignUp findByEmail(String email);

	SignUp findByResetPasswordToken(String resetPasswordToken);

	SignUp findByConfirmationTokenForLogin(String token);

	

}