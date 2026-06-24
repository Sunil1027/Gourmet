package com.example.gourmetManagementApp.reposities;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Optional<Review> findById(long id);

	@Override
	void deleteById(Long id);

}