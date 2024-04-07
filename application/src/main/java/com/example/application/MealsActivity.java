package com.example.application;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import com.example.application.util.ParseFoodString;
import com.example.application.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        if(checkedId == R.id.meals_home_btn){
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
                RequestBody requestBody = new FormBody.Builder()
                        .add("grant_type", "client_credentials")
                        .add("client_id", clientID)
                        .add("client_secret", clientSecret)
                        .add("scope", "basic")
                        .build();

                // 构建请求
                Request request = new Request.Builder()
                        .url("https://oauth.fatsecret.com/connect/token")
                        .post(requestBody)
                        .build();

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


                RequestBody requestBody = new FormBody.Builder()
                        .add("method", "foods.search")
                        .add("format", "json")
                        .add("max_results", "10")
                        .add("search_expression", foodName)
                        .build();

                // 构建请求
                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + accessToken)// 设置访问令牌
                        .url("https://platform.fatsecret.com/rest/server.api")
                        .post(requestBody)
                        .build();
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

    public void addFood(View view) {
        dialogAddFood.show();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addFood_search_btn){
            EditText addFood_foodName_et = dialogAddFood.findViewById(R.id.addFood_foodName_et);
            String foodName = addFood_foodName_et.getText().toString();
            requestAccessToken(foodName);
        } else if(v.getId() == R.id.addFood_Add_btn){
            String selectedFood = addFood_spinner.getSelectedItem().toString();
            Log.d("tag:llxl", "selected: " + selectedFood);
            double foodQuantity = Double.parseDouble(addFood_quantity_et.getText().toString());
            String category = "breakfast";
            if(mHelper.storeFood(selectedFood, foodQuantity, category) > 0){
                ToastUtil.show(this, "Add food successfully!");
            }else ToastUtil.show(this, "Add food unsuccessfully!");
            dialogAddFood.dismiss();
            updatePage();
        }
    }

    private void updatePage(){
        List<List<Food>> foodList = mHelper.getFoodsToday();
        List<Food> breakfastList = foodList.get(0);
        int breakfastNum = breakfastList.size();
        for (int i = 0; i < breakfastNum;i++){

            if(i == 0){
                Food tempFood = breakfastList.get(0);
                @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s,", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
                TextView tempTV = findViewById(R.id.breakfast_food_1);
                tempTV.setText(tempFoodString);
            } else if (i == 1) {
                Food tempFood = breakfastList.get(1);
                @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s,", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
                TextView tempTV = findViewById(R.id.breakfast_food_2);
                tempTV.setText(tempFoodString);
            } else if (i == 2) {
                Food tempFood = breakfastList.get(2);
                @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s,", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
                TextView tempTV = findViewById(R.id.breakfast_food_3);
                tempTV.setText(tempFoodString);
            }else if (i == 3) {
                Food tempFood = breakfastList.get(3);
                @SuppressLint("DefaultLocale") String tempFoodString = String.format("%s %sX%s,", tempFood.food_name, String.format("%.0f", tempFood.quantity), tempFood.unit);
                TextView tempTV = findViewById(R.id.breakfast_food_4);
                tempTV.setText(tempFoodString);
            }
        }


        List<Food> lunchList = foodList.get(1);
        List<Food> dinnerList = foodList.get(2);
    }
}