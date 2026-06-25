package com.example.gourmetManagementApp.reposities;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findById(long id);
	
	void deleteByRestaurant_Id(Long restaurantId);
	List<Review> findByRestaurant_Id(Long id);
	 List<Review> findByUserId(String userId);

	 void deleteByRestaurantId(long id);

	 @Nullable
	 Object findByRestaurantId(int id);

}