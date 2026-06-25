package com.example.gourmetManagementApp.reposities;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findById(long id);
	
	void deleteByRestaurantId(Long restaurantId);

	@Override
	void deleteById(Long id);

	
	List<Review> findByRestaurantId(int id);

}