package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private ImageView home_dot_calorie;
    private ImageView home_dot_macro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RadioGroup guide_rg = findViewById(R.id.home_guide_rg);
        guide_rg.setOnCheckedChangeListener(this);
        RadioButton home_home_btn = findViewById(R.id.home_home_btn);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable home_btn_drawable = getDrawable(R.drawable.guide_home_icon_selected);
        home_home_btn.setBackground(home_btn_drawable);

        ImageButton home_logout_btn = findViewById(R.id.home_logout_btn);
        home_logout_btn.setOnClickListener(this);

        ViewPager viewPager = findViewById(R.id.home_vp);
        HomePagerAdapter adapter = new HomePagerAdapter(this);
        viewPager.setAdapter(adapter);
        this.home_dot_calorie = findViewById(R.id.home_dot_calorie);
        this.home_dot_macro = findViewById(R.id.home_dot_macro);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 滑动中的逻辑，可以不用处理
            }

            @Override
            public void onPageSelected(int position) {
                // 当页面切换时被调用
                if (position == 0) {
                    // 如果ViewPager滑动到第一个页面，更换ImageButton的图片源为新的图片
                    home_dot_calorie.setImageResource(R.drawable.dot_shape_blue);
                    home_dot_macro.setImageResource(R.drawable.dot_shape_grey);
                } else if (position == 1) {
                    // 如果ViewPager滑动到第二个页面，更换ImageButton的图片源为另一个新的图片
                    home_dot_macro.setImageResource(R.drawable.dot_shape_blue);
                    home_dot_calorie.setImageResource(R.drawable.dot_shape_grey);
                }
                // 可以根据需要继续处理更多页面切换逻辑
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 页面滚动状态变化时的逻辑，可以不用处理
            }
        });

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.home_diary_btn){
            startActivity(new Intent(HomeActivity.this, MealsActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.home_logout_btn){
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

            Toast.makeText(getApplicationContext(), "Successfully Logout!", Toast.LENGTH_SHORT).show();
        }
    }
}