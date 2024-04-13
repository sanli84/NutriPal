package com.example.application;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.application.database.NutriPalDBHelper;
import com.example.application.entity.User;
import com.example.application.util.calculateDailyCaloriesUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private ImageView home_dot_calorie;
    private ImageView home_dot_macro;
    private NutriPalDBHelper mHelper;
    private HomePagerAdapter adapter;
    private TextView home_calories_target_tv;
    private TextView home_food_tv;
    private View currentView;
    private ViewPager viewPager;
    private View calorieView;
    private View macroView;
    private View caloriesCardView;
    private Dialog addWeightDialog;
    private LineChartView line_chart;
    private Button home_addWeight_btn;
    private Button addWeight_continue_btn;
    private Button addWeight_cancel_btn;

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
        line_chart = (LineChartView) findViewById(R.id.line_chart);

        this.home_dot_calorie = findViewById(R.id.home_dot_calorie);
        this.home_dot_macro = findViewById(R.id.home_dot_macro);
        addWeightDialog = new Dialog(this);
        addWeightDialog.setContentView(R.layout.addweight_dialog);
        home_addWeight_btn = findViewById(R.id.home_addWeight_btn);
        home_addWeight_btn.setOnClickListener(this);
        addWeight_continue_btn = addWeightDialog.findViewById(R.id.addWeight_continue_btn);
        addWeight_continue_btn.setOnClickListener(this);
        addWeight_cancel_btn = addWeightDialog.findViewById(R.id.addWeight_cancel_btn);
        addWeight_cancel_btn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {

        super.onStart();
        mHelper = NutriPalDBHelper.getInstance(this);
        mHelper.openWriteLink();
        mHelper.openReadLink();
        String currentUsername = getIntent().getStringExtra("currentUsername");
        mHelper.setCurrentUsername(currentUsername);
        instantiateLineChart(line_chart);
        viewPager = findViewById(R.id.home_vp);
        User currentUser = mHelper.getUser(mHelper.getCurrentUsername());
        adapter = new HomePagerAdapter(this, currentUser, mHelper);
        viewPager.setAdapter(adapter);

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
        if (checkedId == R.id.home_diary_btn) {
            Intent intent = new Intent(HomeActivity.this, MealsActivity.class);
            String currentUsername = mHelper.getCurrentUsername();
            Log.d("tag:llxl", "from profile to home: " + currentUsername);
            intent.putExtra("currentUsername", currentUsername);
            startActivity(intent);
        } else if (checkedId == R.id.home_profile_btn) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            String currentUsername = mHelper.getCurrentUsername();
            intent.putExtra("currentUsername", currentUsername);
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.home_logout_btn) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

            Toast.makeText(getApplicationContext(), "Successfully Logout!", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.home_addWeight_btn){
            addWeightDialog.show();
        } else if (v.getId() == R.id.addWeight_continue_btn) {
            EditText addWeight_et = addWeightDialog.findViewById(R.id.addWeight_et);
            int currentWeight = Integer.parseInt(addWeight_et.getText().toString());
            if (mHelper.getWeight(0) != 0){
                mHelper.updateWeight(currentWeight);
            }else mHelper.insertWeight(mHelper.getCurrentUsername(), currentWeight);

            instantiateLineChart(line_chart);
            addWeightDialog.dismiss();
        } else if (v.getId() == R.id.addWeight_cancel_btn) {
            addWeightDialog.dismiss();
        }
    }

    private void instantiateLineChart(LineChartView lineChart) {
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        List<Float> weightData = new ArrayList<>();
        List<String> dateData = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            //days to today
            float weight = mHelper.getWeight(4-i);
            weightData.add(weight);

        }
        float lastWeight = 0;
        for (int i = 0; i < 5; i++) {

            if(weightData.get(i) != 0){
                lastWeight = weightData.get(i);
                break;
            }
        }

        for (int i = 0; i < 5; i++) {
            //days to today
            if (weightData.get(i) == 0){
                weightData.set(i,lastWeight);
            }


        }

        for (int i = 4; i >= 0; i--) { // 循环生成当前日期的前后两天的日期
            // 调整日期
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();

            // 获取前三天的日期
            LocalDate daysBefore = currentDate.minusDays(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

            // 格式化日期
            String DateBeforeString = daysBefore.format(formatter);

            // 添加到日期数据列表中
            dateData.add(DateBeforeString);
        }

// 准备日期和体重数据
        for (int i = 0; i < dateData.size(); i++) {
            axisValues.add(new AxisValue(i).setLabel(dateData.get(i))); // 添加日期数据
        }

        for (int i = 0; i < weightData.size(); i++) {
            values.add(new PointValue(i, weightData.get(i))); // 添加体重数据
        }

        Line line = new Line(values).setColor(ContextCompat.getColor(this, R.color.cyan_blue)).setCubic(true); // 创建线条对象
        line.setCubic(false);//设置为直线
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis xAxis = new Axis();
        xAxis.setValues(axisValues);
        xAxis.setName("Date");
        data.setAxisXBottom(xAxis);

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(8);//设置字体大小
        axisY.setHasLines(true);
        axisY.setMaxLabelChars(6);//max label length, for example 60
        // 这样添加y轴坐标 就可以固定y轴的数据
        List<AxisValue> weightYValues = new ArrayList<>();
        for (int i = 0; i < weightData.size(); i++) {
            AxisValue value = new AxisValue(weightData.get(i));
            weightYValues.add(value);
        }

        axisY.setValues(weightYValues);
        data.setAxisYLeft(axisY);  //Y轴设置在左边

        // 绘制目标体重水平线
        float targetWeight = 65; // 假设目标体重为70公斤
        Line targetLine = new Line();
        targetLine.setColor(ContextCompat.getColor(this, R.color.icon_red)); // 设置线的颜色为红色
        // 设置目标线的值
        targetLine.setValues(Arrays.asList(
                new PointValue(0, targetWeight), // 第一个点，坐标为 (0, targetWeight)
                new PointValue(dateData.size() - 1, targetWeight) // 第二个点，坐标为 (dataSize - 1, targetWeight)，使线延伸到最右端
        ));
        targetLine.setPointRadius(0);
        AxisValue targetValue = new AxisValue(targetWeight);
        weightYValues.add(targetValue);
        axisY.setValues(weightYValues);
        // 将线添加到折线图中
        lines.add(targetLine);
        data.setLines(lines);

        lineChart.setLineChartData(data);
    }


}