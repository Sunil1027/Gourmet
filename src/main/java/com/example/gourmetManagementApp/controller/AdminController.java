package com.example.gourmetManagementApp.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.gourmetManagementApp.entities.User;
import com.example.gourmetManagementApp.reposities.RestaurantRepository;
import com.example.gourmetManagementApp.reposities.ReviewRepository;
import com.example.gourmetManagementApp.reposities.UserRepository;
import com.example.gourmetManagementApp.service.RestaurantService;

import jakarta.transaction.Transactional;

@Transactional
@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	@Autowired
	private RestaurantService restaurantService;
	

	@Autowired
	private UserRepository userRepository;

	/**
	 * ユーザー一覧画面を表示 GET /admin/users
	 */
	@GetMapping("/users")
	public ModelAndView showUserList(ModelAndView mav) {
		mav.addObject("isSearch", false);
		mav.addObject("data", userRepository.findAll());
		mav.setViewName("users");
		return mav;
	}

	/**
	 * ユーザー検索処理を実行 GET /admin/users/search/{param}
	 */

	@GetMapping("/users/search/{param}")
	public ModelAndView searchUsers(@PathVariable("param") String param, ModelAndView mav) {

		mav.addObject("isSearch", true);

		java.util.List<User> resultList = userRepository.findByUserIdContaining(param);

		mav.addObject("data", resultList);
		mav.addObject("msg", "「" + param + "」の検索結果（" + resultList.size() + "件）");

		mav.setViewName("users");
		return mav;
	}

	/**
	 * ユーザー削除確認画面を表示 GET /admin/users/delete/{id}
	 */
	@GetMapping("/users/delete/{id}")
	public ModelAndView showUserDeleteForm(@PathVariable("id") Long id, ModelAndView mav) {
		// 削除対象のユーザー情報を取得して画面に渡す
		userRepository.findById(id).ifPresent(user -> mav.addObject("targetUser", user));

		mav.setViewName("userDelete"); // userDelete.html を表示
		return mav;
	}

	/**
	 * ユーザー削除処理を実行 POST /admin/users/delete/{id}
	 */
	@PostMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Long id) {
		userRepository.deleteById(id);
		return "redirect:/admin/users"; // 削除後は一覧にリダイレクト
	}

	/**
	 * レビュー編集画面を表示 GET /admin/reviews/edit/{id}
	 */
	@GetMapping("/reviews/edit/{id}")
	public ModelAndView showReviewEditForm(@PathVariable("id") Long id, ModelAndView mav) {
		mav.setViewName("reviewEdit");
		return mav;
	}

	/**
	 * レビュー更新処理を実行 POST /admin/reviews/update
	 */
	@PostMapping("/reviews/update")
	public String updateReview() {
		return "redirect:/admin/users";
	}

	/**
	 * レビュー削除処理を実行 POST /admin/reviews/delete/{id}
	 */
	@PostMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable("id") Long id) {
		// TODO: レビュー削除
		return "redirect:/admin/users";
	}

	/**
	 * ユーザー確認ログイン GET /admin/users/login-as/{id}
	 */
	@GetMapping("/users/login-as/{id}")
	public String loginAsUser(@PathVariable("id") Long id, jakarta.servlet.http.HttpSession session) {
		// ユーザーをデータベースから探索
		User targetUser = userRepository.findById(id).orElse(null);

		if (targetUser != null) {

			session.setAttribute("isSwitchUser", true);// 管理者の印
			session.setAttribute("adminName", "admin");

			// 一般ユーザを設定
			session.setAttribute("loginUserId",targetUser.getUserId());

			return "redirect:/user-home";
		}

		return "redirect:/admin/users";

	}


//
//	/**
//	 * 飲食店一覧(管理者)検索を表示 GET /admin/a_restaurants/search
//	 */
//	@GetMapping("/restaurants/search")
//	public ModelAndView searchRestaurants(@RequestParam("keyword") String keyword, ModelAndView mav) {
//		if (keyword != null && !keyword.trim().isEmpty()) {
//			mav.addObject("data", restaurantRepository.findByParam(keyword));
//			mav.addObject("keywordValue", keyword);
//		} else {
//			mav.addObject("data", restaurantRepository.findAll());
//		}
//		mav.setViewName("restaurant");
//		return mav;
//	}

	/**
	 * 飲食店削除確認画面を表示 GET /admin/restaurant/delete/{id}
	 */
	@GetMapping("/restaurant/delete/{id}")
	public ModelAndView showRestaurantDeleteForm(@PathVariable("id") Long id, ModelAndView mav) {
		
	mav.setViewName("restaurantDelete"); 
		mav.addObject("title", "Delete Restaurant.");
		mav.addObject("msg", "このレコードを本当に削除しますか？");
		
		restaurantRepository.findById(id).ifPresent(restaurant -> {
			mav.addObject("formModel", restaurant);
			mav.addObject("targetRestaurant", restaurant); 
		});
				mav.addObject("fieldNames", restaurantService.generateFieldNames());
		mav.addObject("fieldJapaneseNames", restaurantService.generateJapaneseFieldNames());
		
		return mav;
	}

	/**
	 * 飲食店削除処理を実際に実行 POST /admin/restaurants/delete/{id}
	 */
	@jakarta.transaction.Transactional
	@PostMapping("/restaurant/delete/{id}")
	public String deleteRestaurant(@PathVariable("id") Long id) {

		//店のレビューをリストとして持ってくる
		java.util.List<com.example.gourmetManagementApp.entities.
		Review> reviews = reviewRepository.findByRestaurantId(id);
		
		//レビューが1件でもあるか確認
		if (reviews != null && !reviews.isEmpty()) {
			reviewRepository.deleteAll(reviews); 
		}
		
		//レビューが入ったリストを削除
		restaurantRepository.deleteById(id);
		return "redirect:/restaurant"; 
	}

}