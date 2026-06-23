package com.example.gourmetManagementApp.reposities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
	// 主キー検索
	Optional<User> findById(long id);
	// ユーザー名(userId)検索
	List<User> findByUserIdContaining(String userId);
	Optional<User> findByUserId(String userId);
	// ユーザー削除
	User deleteById(long id);
}