package com.example.gourmetManagementApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.gourmetManagementApp.entities.User;
import com.example.gourmetManagementApp.reposities.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SecuriutyController {

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 自動で(/login)
	@GetMapping("/")
	public String index() {
		return "redirect:/login";
	}

	// ログインページに遷移
//	@GetMapping("/login")
//	public ModelAndView showTopPage() {
//		return new ModelAndView("login");
//	}

	@RequestMapping("/login")
	public ModelAndView login(ModelAndView mav, @RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "username", required = false) String username, // 💡 POSTの時だけ送られてくる
			@RequestParam(value = "password", required = false) String password, // 💡 POSTの時だけ送られてくる
			HttpServletRequest request) {

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			try {
				// 手動ログインを実行
				request.login(username, password);
				mav.setViewName("redirect:/"); // 成功したらルート(/)へ遷移
				return mav;
			} catch (Exception e) {
				e.printStackTrace();

				mav.setViewName("redirect:/login?error");
				return mav;
			}
		}

		// ログイン中か確認
		if (request.getRemoteUser() != null) {
			if (request.isUserInRole("ADMIN")) {
				mav.setViewName("redirect:/admin/users");
				return mav;
			}
			mav.setViewName("redirect:/user-home");
			return mav;
		}

		// ログイン画面を表示
		mav.setViewName("login");
		mav.addObject("title", "グルメ管理システム");

		if (error != null) {
			mav.addObject("msg", "ログインできませんでした。");
		} else {
			mav.addObject("msg", "新規登録の場合は、ユーザー名とパスワードを記入し、新規登録を押してください。");
		}
		return mav;
	}

	@PostMapping("/register")
	public ModelAndView register(@RequestParam("username") String username, @RequestParam("password") String password,
			HttpServletRequest request, ModelAndView mav) {

		if (username.isBlank() || password.isBlank()) {
			mav.setViewName("redirect:/login");
			return mav;
		}

		// 重複チェック
		if (!repository.findByUserId(username).isEmpty()) {
			mav.setViewName("login");
			mav.addObject("title", "グルメ管理システム");
			mav.addObject("msg", "ユーザー名が重複しています");
			return mav;
		}

		// パスワードハッシュ化、一般ユーザー（ROLE_USER）保存
		String pass = passwordEncoder.encode(password);
		User newUser = new User(username, pass, "ROLE_USER");
		repository.save(newUser);

		// 新規登録後、自動ログイン
		try {
			request.login(username, password);
			mav.setViewName("redirect:/"); // 登録後(/user-home)に遷移
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("msg", "登録完了！");
			mav.setViewName("redirect:/login");
		}

		return mav;

	}

	@GetMapping("/user-home")
	public String showUserHome() {
		return "user-home";
	}

	@GetMapping("/admin/users")
	public String showAdminUserList() {
		return "users";
	}

}
