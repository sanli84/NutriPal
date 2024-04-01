package com.example.application;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.health.connect.datatypes.HeightRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;



public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, RadioGroup.OnCheckedChangeListener {

    private DatePicker dp_date;
    private TextView profile_birth_tv;
    private TextView profile_weight_tv;
    private TextView profile_height_tv;
    private TextView profile_target_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_birth_tv = findViewById(R.id.profile_birth_tv);
        profile_birth_tv.setOnClickListener(this);
        profile_weight_tv = findViewById(R.id.profile_weight_tv);
        profile_weight_tv.setOnClickListener(this);
        profile_height_tv = findViewById(R.id.profile_height_tv);
        profile_height_tv.setOnClickListener(this);
        profile_target_tv = findViewById(R.id.profile_target_tv);
        profile_target_tv.setOnClickListener(this);

        RadioGroup guide_rg = findViewById(R.id.profile_guide_rg);
        guide_rg.setOnCheckedChangeListener(this);
        RadioButton profile_profile_btn = findViewById(R.id.profile_profile_btn);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable home_btn_drawable = getDrawable(R.drawable.guide_profile_selected);
        profile_profile_btn.setBackground(home_btn_drawable);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_birth_tv){
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Dialog,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (v.getId() == R.id.profile_weight_tv) {
            showWeightPickerDialog(v);
        } else if (v.getId() == R.id.profile_height_tv) {
            showHeightPickerDialog();
        } else if (v.getId() == R.id.profile_target_tv) {
            showWeightPickerDialog(v);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String desc = String.format("%s/%s/%s", year, month + 1, dayOfMonth);
        profile_birth_tv.setText(desc);
        LocalDate birthDate = LocalDate.of(year, month, dayOfMonth);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        int age = period.getYears();
        TextView profile_age_tv = findViewById(R.id.profile_age_tv);
        String age_string = String.format("%s", age);
        profile_age_tv.setText(age_string);
    }

    public void showWeightPickerDialog(View v){
        View clicked = v;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_picker_dialog);

        // 找到NumberPicker和按钮
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        Button okButton = dialog.findViewById(R.id.okButton);

        // 设置NumberPicker的范围和默认值
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(50);
        numberPicker.setTextSize(50);

        // 设置按钮的点击监听器
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理按钮点击事件
                // 可以获取NumberPicker的值并进行相应操作
                int selectedValue = numberPicker.getValue();
                String desc = String.format("%s KG", selectedValue);
                if (clicked.getId() == R.id.profile_weight_tv){
                    profile_weight_tv.setText(desc);
                } else if (clicked.getId() == R.id.profile_target_tv) {
                    profile_target_tv.setText(desc);
                }
                // 关闭Dialog
                dialog.dismiss();
            }
        });

        // 显示Dialog
        dialog.show();
    }

    public void showHeightPickerDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_picker_dialog);

        // 找到NumberPicker和按钮
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        TextView title_tv = dialog.findViewById(R.id.np_title_tv);
        TextView unit_textview = dialog.findViewById(R.id.unit_textview);
        Button okButton = dialog.findViewById(R.id.okButton);

        // 设置NumberPicker的范围和默认值
        title_tv.setText(getString(R.string.profile_enterHeight));
        unit_textview.setText(getString(R.string.cm));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(230);
        numberPicker.setValue(175);
        numberPicker.setTextSize(50);

        // 设置按钮的点击监听器
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理按钮点击事件
                // 可以获取NumberPicker的值并进行相应操作
                int selectedValue = numberPicker.getValue();
                String desc = String.format("%s cm", selectedValue);
                profile_height_tv.setText(desc);
                // 关闭Dialog
                dialog.dismiss();
            }
        });

        // 显示Dialog
        dialog.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.profile_home_btn){
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        } else if (checkedId == R.id.profile_diary_btn) {
            startActivity(new Intent(ProfileActivity.this, MealsActivity.class));
        }
    }
}