package com.example.application;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.application.database.NutriPalDBHelper;
import com.example.application.entity.User;
import com.example.application.util.SaveImageUtil;
import com.example.application.util.ToastUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText username_et;
    private EditText password_et;
    private CheckBox login_remember_cb;
    private NutriPalDBHelper mHelper;
    private Button register_btn;

    private EditText register_Username_et, register_Password_et, register_ConfirmPassword_et;
    private EditText info_username_et;
    private EditText info_age_tv;
    private TextView info_birth_tv;
    private TextView info_weight_tv;
    private TextView info_height_tv;
    private TextView info_target_tv;
    private ImageView info_photo_iv;
    private Button register_Register_btn, register_Cancel_btn;
    private Button info_continue_btn, info_Cancel_btn;
    private TextView register_hint_tv;
    private ActivityResultLauncher<String> galleryLauncher;
    private Dialog dialogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        username_et = findViewById(R.id.login_username_et);
        password_et = findViewById(R.id.login_password_et);
        login_remember_cb = findViewById(R.id.login_remember_cb);
        register_btn = findViewById(R.id.login_register_btn);
        register_btn.setOnClickListener(this);

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

        dialogInfo = new Dialog(this);
        dialogInfo.setContentView(R.layout.enterinfo_dialog);
        info_photo_iv = dialogInfo.findViewById(R.id.info_photo_iv);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            // 将选择的图片设置到 ImageView 中
                            info_photo_iv.setImageURI(result);
                        }
                    }
                });
        info_photo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动相册选择图片
                openGallery();
            }
        });

        info_continue_btn = dialogInfo.findViewById(R.id.register_save_btn);
        info_continue_btn.setOnClickListener(this);
        info_age_tv = dialogInfo.findViewById(R.id.info_age_tv);
        info_birth_tv = dialogInfo.findViewById(R.id.info_birth_tv);
        info_birth_tv.setOnClickListener(this);
        info_weight_tv = dialogInfo.findViewById(R.id.info_weight_tv);
        info_weight_tv.setOnClickListener(this);
        info_height_tv = dialogInfo.findViewById(R.id.info_height_tv);
        info_height_tv.setOnClickListener(this);
        info_target_tv = dialogInfo.findViewById(R.id.info_target_tv);
        info_target_tv.setOnClickListener(this);
        info_photo_iv = dialogInfo.findViewById(R.id.info_photo_iv);
        info_username_et = dialogInfo.findViewById(R.id.info_username_et);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = NutriPalDBHelper.getInstance(this);
        mHelper.openWriteLink();
        mHelper.openReadLink();
        Log.d("llxl", "received a instance");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.closeLink();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btn) {

            if (checkPassword()) {

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                Toast.makeText(getApplicationContext(), "Successfully Login!", Toast.LENGTH_SHORT).show();

                String username = username_et.getText().toString();
                String password = password_et.getText().toString();

                mHelper.setCurrentUsername(username);
                // 如果勾选了记住密码，将用户名和密码保存到SharedPreferences中
                if (login_remember_cb.isChecked()) {
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Password incorrect!", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.login_register_btn) {
            showRegisterDialog();
        } else if (v.getId() == R.id.info_birth_tv){
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Dialog,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (v.getId() == R.id.info_weight_tv) {
            showWeightPickerDialog(v);
        } else if (v.getId() == R.id.info_height_tv) {
            showHeightPickerDialog();
        } else if (v.getId() == R.id.info_target_tv) {
            showWeightPickerDialog(v);
        } else if (v.getId() == R.id.register_save_btn) {

            String name = (info_username_et.getText().toString()).replaceAll("\\s+", "");
            int age = Integer.parseInt(info_age_tv.getText().toString());
            String photo_name = SaveImageUtil.saveImageToInternalStorage(this,info_photo_iv,mHelper.getCurrentUsername());
            String birth = info_birth_tv.getText().toString();
            int height = Integer.parseInt(info_height_tv.getText().toString());
            int real_weight = Integer.parseInt(info_weight_tv.getText().toString());
            int target_weight = Integer.parseInt(info_target_tv.getText().toString());

            //将用户信息保存至数据库
            User user = new User(name,age,photo_name,birth,height,real_weight,target_weight);
            boolean isExist = mHelper.userExistsInDatabase(user.name);
            if (isExist){

                ToastUtil.show(this, "Username occupied!");
            }else{
                if(mHelper.insert(user)>0){
                    ToastUtil.show(this, "Register successfully");
                    dialogInfo.dismiss();
                }
            }

        }

    }

    public boolean checkPassword() {
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();

        return mHelper.checkPassword(username, password);
    }

    private void showRegisterDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.register_dialog);
        register_Username_et = dialog.findViewById(R.id.register_Username_et);
        register_hint_tv = dialog.findViewById(R.id.register_hint_tv);
        register_Password_et = dialog.findViewById(R.id.register_Password_et);
        register_ConfirmPassword_et = dialog.findViewById(R.id.register_Confirm_et);
        register_Register_btn = dialog.findViewById(R.id.register_Register_btn);
        register_Cancel_btn = dialog.findViewById(R.id.register_Cancel_btn);

        register_Username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本变化之前被调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = register_Username_et.getText().toString();
                if (mHelper.userExistsInDatabase(username)){
                    register_hint_tv.setText(R.string.usernameExist);
                    register_hint_tv.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.icon_red));
                }else{
                    register_hint_tv.setText(R.string.usernameAvailable);
                    register_hint_tv.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.icon_blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 在文本变化之后被调用
            }
        });

        register_Cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        register_Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = register_Username_et.getText().toString();
                String password = register_Password_et.getText().toString();
                String confirmPassword = register_ConfirmPassword_et.getText().toString();
                boolean isExist = mHelper.userExistsInDatabase(username);
                boolean readyToContinue = false;

                // 检查用户名和密码是否有效，并执行注册操作
                if (isValidInput(username, password, confirmPassword) && !isExist) {
                    // 执行注册操作
                    registerUser(username, password);
                    readyToContinue = true;
                } else {
                    // 显示错误消息或者做其他操作
                    Toast.makeText(LoginActivity.this, "Please check username or password", Toast.LENGTH_SHORT).show();

                }
                if(readyToContinue){
                    mHelper.setCurrentUsername(username);
                    dialog.dismiss();
                    showEnterInfoDialog();
                }

            }
        });


        dialog.show();

    }

    private void showEnterInfoDialog() {



        info_username_et.setText(mHelper.getCurrentUsername());

        dialogInfo.show();
    }

    private boolean isValidInput(String username, String password, String confirmPassword) {
        // 在此处进行输入验证，例如检查用户名和密码是否为空，密码是否符合规定，确认密码是否与密码匹配等
        // 返回 true 表示输入有效，否则返回 false
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }

    // 注册用户
    private void registerUser(String username, String password) {
        mHelper.registerUser(username, password);
        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
    }
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String desc = String.format("%s/%s/%s", year, month + 1, dayOfMonth);
        info_birth_tv.setText(desc);
        LocalDate birthDate = LocalDate.of(year, month, dayOfMonth);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        int age = period.getYears();
        String age_string = String.format("%s", age);
        info_age_tv.setText(age_string);
    }

    public void showWeightPickerDialog(View v){
        View clicked = v;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_picker_dialog);

        // 找到NumberPicker和按钮
        NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        Button okButton = dialog.findViewById(R.id.okButton);
        TextView title = dialog.findViewById(R.id.np_title_tv);

        if (v.getId()==R.id.info_target_tv){
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
                if (clicked.getId() == R.id.info_weight_tv){
                    info_weight_tv.setText(desc);
                } else if (clicked.getId() == R.id.info_target_tv) {
                    info_target_tv.setText(desc);
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
                String desc = String.format("%s", selectedValue);
                info_height_tv.setText(desc);
                // 关闭Dialog
                dialog.dismiss();
            }
        });

        // 显示Dialog
        dialog.show();
    }
}