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
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.application.database.NutriPalDBHelper;
import com.example.application.entity.User;
import com.example.application.util.calculateDailyCaloriesUtil;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

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
            PieChartView pieChartView = caloriesCardView.findViewById(R.id.calories_pie_chart);
            instantiateCaloriesPieChart(pieChartView);
            view = caloriesCardView;
            container.addView(view);
        } else {
            macroCardView = layoutInflater.inflate(R.layout.home_cardview_macro, container, false);
            PieChartView macro_fat_pie_chart = macroCardView.findViewById(R.id.macro_fat_pie_chart);
            PieChartView macro_protein_pie_chart = macroCardView.findViewById(R.id.macro_protein_pie_chart);
            PieChartView macro_carbs_pie_chart = macroCardView.findViewById(R.id.macro_carbs_pie_chart);
            instantiateMacroNutritionChart(macro_fat_pie_chart, macro_protein_pie_chart, macro_carbs_pie_chart);
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

    private void instantiateCaloriesPieChart(PieChartView pieChart){

        int foodCalories = mHelper.calculateCalories();
        int targetCalories = calculateDailyCaloriesUtil.calculateCalories(user.target_weight, user.height,user.age);
        int exerciseCalories = 374;
        int restCalories = targetCalories - foodCalories + exerciseCalories;

        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(foodCalories, ContextCompat.getColor(this.context, R.color.icon_blue)).setLabel("Food"));
        pieData.add(new SliceValue(exerciseCalories, ContextCompat.getColor(this.context, R.color.icon_yellow)).setLabel("Exercise"));
        pieData.add(new SliceValue(restCalories, ContextCompat.getColor(this.context, R.color.icon_grey)).setLabel("Rest"));

        // 设置饼图属性
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true); // 显示标签
        pieChartData.setHasLabelsOnlyForSelected(false); // 只在选中时显示标签
        if(foodCalories == 0){
            pieData.get(0).setLabel((String) "");
        }
        pieChartData.setHasLabelsOutside(false); // 标签在饼图外面
        pieChartData.setHasCenterCircle(true); // 显示中心圆
        pieChart.setPieChartData(pieChartData);
    }

    private void instantiateMacroNutritionChart(PieChartView fatChart, PieChartView proteinChart, PieChartView carbsChart){
        int targetCalories = calculateDailyCaloriesUtil.calculateCalories(user.target_weight, user.height,user.age);
        double fatTotalCalories = targetCalories * 0.3;
        double carbsTotalCalories = targetCalories * 0.6;
        double ProteinTotalCalories = targetCalories * 0.1;

        List<Double> macroList = mHelper.calculateMacroNutrition();
        double currentFat = macroList.get(0);
        double currentCarbs = macroList.get(1);
        double currentProtein = macroList.get(2);

        double restFat = fatTotalCalories - currentFat;
        double restCarbs = carbsTotalCalories - currentCarbs;
        double restProtein = ProteinTotalCalories - currentProtein;

        List<SliceValue> fatPieData = new ArrayList<>();
        fatPieData.add(new SliceValue((float) currentFat, ContextCompat.getColor(this.context, R.color.cyan_blue)).setLabel("Fat"));
        fatPieData.add(new SliceValue((float) restFat, ContextCompat.getColor(this.context, R.color.icon_grey)).setLabel("Rest"));

        // 设置饼图属性
        PieChartData fatPieChartData = new PieChartData(fatPieData);
        fatPieChartData.setHasLabels(true); // 显示标签
        fatPieChartData.setHasLabelsOnlyForSelected(true); // 只在选中时显示标签
//        if(currentFat == 0){
//            fatPieData.get(0).setLabel((String) "");
//        }
//        fatPieChartData.setHasLabelsOutside(true); // 标签在饼图外面
        fatPieChartData.setHasCenterCircle(true); // 显示中心圆
        fatPieChartData.setCenterText1(String.format("%.1f", restFat));
        fatPieChartData.setCenterText1Color(ContextCompat.getColor(this.context, R.color.cyan_blue));
        fatPieChartData.setCenterText1FontSize(15);
        fatPieChartData.setCenterText2("Left");
        fatPieChartData.setCenterText2FontSize(12);
        fatChart.setPieChartData(fatPieChartData);


        //protein chart
        List<SliceValue> proteinPieData = new ArrayList<>();
        proteinPieData.add(new SliceValue((float) currentProtein, ContextCompat.getColor(this.context, R.color.purple)).setLabel("Protein"));
        proteinPieData.add(new SliceValue((float) restProtein, ContextCompat.getColor(this.context, R.color.icon_grey)).setLabel("Rest"));

        // 设置饼图属性
        PieChartData proteinPieChartData = new PieChartData(proteinPieData);
        proteinPieChartData.setHasLabels(true); // 显示标签
        proteinPieChartData.setHasLabelsOnlyForSelected(true); // 只在选中时显示标签
//        if(currentProtein == 0){
//            proteinPieData.get(0).setLabel((String) "");
//        }
//        proteinPieChartData.setHasLabelsOutside(false); // 标签在饼图外面
        proteinPieChartData.setHasCenterCircle(true); // 显示中心圆
        proteinPieChartData.setCenterText1(String.format("%.1f", restProtein));
        proteinPieChartData.setCenterText1Color(ContextCompat.getColor(this.context, R.color.purple));
        proteinPieChartData.setCenterText1FontSize(15);
        proteinPieChartData.setCenterText2("Left");
        proteinPieChartData.setCenterText2FontSize(12);
        proteinChart.setPieChartData(proteinPieChartData);


        //carbs chart
        List<SliceValue> carbsPieData = new ArrayList<>();
        carbsPieData.add(new SliceValue((float) currentCarbs, ContextCompat.getColor(this.context, R.color.orange)).setLabel("Carbs"));
        carbsPieData.add(new SliceValue((float) restCarbs, ContextCompat.getColor(this.context, R.color.icon_grey)).setLabel("Rest"));


        // 设置饼图属性
        PieChartData carbsPieChartData = new PieChartData(carbsPieData);
        carbsPieChartData.setHasLabels(true); // 显示标签
        carbsPieChartData.setHasLabelsOnlyForSelected(true); // 只在选中时显示标签
//        if(currentCarbs == 0){
//            carbsPieData.get(0).setLabel((String) "");
//        }
//        carbsPieChartData.setHasLabelsOutside(false); // 标签在饼图外面
        carbsPieChartData.setHasCenterCircle(true); // 显示中心圆
        carbsPieChartData.setCenterText1(String.format("%.1f", restCarbs));
        carbsPieChartData.setCenterText1Color(ContextCompat.getColor(this.context, R.color.orange));
        carbsPieChartData.setCenterText1FontSize(15);
        carbsPieChartData.setCenterText2("Left");
        carbsPieChartData.setCenterText2FontSize(12);
        carbsChart.setPieChartData(carbsPieChartData);


    }

}
