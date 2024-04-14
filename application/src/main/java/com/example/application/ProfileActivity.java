package com.example.application;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import com.example.application.database.NutriPalDBHelper;
import com.example.application.entity.User;
import com.example.application.util.SaveImageUtil;
import com.example.application.util.ToastUtil;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, RadioGroup.OnCheckedChangeListener {

    private DatePicker dp_date;
    private  TextView profile_username_et;
    private EditText profile_age_tv;
    private TextView profile_birth_tv;
    private TextView profile_weight_tv;
    private TextView profile_height_tv;
    private TextView profile_target_tv;
    private ActivityResultLauncher<String> galleryLauncher;
    private ImageView profile_photo_iv;
    private NutriPalDBHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_username_et = findViewById(R.id.profile_username_et);
        profile_birth_tv = findViewById(R.id.profile_birth_tv);
        profile_birth_tv.setOnClickListener(this);
        profile_weight_tv = findViewById(R.id.profile_weight_tv);
        profile_weight_tv.setOnClickListener(this);
        profile_height_tv = findViewById(R.id.profile_height_tv);
        profile_height_tv.setOnClickListener(this);
        profile_target_tv = findViewById(R.id.profile_target_tv);
        profile_target_tv.setOnClickListener(this);
        profile_age_tv = findViewById(R.id.profile_age_tv);
        Button profile_save_btn = findViewById(R.id.profile_save_btn);
        profile_save_btn.setOnClickListener(this);

        RadioGroup guide_rg = findViewById(R.id.profile_guide_rg);
        guide_rg.setOnCheckedChangeListener(this);
        RadioButton profile_profile_btn = findViewById(R.id.profile_profile_btn);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable home_btn_drawable = getDrawable(R.drawable.guide_profile_selected);
        profile_profile_btn.setBackground(home_btn_drawable);

        profile_photo_iv = findViewById(R.id.profile_photo_iv);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            // 将选择的图片设置到 ImageView 中
                            profile_photo_iv.setImageURI(result);
                        }
                    }
                });

        profile_photo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动相册选择图片
                openGallery();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = NutriPalDBHelper.getInstance(this);
        mHelper.openWriteLink();
        mHelper.openReadLink();
        String currentUsername = getIntent().getStringExtra("currentUsername");
        mHelper.setCurrentUsername(currentUsername);

        profile_username_et.setText(mHelper.getCurrentUsername());
        User currentUser = mHelper.getUser(mHelper.getCurrentUsername());
        profile_username_et.setText(currentUser.name);
        profile_age_tv.setText(String.valueOf(currentUser.age));

        profile_birth_tv.setText(currentUser.birth);
        profile_weight_tv.setText(String.valueOf(currentUser.real_weight));
        profile_height_tv.setText(String.valueOf(currentUser.height));
        profile_target_tv.setText(String.valueOf(currentUser.target_weight));
        String photoFileName = String.format("photo_%s.jpg", currentUser.name); // 替换为你的照片文件名

        String filePath = getFilesDir().getAbsolutePath() + "/photos/";
        String photoFilePath = filePath + photoFileName;

        Uri photoUri = Uri.parse("file://" + photoFilePath);
        profile_photo_iv.setImageURI(photoUri);

    }


    @Override
    protected void onStop() {
        super.onStop();
//        mHelper.closeLink();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_birth_tv) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Dialog,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (v.getId() == R.id.profile_weight_tv) {
            showWeightPickerDialog(v);
        } else if (v.getId() == R.id.profile_height_tv) {
            showHeightPickerDialog();
        } else if (v.getId() == R.id.profile_target_tv) {
            showWeightPickerDialog(v);
        } else if (v.getId() == R.id.profile_save_btn) {

            String name = (profile_username_et.getText().toString()).replaceAll("\\s+", "");
            int age = Integer.parseInt(profile_age_tv.getText().toString());
            String photo_name = SaveImageUtil.saveImageToInternalStorage(this, profile_photo_iv, name);
            String birth = profile_birth_tv.getText().toString();
            int height = Integer.parseInt(profile_height_tv.getText().toString());
            int real_weight = Integer.parseInt(profile_weight_tv.getText().toString());
            int target_weight = Integer.parseInt(profile_target_tv.getText().toString());

            //将用户信息保存至数据库
            User user = new User(name, age, photo_name, birth, height, real_weight, target_weight);
            boolean isExist = mHelper.userExistsInDatabase(user.name);

            Log.d("tag:llxl","button_save clicked");
            if ( mHelper.updatePWTable(user, mHelper.getCurrentUsername()) > 0 && mHelper.update(user) > 0) {
                Log.d("llxl",String.format("user's name: %s",user.name));
                mHelper.setCurrentUsername(user.name);
                Log.d("llxl",String.format("current_username: %s",mHelper.getCurrentUsername()));

                ToastUtil.show(this, "Update successfully");

            }
            if (mHelper.getWeight(0) != 0){
                mHelper.updateWeight(real_weight);
            }else mHelper.insertWeight(user.name, real_weight);




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
        profile_age_tv = findViewById(R.id.profile_age_tv);
        String age_string = String.format("%s", age);
        profile_age_tv.setText(age_string);
    }

    public void showWeightPickerDialog(View v) {
        View clicked = v;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_picker_dialog);

        // 找到NumberPicker和按钮
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        Button okButton = dialog.findViewById(R.id.okButton);
        TextView title = dialog.findViewById(R.id.np_title_tv);

        if (v.getId() == R.id.profile_target_tv) {
            title.setText(R.string.profile_enterTarget);
        }

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
                String desc = String.format("%s", selectedValue);
                if (clicked.getId() == R.id.profile_weight_tv) {
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

    public void showHeightPickerDialog() {
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
                String desc = String.format("%s", selectedValue);
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
        if (checkedId == R.id.profile_home_btn) {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            String currentUsername = mHelper.getCurrentUsername();
            Log.d("tag:llxl", "from profile to home: "+ currentUsername);
            intent.putExtra("currentUsername", currentUsername);
            startActivity(intent);
        } else if (checkedId == R.id.profile_diary_btn) {
            Intent intent = new Intent(ProfileActivity.this, MealsActivity.class);
            String currentUsername = mHelper.getCurrentUsername();
            intent.putExtra("currentUsername", currentUsername);
            startActivity(intent);
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

//    private String saveImageToInternalStorage() {
//        Bitmap bitmap = ((BitmapDrawable) profile_photo_iv.getDrawable()).getBitmap();
//
//        // 获取内部存储路径
//        File directory = new File(getFilesDir(), "photos");
//
//        // 如果文件夹不存在，则创建
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//
//        // 为图片生成一个用户名命名的文件名
//        //暂时使用用户名，后续使用用户id
//        String fileName = "photo_" + profile_username_et.getText() + ".jpg";
//        File file = new File(directory, fileName);
//
//        // 将图片保存到文件中
//        try (FileOutputStream outputStream = new FileOutputStream(file)) {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
////            Toast.makeText(ProfileActivity.this, "Image saved to internal storage", Toast.LENGTH_SHORT).show();
//            return fileName;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(ProfileActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
//            //若没找到，返回空，后续若没找到可使用默认图片
//            return null;
//        }
//    }

}