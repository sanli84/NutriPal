package com.example.application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText username_et;
    private EditText password_et;
    private CheckBox login_remember_cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        username_et = findViewById(R.id.login_username_et);
        password_et = findViewById(R.id.login_password_et);
        login_remember_cb = findViewById(R.id.login_remember_cb);

        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // When the Activity is created, get the status of remembering the password from SharedPreferences
        boolean rememberPassword = sharedPreferences.getBoolean("rememberPassword", false);
        login_remember_cb.setChecked(rememberPassword);

        if (rememberPassword) {
            // 如果记住密码，从SharedPreferences中获取用户名和密码，并设置到EditText中
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            username_et.setText(username);
            password_et.setText(password);
        }

        login_remember_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 当复选框状态改变时，将状态保存到SharedPreferences中
                editor.putBoolean("rememberPassword", isChecked);
                editor.apply();
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btn){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            Toast.makeText(getApplicationContext(), "Successfully Login!", Toast.LENGTH_SHORT).show();

            String username = username_et.getText().toString();
            String password = password_et.getText().toString();

            // 如果勾选了记住密码，将用户名和密码保存到SharedPreferences中
            if (login_remember_cb.isChecked()) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();
            }

        }

    }
}