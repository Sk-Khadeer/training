package com.training.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.training.entity.CourseAdditionRequest;
import com.training.entity.SignUp;
import com.training.repository.SignUpRepository;
import com.training.service.LoginService;
import com.training.service.SignUpService;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TraineeController {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private SignUpService signUpService;
	
	@Autowired
	private SignUpRepository signUpRepository;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignUp signup) {
		try {
			signUpService.register(signup);
			return new ResponseEntity<>("Signup successful!", HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<>("There was an issue with registration. Please try again.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getUserCourses")
	public List<CourseAdditionRequest> getUserCourses(@RequestParam String email) {
		return signUpService.findCourse(email);

	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String password = body.get("password");
		// Directly return the ResponseEntity you get from the service
		return loginService.login(email, password);
	}

	@GetMapping("/confirm")
	public ResponseEntity<Map<String, String>> confirmEmail(@RequestParam("token") String token) {
		  SignUp user = signUpRepository.findByConfirmationTokenForLogin(token);
		    Map<String, String> response = new HashMap<>();
		    
		    if (user == null) {
		        response.put("status", "Invalid confirmation link.");
		        return ResponseEntity.badRequest().body(response);
		    }		    
		    else if (!"confirmed".equals(user.getStatus())) {
		        user.setStatus("confirmed");
		        signUpRepository.save(user);
		    }
		   
		    response.put("status", "Success"); // Generic success message
		    return ResponseEntity.ok(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
		String email = body.get("email");

		return signUpService.forgotPassword(email);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
		String token = body.get("token");
		String newPassword = body.get("newPassword");
		return signUpService.resetPassword(token, newPassword);

	}

	@PostMapping("/check-expired-courses")
	public ResponseEntity<?> checkExpiredCourses(@RequestBody Map<String, String> body) {
		String userEmail = body.get("email");

		return signUpService.checkCourseExpired(userEmail);
	}

	@PostMapping("/validateUser")
	public ResponseEntity<?> validateUser(@RequestBody Map<String, String> body) {
		String userEmail = body.get("email");

		return signUpService.validateUser(userEmail);

	}

	@PostMapping("/check-course-registration")
	public ResponseEntity<?> checkRegistration(@RequestBody CourseRequest courseRequest) {

		boolean isRegistered = loginService.isUserRegisteredForCourse(courseRequest.getEmail(),
				courseRequest.getCourse());

		return ResponseEntity.ok(new RegistrationResponse(isRegistered));
	}

	// Create a DTO for the request payload
	public static class CourseRequest {
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getCourse() {
			return course;
		}

		public void setCourse(String course) {
			this.course = course;
		}

		private String email;
		private String course; // Change this to a String

	}

	// Create a DTO for the response
	public static class RegistrationResponse {
		private boolean isRegistered;

		public RegistrationResponse(boolean isRegistered) {
			super();
			this.isRegistered = isRegistered;
		}

		public boolean isRegistered() {
			return isRegistered;
		}

		public void setRegistered(boolean isRegistered) {
			this.isRegistered = isRegistered;
		}

		// constructor, getters and setters
	}

	@PostMapping("/addCourse")
	public ResponseEntity<?> addCourse(@RequestBody CourseAdditionRequest request) {

		try {
			signUpService.purchaseCourse(request);
			return new ResponseEntity<>("Course added Successfully!", HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<>("There was an issue while purchasing course. Please try again.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
