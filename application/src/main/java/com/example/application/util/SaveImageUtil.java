package com.example.application.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.application.LoginActivity;
import com.example.application.ProfileActivity;
import com.example.application.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveImageUtil {

    public static String saveImageToInternalStorage(Context ctx, ImageView iv, String current_username) {


        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();

        // 获取内部存储路径
        File directory = new File(ctx.getFilesDir(), "photos");

        // 如果文件夹不存在，则创建
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 为图片生成一个用户名命名的文件名
        //暂时使用用户名，后续使用用户id
        String fileName = "photo_" + current_username + ".jpg";
        File file = new File(directory, fileName);

        // 将图片保存到文件中
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            Toast.makeText(ProfileActivity.this, "Image saved to internal storage", Toast.LENGTH_SHORT).show();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Failed to save image", Toast.LENGTH_SHORT).show();
            //若没找到，返回空，后续若没找到可使用默认图片
            return null;
        }
    }
}
