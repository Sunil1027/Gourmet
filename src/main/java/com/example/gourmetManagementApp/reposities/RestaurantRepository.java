package com.example.gourmetManagementApp.reposities;


import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.gourmetManagementApp.entities.Restaurant;


@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	@Override
	public Optional<Restaurant> findById(Long num);
	
	
	
	
	@Query("SELECT f FROM Restaurant f WHERE " 
		     + "f.restaurantName LIKE %:word% OR " 
		     + "f.genre LIKE %:word% OR "
		     + "f.memo LIKE %:word% OR " 
		     + "f.userId LIKE %:word%"
		)
	List<Restaurant> findByParam(@Param("word") String param);

}