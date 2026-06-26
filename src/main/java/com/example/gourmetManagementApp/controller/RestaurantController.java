package com.example.gourmetManagementApp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.example.gourmetManagementApp.reposities.ReviewRepository;
import com.example.gourmetManagementApp.service.RestaurantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Controller

@RequestMapping("/restaurant")
public class RestaurantController {
	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	private RestaurantService restaurantService;

	@RequestMapping("")
	public ModelAndView showRestaurant(@ModelAttribute("formModel") Restaurant restaurant, ModelAndView mav) {
		// String userId=restaurantService.getLoginUserId();

		mav.setViewName("restaurants");
		mav.addObject("title", "Restaurant Page");
		// mav.addObject("msg", "This is restaurant page.");
		List<Restaurant> list = restaurantRepository.findAll();

		mav.addObject("formModel", restaurant);
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
		mav.addObject("loginUserId", restaurantService.getLoginUserId());

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
			restaurant.setUserId(restaurantService.getLoginUserId()); // ログインしているユーザーIDをセット
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
	public ModelAndView editRestaurant(@ModelAttribute("formModel") Restaurant restaurant,
			@RequestHeader(name = "referer", required = false) String referer, @PathVariable int id, ModelAndView mav) {
		mav.setViewName("restaurantEdit");
		System.out.println(referer);
		mav.addObject("cancelUrl", referer);

		mav.addObject("title", "edit Restaurant.");
		Optional<Restaurant> data = restaurantRepository.findById((long) id);
		mav.addObject("formModel", data.get());
		mav.addObject("loginUserId", restaurantService.getLoginUserId());

		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		return mav;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateRestaurant(@ModelAttribute("formModel") @Validated Restaurant restaurant,
			BindingResult result, ModelAndView mav) {
		ModelAndView res = null;
		System.out.println(result.getFieldErrors());
		if (!result.hasErrors()) {
			System.out.println("UPDATE");
			restaurantRepository.saveAndFlush(restaurant);
			res = new ModelAndView("redirect:/restaurant");
		} else {// バリデーション結果表示
			mav.setViewName("restaurantEdit");
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
		mav.addObject("loginUserId", restaurantService.getLoginUserId());
		mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());

		// Controller内でレビュー用の項目リストを用意して送る
		List<String> reviewFields = Arrays.asList("userId", "title", "rating", "createAt", "comment");
		List<String> reviewJpFields = Arrays.asList("投稿者", "タイトル", "評価", "投稿日時", "コメント");

		mav.addObject("reviewFieldNames", reviewFields);
		mav.addObject("reviewFieldJapaneseNames", reviewJpFields);

		// 💡 リポジトリからこのお店のレビュー一覧を取得して渡す
		mav.addObject("reviews", reviewRepository.findByRestaurantId((long)id));

		return mav;
	}

	@RequestMapping(value = "/{id}/review/add", method = RequestMethod.GET)
	public ModelAndView showReviewAdd(@PathVariable Long id,
			@RequestHeader(name = "referer", required = false) String referer, ModelAndView mav) {

		mav.setViewName("reviewAdd");
		System.out.println(referer);
		mav.addObject("cancelUrl", referer);

		Optional<Restaurant> data = restaurantRepository.findById((long) id);
		mav.addObject("restaurant", data.get());
		mav.addObject("loginUserId", restaurantService.getLoginUserId());

		mav.addObject("reviewForm", new Review());

		// 英語のフィールド名リスト（※ restaurant や user、createAt はループから除外）
		List<String> fieldNames = Arrays.asList("userId", "title", "rating", "comment");
		mav.addObject("fieldNames", fieldNames);
		// 日本語のラベル名リスト（上の英語リストと「順番」を完全に一致）
		List<String> fieldJapaneseNames = Arrays.asList("投稿者", "タイトル", "評価（1〜5）", "レビュー内容");
		mav.addObject("fieldJapaneseNames", fieldJapaneseNames);
		return mav;
	}

	@RequestMapping(value = "/{restaurantId}/review/confirm", method = RequestMethod.POST)
	public ModelAndView confirmReview(@ModelAttribute("reviewForm") Review review, BindingResult result,
			@PathVariable Long restaurantId, ModelAndView mav) {

		ModelAndView res = null;
		System.out.println(result.getFieldErrors());
		if (!result.hasErrors()) {

			mav.setViewName("reviewConfirm");
			System.out.println("Confirm");
			// URLの {retaurantId} からレストランのデータを取得
			Optional<Restaurant> data = restaurantRepository.findById((long) restaurantId);
			// 取得したレストランを、これから保存する review オブジェクトに手動でセット（紐付け）
			review.setRestaurant(data.get());

			mav.addObject("restaurant", data.get());
			// review.setCreateAt(new Date());

			List<String> fieldNames = Arrays.asList("userId", "title", "rating", "comment");
			mav.addObject("fieldNames", fieldNames);
			// 日本語のラベル名リスト（上の英語リストと「順番」を完全に一致）
			List<String> fieldJapaneseNames = Arrays.asList("投稿者", "タイトル", "評価（1〜5）", "レビュー内容");
			mav.addObject("fieldJapaneseNames", fieldJapaneseNames);

			res = mav;
		} else {// バリデーション結果表示
			mav.setViewName("reviewAdd");
			mav.addObject("title", "Review Register Page (error)");
			mav.addObject("msg", "sorry, error is occurred...");
//			mav.addObject("reviewForm", review);
//			List<String> fieldNames = Arrays.asList("userId","title", "rating", "comment");
//			mav.addObject("fieldNames", fieldNames);
//
//			List<String> fieldJapaneseNames = Arrays.asList("タイトル", "評価（1〜5）", "レビュー内容");
//			mav.addObject("fieldJapaneseNames", fieldJapaneseNames);	
			res = mav;
		}
		return res;

	}

	@Transactional
	@RequestMapping(value = "/{restaurantId}/review/register", method = RequestMethod.POST)
	public ModelAndView registerReview(@ModelAttribute("reviewForm") Review review, @PathVariable Long restaurantId,
			ModelAndView mav) {

		System.out.println("Register");

		// URLの {retaurantId} からレストランのデータを取得
		Optional<Restaurant> data = restaurantRepository.findById((long) restaurantId);
		// 取得したレストランを、これから保存する review オブジェクトに手動でセット（紐付け）
		review.setRestaurant(data.get());

		//review.setCreateAt(new Date()); // 時刻設定
		
		review.setCreateAt(LocalDateTime.now());
		reviewRepository.saveAndFlush(review);
		return new ModelAndView("redirect:/restaurant");

	}

}