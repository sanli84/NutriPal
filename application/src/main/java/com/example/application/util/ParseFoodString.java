package com.example.application.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ParseFoodString {
    public static List<List<String>> parseFood(String str){
        List<List<String>> foodsList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(str);

            // 获取 foods 对象
            JSONObject foodsObject = jsonObject.getJSONObject("foods");

            // 获取 food 数组
            JSONArray foodArray = foodsObject.getJSONArray("food");

            // 遍历 food 数组
            for (int i = 0; i < foodArray.length(); i++) {
                // 获取每个食物的 JSON 对象
                JSONObject foodObject = foodArray.getJSONObject(i);
                List<String> foodInfo = new ArrayList<>();
                // 从食物对象中获取食物信息
                String foodName = foodObject.getString("food_name");
                String foodDescription = foodObject.getString("food_description");
                foodInfo.add(foodName);
                foodInfo.add(foodDescription);
                foodsList.add(foodInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return foodsList;
    }

    public static List<String> getFoodsStrings(List<List<String>> foodsList){
        List<String> arr = new ArrayList<>();
        for (List<String> innerList : foodsList) {
            // 打印内部列表的元素
            String items = "";
            for (String item : innerList) {
                items = items + " " + item;
            }
            Log.d("tag:llxl", items);
            arr.add(items);
        }
        return arr;
    }
}
