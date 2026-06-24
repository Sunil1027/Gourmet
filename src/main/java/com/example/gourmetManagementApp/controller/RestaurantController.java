package com.example.gourmetManagementApp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.gourmetManagementApp.entities.Restaurant;
import com.example.gourmetManagementApp.entities.Review;
import com.example.gourmetManagementApp.reposities.RestaurantRepository;
import com.example.gourmetManagementApp.service.RestaurantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Controller

@RequestMapping("/restaurant")
public class RestaurantController {
	@Autowired
	RestaurantRepository restaurantRepository;
	@Autowired
	private RestaurantService restaurantService;

	@RequestMapping("")
	public ModelAndView showRestaurant(@ModelAttribute("formModel") Restaurant food, ModelAndView mav) {
		// String userId=Service.getLoginUserId();

		mav.setViewName("restaurants");
		mav.addObject("title", "Restaurant Page");
		// mav.addObject("msg", "This is restaurant page.");
		List<Restaurant> list = restaurantRepository.findAll();

		mav.addObject("formModel", food);
		mav.addObject("data", list);

		ArrayList<String> fieldJapaneseNames = restaurantService.generateJapaneseFieldNames();
		ArrayList<String> fieldNames = restaurantService.generateFieldNames();

		mav.addObject("fieldJapaneseNames", fieldJapaneseNames);
		mav.addObject("fieldNames", fieldNames);
		return mav;
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ModelAndView searchFoods(HttpServletRequest request, ModelAndView mav) {

		mav.setViewName("restaurants"); // 処理後view画面に遷移
		String param = request.getParameter("find_str");

		mav.addObject("keywordValue", param);
		boolean hasWord = !param.isEmpty();
		List<Restaurant> data = new ArrayList<Restaurant>();// 結果を返すリスト

		if (hasWord) {
			data = restaurantService.findRestaurants(param); // とりあえずキーワード検索のみ
			mav.addObject("data", data);
		} else {
			return new ModelAndView("redirect:/restaurant"); // 検索ボタン空うちの場合
		}
		mav.addObject("title", "Find result");
		// mav.addObject("msg", "検索結果");
		// mav.addObject("value", param);
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		mav.addObject("fieldNames", restaurantService.generateFieldNames());

		return mav;
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ModelAndView showRestaurantAddForm(@ModelAttribute("formModel") Restaurant restaurant, ModelAndView mav) {

		mav.setViewName("restaurantAdd");

		mav.addObject("title", "Add Page ");
		mav.addObject("msg", "Restaurant Add Page ");
		mav.addObject("formModel", restaurant);
		// mav.addObject("loginUserId", restaurantService.getLoginUserId());

		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		return mav;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@Transactional
	public ModelAndView registerRestaurant(@ModelAttribute("formModel") @Validated Restaurant restaurant,
			BindingResult result, ModelAndView mav) {
		ModelAndView res = null;
		System.out.println(result.getFieldErrors());
		if (!result.hasErrors()) {
			restaurantRepository.saveAndFlush(restaurant);
			res = new ModelAndView("redirect:/restaurant");
		} else {// バリデーション結果表示
			mav.setViewName("restaurantAdd");
			mav.addObject("title", "Add Page (error)");
			mav.addObject("msg", "sorry, error is occurred...");
			mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
			mav.addObject("fieldNames", restaurantService.generateFieldNames());
			res = mav;
		}
		return res;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public ModelAndView editFood(@ModelAttribute("formModel") Restaurant restaurant, @PathVariable int id,
			ModelAndView mav) {
		mav.setViewName("restaurantEdit");
		mav.addObject("title", "edit Restaurant.");
		Optional<Restaurant> data = restaurantRepository.findById((long) id);
		mav.addObject("formModel", data.get());
		// mav.addObject("loginUserId", restaurantService.getLoginUserId());

		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		return mav;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateFood(@ModelAttribute("formModel") @Validated Restaurant restaurant, BindingResult result,
			ModelAndView mav) {
		ModelAndView res = null;
		System.out.println(result.getFieldErrors());
		if (!result.hasErrors()) {
			System.out.println("UPDATE");
			restaurantRepository.saveAndFlush(restaurant);
			res = new ModelAndView("redirect:/restaurant");
		} else {// バリデーション結果表示
			mav.setViewName("edit");
			mav.addObject("title", "Restaurant Edit Page (error)");
			mav.addObject("msg", "sorry, error is occurred...");

			mav.addObject("formModel", restaurant);
			mav.addObject("fieldNames", restaurantService.generateFieldNames());
			mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
			res = mav;
		}
		return res;
	}

	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public ModelAndView showRestaurantDetail(@ModelAttribute("formModel") Restaurant restaurant, @PathVariable int id,
			ModelAndView mav) {
		mav.setViewName("restaurantDetail");
		mav.addObject("title", "店詳細");

		Optional<Restaurant> data = restaurantRepository.findById((long) id);
		mav.addObject("formModel", data.get());
		// mav.addObject("loginUserId", restaurantService.getLoginUserId());
		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());

		// Controller内でレビュー用の項目リストを用意して送る
		List<String> reviewFields = Arrays.asList("restaurantName", "userName", "evaluation", "createdAt", "comment");
		List<String> reviewJpFields = Arrays.asList("店舗名", "ユーザー名", "評価", "投稿日", "コメント");

		mav.addObject("reviewFieldNames", reviewFields);
		mav.addObject("reviewFieldJapaneseNames", reviewJpFields);

		// 💡 実際はリポジトリからこのお店のレビュー一覧を取得して渡す
		// mav.addObject("reviews", reviewService.findByRestaurantId(id));

		return mav;
	}

	@RequestMapping(value = "/detail/{id}/review/add", method = RequestMethod.GET)
	public ModelAndView showReviewAdd(@ModelAttribute("form") @Validated Review review, @PathVariable Long id,
			ModelAndView mav) {

		mav.setViewName("reviewAdd");
		return mav;
	}

	@RequestMapping(value = "/detail/{id}/review/confirm", method = RequestMethod.POST)
	public ModelAndView confirmReview(@ModelAttribute("form") Review review, @PathVariable Long id, ModelAndView mav) {

		mav.setViewName("reviewConfirm");
		return mav;
	}

	@RequestMapping(value = "/detail/{id}/review/register", method = RequestMethod.POST)
	public ModelAndView registerReview(@ModelAttribute("form") Review review, @PathVariable Long id, ModelAndView mav) {

		mav.setViewName("reviewConfirm");
		return new ModelAndView("redirect:/restaurant");
	}

	@RequestMapping(value = "/testDelete/{id}", method = RequestMethod.GET)
	public ModelAndView showDeleteFood(@PathVariable int id, HttpServletRequest request, ModelAndView mav) {
		mav.setViewName("testDelete");
		mav.addObject("title", "Delete Restaurant.");
		mav.addObject("msg", "Can I delete this record?");
		Optional<Restaurant> data = restaurantRepository.findById((long) id);
		// String loginUserId = restaurantService.getLoginUserId();
		mav.addObject("formModel", data.get());
		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		return mav;
	}

	@RequestMapping(value = "/testDelete", method = RequestMethod.POST)
	@Transactional
	public ModelAndView excuteDeleteFood(@RequestParam long id,
			@RequestHeader(value = "referer", required = false) String referer, ModelAndView mav) {
		restaurantRepository.deleteById(id);
		System.out.println(referer);
		return new ModelAndView("redirect:/restaurant");
	}

}