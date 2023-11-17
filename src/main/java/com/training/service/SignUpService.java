package com.training.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.training.entity.CourseAdditionRequest;
import com.training.entity.PurchasedCourse;
import com.training.entity.SignUp;
import com.training.repository.PurchasedCourseRepository;
import com.training.repository.SignUpRepository;

@Service
public class SignUpService {

	@Autowired
	private SignUpRepository signUpRepository;

	@Autowired
	private PurchasedCourseRepository purchasedCourseRepository;

	@Autowired
	private EmailService emailService;

	public static final String USER_ROLE = "USER";
	public static final String ADMIN_ROLE = "ADMIN";

	public SignUp register(SignUp register) {
		if (signUpRepository.findByEmail(register.getEmail()) != null) {
			throw new IllegalArgumentException("Email already exists! Please Login.");
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = register.getPassword();
		String hashedPassword = passwordEncoder.encode(rawPassword);
		register.setPassword(hashedPassword);
		register.setConfirmationTokenForLogin(UUID.randomUUID().toString());
		System.out.println("==========================");
		try {
			System.out.println("enetered===================");
//			register = signUpRepository.save(register);
			System.out.println(register.getConfirmationTokenForLogin());
			emailService.sendEmail(register);
			return signUpRepository.save(register);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			throw new DataIntegrityViolationException("There was an issue with registration. Please try again.");
		}
	}

	

	public PurchasedCourse purchaseCourse(CourseAdditionRequest request) {
		SignUp user = signUpRepository.findByEmail(request.getEmail());
		if (user == null) {
			throw new IllegalArgumentException("User Not found");
		}
		try {
			PurchasedCourse details = new PurchasedCourse();
			details.setUser(user);
			details.setCourse(request.getCourseName());
			details.setDuration(request.getDuration());
			details.setAmount(request.getAmount());
			details.setCourseType(request.getCourseType());
			return purchasedCourseRepository.save(details);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			throw new DataIntegrityViolationException("There was an issue while purchasing course. Please try again.");
		}

	}

	public List<CourseAdditionRequest> findCourse(String email) {
		SignUp user = signUpRepository.findByEmail(email);
		List<PurchasedCourse> purchasedCourses = purchasedCourseRepository.findAllByUser(user);

		return purchasedCourses.stream().map(pc -> {
			CourseAdditionRequest dto = new CourseAdditionRequest();
			dto.setCourseName(pc.getCourse());
			dto.setPurchaseDate(pc.getPurchaseDate());
			dto.setDuration(pc.getDuration());
			dto.setCourseType(pc.getCourseType());
			dto.setAmount(pc.getAmount());
			return dto;
		}).collect(Collectors.toList());
	}

	public ResponseEntity<?> forgotPassword(String email) {
		SignUp user = signUpRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}

		// Generate a reset token
		String token = UUID.randomUUID().toString();
		user.setResetPasswordToken(token);
		user.setResetPasswordExpiryToken(LocalDateTime.now().plusHours(24)); // token valid for 24 hours

		try {
			signUpRepository.save(user);
			emailService.sendPasswordResetEmail(email, token);
			return ResponseEntity.ok(Collections.singletonMap("status", "success"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the request.");
		}
	}

	public ResponseEntity<?> resetPassword(String token, String newPassword) {
		// Check if the token is valid
		SignUp user = signUpRepository.findByResetPasswordToken(token);
		if (user == null) {
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("status", "Invalid token."));
		}

		// Check token expiration
		LocalDateTime now = LocalDateTime.now();
		if (user.getResetPasswordExpiryToken().isBefore(now)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired.");
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(hashedPassword);

		user.setResetPasswordToken(null); // Clear the reset token
		user.setResetPasswordExpiryToken(null); // Clear the token expiry date
		signUpRepository.save(user);
//	    return ResponseEntity.ok("success");
		return ResponseEntity.ok(Collections.singletonMap("status", "success"));
	}

	public ResponseEntity<String> checkCourseExpired(String userEmail) {
		// Retrieve user's enrolled courses and their start dates
		SignUp user = signUpRepository.findByEmail(userEmail);
		List<PurchasedCourse> purchasedCourses = purchasedCourseRepository.findAllByUser(user);

		// Iterate over courses and check if they're expired
		for (PurchasedCourse course : purchasedCourses) {
			LocalDate startDate = course.getPurchaseDate().toLocalDate();
			LocalDate currentDate = LocalDate.now();

			LocalDate expiryDate = startDate.plusMonths(Integer.parseInt(course.getDuration()));

			long daysUntilExpiry = ChronoUnit.DAYS.between(currentDate, expiryDate);
			if (daysUntilExpiry <= 10 && daysUntilExpiry > 0) {
				System.out.println(daysUntilExpiry);
				emailService.sendEmailReminder(user, course, expiryDate);
			}

			// If the course duration is 3 months and it has been 3 months since the start
			// date, it's expired
			if (Integer.parseInt(course.getDuration()) == 3 && startDate.plusMonths(3).isBefore(currentDate)) {
				// Remove the course from the user's enrolled courses
				purchasedCourseRepository.deleteById(course.getId());
			} else if (Integer.parseInt(course.getDuration()) == 6 && startDate.plusMonths(6).isBefore(currentDate)) {
				// Remove the course from the user's enrolled courses
				purchasedCourseRepository.deleteById(course.getId());
			}
		}
		return ResponseEntity.ok("Checked and updated expired courses");

	}

	public ResponseEntity<?> validateUser(String email) {

		SignUp user = signUpRepository.findByEmail(email);
		if (user == null) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		} else {
			return ResponseEntity.ok("User is valid");
		}

	}

}