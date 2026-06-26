package com.example.gourmetManagementApp.controller;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.gourmetManagementApp.entities.Review;
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
	 * 全レビュー一覧画面を表示（ユーザー名・店舗名連動） 
	 * GET /admin//allReviews
	 */
	@GetMapping("/allReviews")
	public ModelAndView showAllReviewList(ModelAndView mav) {
		java.util.List<com.example.gourmetManagementApp.entities.Review> allReviews = reviewRepository.findAll();
		
		mav.addObject("reviews", allReviews);
		
		mav.setViewName("allReviews"); 
		return mav;
	}

	
	
	
	/**
	 * レビュー編集画面を表示 GET /allReview/edit/{id}
	 */
	@RequestMapping(value = "/allReview/edit/{id}", method = RequestMethod.GET)
	public ModelAndView showReviewEdit(@PathVariable int id, ModelAndView mav) {

		mav.setViewName("allReviewEdit");
		mav.addObject("title", "レビュー編集");

		Optional<Review> data = reviewRepository.findById(id);

		mav.addObject("review", data.get());

		return mav;
	}
	
	/**
	 * レビュー更新処理を実行 POST /admin/allReview/update
	 */
	@RequestMapping(value = "/allReview/update", method = RequestMethod.POST)
		public ModelAndView updateMyReview(Review review, ModelAndView mav) {

		    Review oldReview = reviewRepository.findById(review.getId()).orElse(null);

		    if (oldReview != null) {
		        oldReview.setRating(review.getRating());
		        oldReview.setComment(review.getComment());

		        reviewRepository.save(oldReview);
		    }

		    mav.setViewName("redirect:/admin/allReviews");
		    return mav;
		}
	
	/**
	 * レビュー削除確認画面を表示
	 * GET /admin/allReview/delete/{id}
	 */
	@RequestMapping(value = "/allReview/delete/{id}", method = RequestMethod.GET)
	public ModelAndView showDeleteReview(
	        @PathVariable Long id,
	        ModelAndView mav) {

	    Review review = reviewRepository.findById(id).orElse(null);

	    mav.setViewName("allReviewDelete");
	    mav.addObject("review", review);

	    return mav;
	}


	/**
	 * レビュー削除処理を実行
	 * POST /admin/allReview/delete/{id}
	 */
	@PostMapping("/allReview/delete/{id}")
	public String deleteMyReview(@PathVariable Long id) {

	    reviewRepository.deleteById(id);

	    return "redirect:/admin/allReviews";
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
	
	@GetMapping("/users/back-to-admin")
	public String backToAdmin(jakarta.servlet.http.HttpSession session) {
	    // 🚪 なりすまし用のセッションデータを削除
	    session.removeAttribute("isSwitchUser");
	    session.removeAttribute("adminName");
	    System.out.println("セッション削除");
	    
	    //再認証不要

	    // セッション削除が完了したら、本来のユーザー一覧画面へリダイレクト
	    return "redirect:/admin/users";
	}


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
	@Transactional
	@PostMapping("/restaurant/delete")
	public String deleteRestaurant(@RequestParam("id") Long id) {

		//店のレビューをリストとして持ってくる
		List<Review> reviews = reviewRepository.findByRestaurantId(id);
		
		//レビューが1件でもあるか確認
		if (reviews != null && !reviews.isEmpty()) {
			reviewRepository.deleteAll(reviews); 
		}
		
		//レビューが入ったリストを削除
		restaurantRepository.deleteById(id);
		return "redirect:/restaurant"; 
	}

}