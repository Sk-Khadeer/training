package com.training.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.training.entity.Course;
import com.training.entity.SignUp;
import com.training.repository.SignUpRepository;

@Service
public class LoginService {

	@Autowired
	private SignUpRepository signUpRepository;

	public ResponseEntity<Map<String, Object>> login(String email, String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		SignUp login = signUpRepository.findByEmail(email);
		Map<String, Object> response = new HashMap<>();
		if (login == null) {
			response.put("status", "email_not_found");
		} else if ("unconfirmed".equals(login.getStatus())) {
			response.put("status", "Please check your email to confirm your registration before logging in.");
		} 
		else if ("confirmed".equals(login.getStatus()) && passwordEncoder.matches(password, login.getPassword())) {
			response.put("status", "Logged in successfully.");
			System.out.println(response);
		} else {
			response.put("status", "invalid_credentials");
		}
		return ResponseEntity.ok(response);
	}

	public boolean isUserRegisteredForCourse(String email, String string) {

		try {
			Course course = Course.valueOf(string.toUpperCase());
			System.out.println(course);
//		      return signUpRepository.findByEmailAndCourse(email, course) != null;
			return true;
		} catch (IllegalArgumentException ex) {
			// Handle the exception (e.g., log it, return false, or throw a custom
			// exception)
			return false;
		}
	}

}
