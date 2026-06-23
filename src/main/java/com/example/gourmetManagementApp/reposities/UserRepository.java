package com.example.gourmetManagementApp.reposities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gourmetManagementApp.entities.Users;

public interface UserRepository extends JpaRepository<Users, Long>{
	// 主キー検索
	Optional<Users> findById(long id);
	// ユーザー名(userId)検索
	List<Users> findByUserIdContaining(String userId);
	Optional<Users> findByUserId(String userId);
	// ユーザー削除
	Users deleteById(long id);
}