package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAccessToken();
        callApiWithToken(accessToken);
    }


    @SuppressLint("StaticFieldLeak")
    private void requestAccessToken() {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("tag:llxl", "Failed to receive access token");
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void callApiWithToken(String accessToken) {
        // 异步任务：调用 API
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... tokens) {
                OkHttpClient client = new OkHttpClient();

                // 构建请求体
                JSONObject requestBodyJson = new JSONObject();
                try {
                    requestBodyJson.put("format", "xml");
                    requestBodyJson.put("method", "food.get.v2");
                    requestBodyJson.put("food_id", "3092");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String requestContent = "method=foods.search&search_expression=toast&format=json";
                // 创建 MediaType 对象，指定为 application/json
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

// 创建 RequestBody 对象，指定请求体的内容和媒体类型
                RequestBody requestBody = RequestBody.create(JSON, requestContent);
                // 构建请求
                Request request = new Request.Builder()
                        .url("https://platform.fatsecret.com/rest/server.api")
                        .post(requestBody)
                        .header("Authorization", "Bearer " + tokens[0])// 设置访问令牌
                        .build();

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
                } else {
                    Log.e("tag:llxl", "Failed to call API");
                }
            }
        }.execute(accessToken);
    }

}
