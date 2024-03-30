package com.example.application;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class HomePagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;



    public HomePagerAdapter(Context context) {

        this.context = context;


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
            view = layoutInflater.inflate(R.layout.home_cardview_calorie, null);
//            home_dot_calorie.setImageResource(R.drawable.dot_shape_blue);
//            home_dot_macro.setImageResource(R.drawable.dot_shape_grey);
        } else {
            view = layoutInflater.inflate(R.layout.home_cardview_macro, null);
//            home_dot_macro.setImageResource(R.drawable.dot_shape_blue);
//            home_dot_calorie.setImageResource(R.drawable.dot_shape_grey);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
