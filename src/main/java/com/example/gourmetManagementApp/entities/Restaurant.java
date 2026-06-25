package com.example.gourmetManagementApp.entities;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity

@Table(name = "restaurant")

public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "restaurant_Name", comment = "店名", nullable = false)

	@NotBlank(message = "店名は入力必須です")
	private String restaurantName;

	@Column(name = "genre", comment = "ジャンル")
	private String genre;

	@Column(name = "user_id", comment = "登録者", nullable = false)
	// @NotBlank(message = "登録者は入力必須です")
	private String userId;
	
    //@ManyToOne
	// private User user;

	@Column(name = "open_time", comment = "営業開始時間")
	@DateTimeFormat(pattern = "HH:mm") // ← 時間と分だけに指定
	private LocalTime openTime;

	@Column(name = "close_time", comment = "営業終了時間")
	@DateTimeFormat(pattern = "HH:mm") // ← 時間と分だけに指定

	private LocalTime closeTime;

	@Column(name = "address", comment = "住所", nullable = false)
	@NotBlank(message = "住所は入力必須です")
	private String address;

	@Column(name = "memo", comment = "メモ")
	private String memo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LocalTime getOpenTime() {
		return openTime;
	}

	public void setOpenTime(LocalTime openTime) {
		this.openTime = openTime;
	}

	public LocalTime getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(LocalTime closeTime) {
		this.closeTime = closeTime;
	}


	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// ★ 1:N の関連付け（mappedByを指定）

	// CascadeType.ALL や orphanRemoval は、店が消えたらレビューも自動削除したい場合に付与します
	// @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval
	// = true)
	// private List<Review> reviews;
}
