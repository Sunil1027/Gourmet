package com.example.gourmetManagementApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.gourmetManagementApp.reposities.UserRepository; 

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	@Autowired
	UserRepository userRepository;
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    //パスワードハッシュ化
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //アクセス制御
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.csrf(csrf -> csrf.disable());
            
           http.authorizeHttpRequests((request) -> request
                .requestMatchers("/", "/login", "/register").permitAll()     // ログイン・登録は誰でもOK
                .requestMatchers("/js/**", "/css/**", "/img/**").permitAll() // 画像やデザイン崩れを防止
                .requestMatchers("/error").permitAll()                       // エラー画面の表示を許可
                .requestMatchers("/admin/**").hasRole("ADMIN")                  // 管理者だけ！
                .anyRequest().authenticated()                                // それ以外はログイン必須
            );

            return http.build();
        }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return username -> userRepository.findByUserId(username)
            .map(user -> org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())   // エンティティのIDを取得
                .password(user.getPassword())     // エンティティの暗号化パスワードを取得
                .authorities(user.getAuthority()) // 権限（ROLE_USERやROLE_ADMIN）を取得
                .build())
            .orElseThrow(() -> new org.springframework.security.core.userdetails.
            		UsernameNotFoundException("ユーザー名が見つかりません: " + username));
    }
    
    //CommandLineRunner　アプリ起動後、一度だけ実行
    @Bean
    public CommandLineRunner initDummyAccounts(PasswordEncoder passwordEncoder) {
        return args -> {
 
            // userテーブルにデータがあるかを見る
            String countSql = "SELECT COUNT(*) FROM users";
            Integer userCount = jdbcTemplate.queryForObject(countSql, Integer.class);
 
            //0人ならadminを新規作成
            if (userCount != null && userCount == 0) {
                String insertSql = "INSERT INTO users (id, user_id, password, authority) VALUES (?, ?, ?, ?)";
                
                //admin(管理者)作成
                String adminPass = passwordEncoder.encode("1234"); //パスワードハッシュ化
                jdbcTemplate.update(insertSql, 1, "admin", adminPass, "ROLE_ADMIN");
                
                //user(一般)作成
                String userPass = passwordEncoder.encode("1234"); //パスワードハッシュ化
                jdbcTemplate.update(insertSql, 2, "user", userPass, "ROLE_USER");
                
                System.out.println("adminを作成");
            } else {
                System.out.println("すでにアカウントが存在するため、自動作成なし");
                
            }
        };
    }
}
    