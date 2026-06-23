package com.example.gourmetManagementApp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.gourmetManagementApp.entities.Review;
import com.example.gourmetManagementApp.reposities.ReviewRepository;

@Controller

public class UserController {
	
	@Autowired
	private ReviewRepository reviewRepository;

	// マイページ表示

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ModelAndView showMyPage(ModelAndView mav) {

		mav.setViewName("user-home");
		mav.addObject("title", "マイページ");

		return mav;
	}
	// 投稿飲食店一覧表示

	@RequestMapping(value = "/users/restaurants", method = RequestMethod.GET)
	public ModelAndView showMyRestaurants(ModelAndView mav) {

		mav.setViewName("myRestaurants");
		mav.addObject("title", "投稿飲食店一覧");

		return mav;
	}
	// 投稿レビュー一覧

	@RequestMapping(value = "users/reviews", method = RequestMethod.GET)
	public ModelAndView showMyReviews(ModelAndView mav) {

		mav.setViewName("myReviews");
		mav.addObject("title", "投稿レビュー一覧");

		Iterable<Review> list = reviewRepository.findAll();

		mav.addObject("list", list);

		return mav;
	}

	@RequestMapping(value = "users/reviews/edit/{id}", method = RequestMethod.GET)
	public ModelAndView showReviewEdit(@PathVariable int id, ModelAndView mav) {

		mav.setViewName("myReviewEdit");
		mav.addObject("title", "レビュー編集");

		Optional<Review> data = reviewRepository.findById(id);

		mav.addObject("review", data.get());

		return mav;
	}

	// レビュー更新処理

	@RequestMapping(value = "users/reviews/update", method = RequestMethod.POST)
	public ModelAndView updateMyReview(Review review, ModelAndView mav) {

		reviewRepository.save(review);

		mav.setViewName("redirect:/users/reviews");

		return mav;
	}

	/**
	 * レビュー削除確認画面
	 */
	@RequestMapping(value = "users/reviews/delete/{id}", method = RequestMethod.GET)
	public ModelAndView showReviewDelete(@PathVariable int id, ModelAndView mav) {

		mav.setViewName("myReviewDelete");
		mav.addObject("title", "レビュー削除");

		Optional<Review> data = reviewRepository.findById(id);

		mav.addObject("review", data.get());

		return mav;
	}

	/**
	 * レビュー削除処理
	 */
	@RequestMapping(value = "users/reviews/delete", method = RequestMethod.POST)
	public ModelAndView deleteMyReview(Long id, ModelAndView mav) {

		reviewRepository.deleteById(id);

		mav.setViewName("redirect:/users/reviews");

		return mav;
	}
}