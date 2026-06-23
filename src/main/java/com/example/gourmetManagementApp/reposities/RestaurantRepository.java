package com.example.gourmetManagementApp.reposities;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}
