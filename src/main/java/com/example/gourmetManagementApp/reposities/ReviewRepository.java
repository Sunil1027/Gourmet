package com.example.gourmetManagementApp.reposities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.Review;

import jakarta.annotation.Nullable;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findById(long id);
	
	void deleteByRestaurant_Id(Long restaurantId);
	 List<Review> findByUserId(String userId);

	 void deleteByRestaurantId(long id);



	java.util.List<Review> findByRestaurantId(Long restaurantId);
}