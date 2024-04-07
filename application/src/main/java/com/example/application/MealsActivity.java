package com.example.application;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.application.database.NutriPalDBHelper;
import com.example.application.entity.Food;
import com.example.application.entity.User;
import com.example.application.util.ParseFoodString;
import com.example.application.util.ToastUtil;
import com.example.application.util.calclateDailyCaloriesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MealsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private String accessToken;
    private Dialog dialogAddFood;
    private List<String> foodsInfosList;
    private Button addFood_search_btn;
    private Spinner addFood_spinner;
    private Button addFood_Add_btn;
    private NutriPalDBHelper mHelper;
    private EditText addFood_quantity_et;
    private String current_category;
    private TextView caloriesDaily_tv;
    private TextView caloriesLunchNum_tv;
    private TextView caloriesBreakfastNum_tv;
    private TextView caloriesDinnerNum_tv;
    private TextView caloriesIngest_tv;
    private TextView calories_left;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        RadioGroup guide_rg = findViewById(R.id.meals_guide_rg);
        guide_rg.setOnCheckedChangeListener(this);
        RadioButton meals_diary_btn = findViewById(R.id.meals_diary_btn);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable meals_btn_drawable = getDrawable(R.drawable.guide_diary_selected);
        meals_diary_btn.setBackground(meals_btn_drawable);
        dialogAddFood = new Dialog(this);
        dialogAddFood.setContentView(R.layout.addfood_dialog);
        addFood_search_btn = dialogAddFood.findViewById(R.id.addFood_search_btn);
        addFood_search_btn.setOnClickListener(this);
        addFood_spinner = dialogAddFood.findViewById(R.id.addFood_spinner);
        addFood_Add_btn = dialogAddFood.findViewById(R.id.addFood_Add_btn);
        addFood_Add_btn.setOnClickListener(this);
        addFood_quantity_et = dialogAddFood.findViewById(R.id.addFood_quantity_et);
        caloriesDaily_tv = findViewById(R.id.caloriesDaily_tv);
        caloriesIngest_tv = findViewById(R.id.caloriesIngest_tv);
        calories_left = findViewById(R.id.calories_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = NutriPalDBHelper.getInstance(this);
        mHelper.openWriteLink();
        mHelper.openReadLink();
        updatePage();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.meals_home_btn) {
            startActivity(new Intent(MealsActivity.this, HomeActivity.class));
        } else if (checkedId == R.id.meals_profile_btn) {
            startActivity(new Intent(MealsActivity.this, ProfileActivity.class));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void requestAccessToken(String foodName) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                String clientID = "004a1fb64c1f4064a816667ec3e3512b";
                String clientSecret = "92ae5817f9da4811b8fe53ae1d1eb349";

                // 构建请求体
                RequestBody requestBody = new FormBody.Builder().add("grant_type", "client_credentials").add("client_id", clientID).add("client_secret", clientSecret).add("scope", "basic").build();

                // 构建请求
                Request request = new Request.Builder().url("https://oauth.fatsecret.com/connect/token").post(requestBody).build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        return responseBody;
                    } else {
                        Log.d("tag:llxl", "Request failed: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        accessToken = json.getString("access_token");
                        Log.d("tag:llxl", "JSON content: " + json.toString());
                        // 在这里处理获取到的令牌
                        Log.d("tag:llxl", "Access token: " + accessToken);
                        callApiWithToken(accessToken, foodName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    callApiWithToken(accessToken);
                } else {
                    Log.e("tag:llxl", "Failed to receive access token");
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void callApiWithToken(String accessToken, String foodName) {
        // 异步任务：调用 API
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... tokens) {
                OkHttpClient client = new OkHttpClient();


                RequestBody requestBody = new FormBody.Builder().add("method", "foods.search").add("format", "json").add("max_results", "10").add("search_expression", foodName).build();

                // 构建请求
                Request request = new Request.Builder().header("Authorization", "Bearer " + accessToken)// 设置访问令牌
                        .url("https://platform.fatsecret.com/rest/server.api").post(requestBody).build();
                Log.d("tag:llxl", String.format("request:%s", request));

                try {
                    // 发送请求并获取响应
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();  // 返回响应体
                    } else {
                        Log.d("tag:llxl", "API request failed: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (result != null) {
                    // 处理 API 响应
                    Log.d("tag:llxl", "API response: " + result);
                    List<List<String>> foodsList = ParseFoodString.parseFood(result);
                    foodsInfosList = ParseFoodString.getFoodsStrings(foodsList);
                    updateSpinnerAdapter(foodsInfosList);
                    Log.d("tag:llxl", foodsInfosList.toString());
                } else {
                    Log.e("tag:llxl", "Failed to call API");
                }
            }
        }.execute(accessToken);
    }

    private void updateSpinnerAdapter(List<String> foodsList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, foodsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addFood_spinner.setAdapter(adapter);
    }

    public void addBreakfast(View view) {
        current_category = "breakfast";
        dialogAddFood.show();

    }

    public void addLunch(View view) {
        current_category = "lunch";
        dialogAddFood.show();

    }

    public void addDinner(View view) {
        current_category = "dinner";
        dialogAddFood.show();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addFood_search_btn) {
            EditText addFood_foodName_et = dialogAddFood.findViewById(R.id.addFood_foodName_et);
            String foodName = addFood_foodName_et.getText().toString();
            requestAccessToken(foodName);
        } else if (v.getId() == R.id.addFood_Add_btn) {
            String selectedFood = addFood_spinner.getSelectedItem().toString();
            Log.d("tag:llxl", "selected: " + selectedFood);
            double foodQuantity = Double.parseDouble(addFood_quantity_et.getText().toString());
            if (mHelper.storeFood(selectedFood, foodQuantity, current_category) > 0) {
                ToastUtil.show(this, "Add food successfully!");
            } else ToastUtil.show(this, "Add food unsuccessfully!");
            dialogAddFood.dismiss();
            updatePage();
        }
    }

    private TextView getTextViewById(Context context, String id) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(id, "id", context.getPackageName());
        TextView textView = new TextView(context);
        if (resId != 0) {
            textView = findViewById(resId);
        } else {
            // 如果未找到对应的资源ID，则显示错误信息
            return null;
        }
        return textView;
    }


    private void updatePage() {
        List<List<Food>> foodList = mHelper.getFoodsToday();
        List<Food> breakfastList = foodList.get(0);
        int breakfastNum = breakfastList.size();
        for (int i = 0; i < breakfastNum; i++) {

            Food tempFood = breakfastList.get(i);
            @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
            String foodComponentID = String.format("breakfast_food_%s", i + 1);

            TextView tempTV = getTextViewById(this, foodComponentID);
            if (tempTV != null) {
                tempTV.setText(tempFoodString);
            }
        }

        List<Food> lunchList = foodList.get(1);
        int lunchNum = lunchList.size();
        for (int i = 0; i < lunchNum; i++) {

            Food tempFood = lunchList.get(i);
            @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
            String foodComponentID = String.format("lunch_food_%s", i + 1);

            TextView tempTV = getTextViewById(this, foodComponentID);
            if (tempTV != null) {
                tempTV.setText(tempFoodString);
            }
        }

        List<Food> dinnerList = foodList.get(2);
        int dinnerNum = dinnerList.size();
        for (int i = 0; i < dinnerNum; i++) {

            Food tempFood = dinnerList.get(i);
            @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
            String foodComponentID = String.format("dinner_food_%s", i + 1);

            TextView tempTV = getTextViewById(this, foodComponentID);
            if (tempTV != null) {
                tempTV.setText(tempFoodString);
            }
        }

        updateBreakfast();
        updateLunch();
        updateDinner();
        User currentUser = mHelper.getUser(mHelper.getCurrentUsername());
        int caloriesDailyNeeded = calclateDailyCaloriesUtil.calculateCalories(currentUser.target_weight, currentUser.height, currentUser.age);
        caloriesDaily_tv.setText(String.valueOf(caloriesDailyNeeded));
        int caloriesBreakfast = Integer.parseInt(caloriesBreakfastNum_tv.getText().toString());
        int caloriesLunch = Integer.parseInt(caloriesLunchNum_tv.getText().toString());
        int caloriesDinner = Integer.parseInt(caloriesDinnerNum_tv.getText().toString());
        //Log.d("tag:llxl", "breakfastNum: " + caloriesBreakfast);
        int caloriesIngest = caloriesBreakfast + caloriesLunch + caloriesDinner;
        Log.d("tag:llxl", "ingestNum: " + caloriesIngest);
        caloriesIngest_tv.setText(String.valueOf(caloriesIngest));
        int caloriesLeft = caloriesDailyNeeded - caloriesIngest;
        calories_left.setText(String.valueOf(caloriesLeft));

    }

    private void updateBreakfast() {
        Map<String, Object> nutritionMapBreakfast = mHelper.calculateNutrition("breakfast");
        Integer totalCaloriesBreakfastInteger = (Integer) nutritionMapBreakfast.get("totalCalories");
        Double totalFatBreakfastDouble = (Double) nutritionMapBreakfast.get("totalFat");
        Double totalCarbsBreakfastDouble = (Double) nutritionMapBreakfast.get("totalCarbs");
        Double totalProteinBreakfastDouble = (Double) nutritionMapBreakfast.get("totalProtein");
        if (totalCaloriesBreakfastInteger != null && totalFatBreakfastDouble != null && totalCarbsBreakfastDouble != null && totalProteinBreakfastDouble != null) {
            int totalCaloriesBreakfast = totalCaloriesBreakfastInteger;
            double totalFatBreakfast = totalFatBreakfastDouble;
            double totalCarbsBreakfast = totalCarbsBreakfastDouble;
            double totalProteinBreakfast = totalProteinBreakfastDouble;

            caloriesBreakfastNum_tv = findViewById(R.id.tv_breakfast_calories_num);
            caloriesBreakfastNum_tv.setText(String.valueOf(totalCaloriesBreakfast));

            TextView fatBreakfastNum_tv = findViewById(R.id.tv_breakfast_fat_num);
            fatBreakfastNum_tv.setText(String.valueOf(totalFatBreakfast));

            TextView carbsBreakfastNum_tv = findViewById(R.id.tv_breakfast_carbs_num);
            carbsBreakfastNum_tv.setText(String.valueOf(totalCarbsBreakfast));

            TextView proteinsBreakfastNum_tv = findViewById(R.id.tv_breakfast_protein_num);
            proteinsBreakfastNum_tv.setText(String.valueOf(totalProteinBreakfast));

        } else {
            ToastUtil.show(this, "update page unsuccessfully");
        }
    }

    private void updateLunch() {
        Map<String, Object> nutritionMapLunch = mHelper.calculateNutrition("lunch");
        Integer totalCaloriesLunchInteger = (Integer) nutritionMapLunch.get("totalCalories");
        Double totalFatLunchDouble = (Double) nutritionMapLunch.get("totalFat");
        Double totalCarbsLunchDouble = (Double) nutritionMapLunch.get("totalCarbs");
        Double totalProteinLunchDouble = (Double) nutritionMapLunch.get("totalProtein");
        if (totalCaloriesLunchInteger != null && totalFatLunchDouble != null && totalCarbsLunchDouble != null && totalProteinLunchDouble != null) {
            int totalCaloriesLunch = totalCaloriesLunchInteger;
            double totalFatLunch = totalFatLunchDouble;
            double totalCarbsLunch = totalCarbsLunchDouble;
            double totalProteinLunch = totalProteinLunchDouble;

            caloriesLunchNum_tv = findViewById(R.id.tv_lunch_calories_num);
            caloriesLunchNum_tv.setText(String.valueOf(totalCaloriesLunch));

            TextView fatLunchNum_tv = findViewById(R.id.tv_lunch_fat_num);
            fatLunchNum_tv.setText(String.valueOf(totalFatLunch));

            TextView carbsLunchNum_tv = findViewById(R.id.tv_lunch_carbs_num);
            carbsLunchNum_tv.setText(String.valueOf(totalCarbsLunch));

            TextView proteinsLunchNum_tv = findViewById(R.id.tv_lunch_protein_num);
            proteinsLunchNum_tv.setText(String.valueOf(totalProteinLunch));

        } else {
            ToastUtil.show(this, "update page unsuccessfully");
        }
    }

    private void updateDinner() {
        Map<String, Object> nutritionMapDinner = mHelper.calculateNutrition("dinner");
        Integer totalCaloriesDinnerInteger = (Integer) nutritionMapDinner.get("totalCalories");
        Double totalFatDinnerDouble = (Double) nutritionMapDinner.get("totalFat");
        Double totalCarbsDinnerDouble = (Double) nutritionMapDinner.get("totalCarbs");
        Double totalProteinDinnerDouble = (Double) nutritionMapDinner.get("totalProtein");
        if (totalCaloriesDinnerInteger != null && totalFatDinnerDouble != null && totalCarbsDinnerDouble != null && totalProteinDinnerDouble != null) {
            int totalCaloriesDinner = totalCaloriesDinnerInteger;
            double totalFatDinner = totalFatDinnerDouble;
            double totalCarbsDinner = totalCarbsDinnerDouble;
            double totalProteinDinner = totalProteinDinnerDouble;

            caloriesDinnerNum_tv = findViewById(R.id.tv_dinner_calories_num);
            caloriesDinnerNum_tv.setText(String.valueOf(totalCaloriesDinner));

            TextView fatDinnerNum_tv = findViewById(R.id.tv_dinner_fat_num);
            fatDinnerNum_tv.setText(String.valueOf(totalFatDinner));

            TextView carbsDinnerNum_tv = findViewById(R.id.tv_dinner_carbs_num);
            carbsDinnerNum_tv.setText(String.valueOf(totalCarbsDinner));

            TextView proteinsDinnerNum_tv = findViewById(R.id.tv_dinner_protein_num);
            proteinsDinnerNum_tv.setText(String.valueOf(totalProteinDinner));

        } else {
            ToastUtil.show(this, "update page unsuccessfully");
        }
    }


}