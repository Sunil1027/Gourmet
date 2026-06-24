package com.example.gourmetManagementApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.gourmetManagementApp.entities.Restaurant;
import com.example.gourmetManagementApp.entities.Review;
import com.example.gourmetManagementApp.reposities.RestaurantRepository;
import com.example.gourmetManagementApp.reposities.ReviewRepository;

import jakarta.servlet.http.HttpSession;

@Controller

public class UserController {
	
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	// マイページ表示

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ModelAndView showMyPage(ModelAndView mav) {

		mav.setViewName("user-home");
		mav.addObject("title", "マイページ");

		return mav;
	}
	// 投稿飲食店一覧表示

	@RequestMapping(value = "/users/restaurants", method = RequestMethod.GET)
	public ModelAndView showMyRestaurants(HttpSession session,ModelAndView mav) {

		mav.setViewName("myRestaurants");
		mav.addObject("title", "投稿飲食店一覧");
		String userId = (String) session.getAttribute("loginUserId");

		System.out.println("userId = " + userId);

		List<Restaurant> restaurantList =
		        restaurantRepository.findByUserId(userId);		mav.addObject("restaurantList",restaurantList);
		return mav;
	}
	/**
	 * 飲食店詳細画面表示
	 */
	@RequestMapping(value = "/restaurants/detail/{id}", method = RequestMethod.GET)
	public ModelAndView showRestaurantDetail(
	        @PathVariable Long id,
	        ModelAndView mav) {

	    // 詳細画面
	    mav.setViewName("restaurantdetail");

	    // 飲食店情報取得
	    Optional<Restaurant> data = restaurantRepository.findById(id);

	    // データを画面へ渡す
	    if (data.isPresent()) {
	        mav.addObject("formModel", data.get());
	    }

	    // タイトル表示用
	    mav.addObject("msg", "店舗詳細");

	    return mav;
	}
	// 投稿レビュー一覧

	@RequestMapping(value = "users/reviews", method = RequestMethod.GET)
	public ModelAndView showMyReviews(ModelAndView mav) {

		mav.setViewName("myReviews");
		mav.addObject("title", "投稿レビュー一覧");

		Iterable<Review> list = reviewRepository.findAll();

		mav.addObject("review", list);

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