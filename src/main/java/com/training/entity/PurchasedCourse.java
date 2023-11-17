
package com.training.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class PurchasedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SignUp user;

    @Enumerated(EnumType.STRING)
    private Course course;
    private String duration;
    private double amount;
    private String CourseType;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime purchaseDate;

}
