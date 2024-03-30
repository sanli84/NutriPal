package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MealsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        RadioGroup guide_rg = findViewById(R.id.meals_guide_rg);
        guide_rg.setOnCheckedChangeListener(this);
        RadioButton meals_diary_btn = findViewById(R.id.meals_diary_btn);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable meals_btn_drawable = getDrawable(R.drawable.guide_diary_selected);
        meals_diary_btn.setBackground(meals_btn_drawable);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.meals_home_btn){
            startActivity(new Intent(MealsActivity.this, HomeActivity.class));
        }
    }
}