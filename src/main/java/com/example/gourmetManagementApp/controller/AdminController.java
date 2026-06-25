package com.example.gourmetManagementApp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import com.example.gourmetManagementApp.entities.Restaurant;
import com.example.gourmetManagementApp.reposities.RestaurantRepository;
import com.example.gourmetManagementApp.reposities.ReviewRepository;
import com.example.gourmetManagementApp.service.RestaurantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;


@Controller
public class AdminController {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	@Autowired
	private RestaurantService restaurantService;
	
	
	@RequestMapping(value = "admin/restaurant/delete/{id}", method = RequestMethod.GET)
	public ModelAndView showDeleteRestaurant(@PathVariable int id, HttpServletRequest request, ModelAndView mav) {
		mav.setViewName("restaurantDelete");
		mav.addObject("title", "Delete Restaurant.");
		mav.addObject("msg", "Can I delete this record?");
		Optional<Restaurant> data = restaurantRepository.findById((long) id);
		// String loginUserId = restaurantService.getLoginUserId();

		boolean isAdmin = request.isUserInRole("ADMIN");

		// 「管理者でない」場合のみエラーにする
		if (!isAdmin) {
			// 🛑 【不一致エラー】削除ボタンを押させず、エラーメッセージだけを画面に表示する
			mav.setViewName("deleteResaurant");
			mav.addObject("title", "Delete Error");
			mav.addObject("msg", "エラー！：管理者以外は飲食店を削除できません。");
			mav.addObject("formModel", data.get());
			mav.addObject("fieldNames", restaurantService.generateFieldNames());
			mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
			return mav;
		}

		mav.addObject("formModel", data.get());
		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		return mav;
	}

	@RequestMapping(value = "admin/restaurant/delete", method = RequestMethod.POST)
	@Transactional
	public ModelAndView deleteRestaurant(@RequestParam long id,
			@RequestHeader(value = "referer", required = false) String referer, ModelAndView mav) {

		reviewRepository.deleteByRestaurantId(id); // レストラン削除前にレビュー削除
		restaurantRepository.deleteById(id);
		System.out.println(referer);
		return new ModelAndView("redirect:/restaurant");
	}

	

}
