package com.example.gourmetManagementApp.entities;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
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
	private Long id;

	//@ManyToOne
	//private Restaurant restaurant;
	
	
	//@ManyToOne
	//private User user;

	@Column(name = "create_at", comment = "投稿日時")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date createAt;

	@Column(name = "rating", comment = "1~5評価", nullable = false)
	private int rating;

	@Column(name = "title", comment = "タイトル")
	private String title;

	@Column(name = "comment", comment = "レビュー内容")
	private String comment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

	
	

}
