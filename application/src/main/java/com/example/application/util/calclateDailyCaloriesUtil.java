package com.example.application.util;

public class calclateDailyCaloriesUtil {
    public static int calculateCalories(int weight, int height, int age){
        int BMR = (int)(10 * weight + 6.25 * height - 5 * age);

        return BMR;
    }
}
