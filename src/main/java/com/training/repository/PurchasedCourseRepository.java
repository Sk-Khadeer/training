package com.training.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.training.entity.PurchasedCourse;
import com.training.entity.SignUp;
@Repository
public interface PurchasedCourseRepository extends JpaRepository<PurchasedCourse, Long> {

	List<PurchasedCourse> findAllByUser(SignUp user);

	
}