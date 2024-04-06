package com.example.application.entity;

public class Food {

    private String food_name;
    private String unit;
    private int calories;
    private double fat;
    private double carbs;
    private double protein;

    public Food(String food_name, String unit,int caloriesNum,double fatNum,double carbsNum,double proteinNum) {
        this.food_name = food_name;
        this.unit = unit;
        this.calories = caloriesNum;
        this.fat = fatNum;
        this.carbs = carbsNum;
        this.protein = proteinNum;
    }
}
