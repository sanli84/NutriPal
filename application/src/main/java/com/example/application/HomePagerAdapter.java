package com.example.application;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.application.database.NutriPalDBHelper;
import com.example.application.entity.User;
import com.example.application.util.calculateDailyCaloriesUtil;
import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    public View caloriesCardView;
    public View macroCardView;
    private List<View> views;
    private User user;
    private NutriPalDBHelper mHelper;


    public HomePagerAdapter(Context context, User user, NutriPalDBHelper mHelper) {

        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user = user;
        this.mHelper = mHelper;
        this.views = new ArrayList<>();
        this.caloriesCardView = layoutInflater.inflate(R.layout.home_cardview_calorie, null);
        this.macroCardView = layoutInflater.inflate(R.layout.home_cardview_macro, null);
        views.add(this.caloriesCardView);
        views.add(this.macroCardView);
        Log.d("tag:llxl", "构造器里就赋值caloriesCardView: " + caloriesCardView);

    }

    public void addView(View view) {
        views.add(view);
    }

    public View getCurrentView(int position) {

        return views.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        if (position == 0) {

            caloriesCardView = layoutInflater.inflate(R.layout.home_cardview_calorie, container, false);
            TextView home_calories_target_tv= caloriesCardView.findViewById(R.id.home_calories_target_tv);
            int targetCalories = calculateDailyCaloriesUtil.calculateCalories(user.target_weight, user.height,user.age);
            home_calories_target_tv.setText(String.format("\tTarget\t\t\t%s",targetCalories));
            int foodCalories = mHelper.calculateCalories();
            TextView home_food_tv= caloriesCardView.findViewById(R.id.home_food_tv);
            home_food_tv.setText(String.format("\tFood\t\t\t\t%s",foodCalories));
            TextView home_calories_exercise_tv = caloriesCardView.findViewById(R.id.home_calories_exercise_tv);
            home_calories_exercise_tv.setText(R.string.example_exercise_calories);
            view = caloriesCardView;
            container.addView(view);
        } else {
            macroCardView = layoutInflater.inflate(R.layout.home_cardview_macro, container, false);
            view = macroCardView;
            container.addView(view);

        }
        views.add(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
    }

}
