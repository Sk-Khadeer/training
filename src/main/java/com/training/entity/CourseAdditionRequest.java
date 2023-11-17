package com.training.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data

public class CourseAdditionRequest {
	private String email;
	private Course courseName;
	private String duration;
	private double amount;
	private String courseType;
	private LocalDateTime purchaseDate;
}
