package com.example.gourmetManagementApp.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import com.example.gourmetManagementApp.entities.Restaurant;
import com.example.gourmetManagementApp.reposities.RestaurantRepository;

import jakarta.persistence.Column;

@Service
public class RestaurantService {
	
	public String getLoginUserId() {
        return SecurityContextHolder.getContext()
                                     .getAuthentication()
                                     .getName();
    }
	
	
	public ArrayList<String> generateFieldNames() {
		Field[] allFields = Restaurant.class.getDeclaredFields();

		// 🌟 ただの文字列（String）を入れるArrayListを用意
		ArrayList<String> fieldNames = new ArrayList<>();

		for (Field field : allFields) {
			String fieldName = field.getName();
			// id は画面に入力させないのでスキップ
			if (fieldName.equals("id")) {
				continue;
			}
			// 🌟 見つかった変数名を、そのまま文字として add するだけ！
			fieldNames.add(fieldName);
		}

		return fieldNames;
	}

	public ArrayList<String> generateJapaneseFieldNames() {
		Field[] allFields = Restaurant.class.getDeclaredFields();
		ArrayList<String> japaneseNames = new ArrayList<>();

		for (Field field : allFields) {
			String fieldName = field.getName();

			// id は画面に入力させないのでスキップ
			if (fieldName.equals("id")) {
				continue;
			}
			// 🌟 ① まずデフォルトとして「英語の変数名」を入れておく
			String japaneseName = fieldName;
			// @Column から日本語名（comment）を取得
			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);

				// 🌟 ② comment属性が空（""）じゃない場合「だけ」日本語名に上書き！
				if (!column.comment().isEmpty()) {
					japaneseName = column.comment();
				}
			}

			// リストに追加して次のフィールドへ
			japaneseNames.add(japaneseName);
		}
		return japaneseNames;
	}

	@Autowired
	RestaurantRepository restaurantRepository;

	public List<Restaurant> findRestaurants(String param) {// singleKeyword

		Long fid = 0L;
		List<Restaurant> results = new ArrayList<Restaurant>();
		param = param.trim();

		try {
			fid = Long.parseLong(param);
			Optional<Restaurant> result = restaurantRepository.findById(fid); // ほかの列の数字は部分一致で検索できない
			if (result.isPresent()) {
				results.add(result.get());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (results.isEmpty()) {// もしidでヒットしなかったらほかの列でキーワード検索
			results = restaurantRepository.findByParam(param);
			return results;
		}
		return results;
	}
}