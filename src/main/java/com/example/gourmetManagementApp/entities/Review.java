package com.example.gourmetManagementApp.entities;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String shop_name;
	private int rating;
	private String comment;
	private LocalTime created_at;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public LocalTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalTime created_at) {
		this.created_at = created_at;
	}
}
