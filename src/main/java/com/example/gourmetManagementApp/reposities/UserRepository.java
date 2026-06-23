package com.example.gourmetManagementApp.reposities;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.User;
public interface UserRepository extends JpaRepository<User, Long> {

	   Optional<User> findByUserId(String userId);
	

}
