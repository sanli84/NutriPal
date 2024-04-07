package com.example.application.entity;

import java.time.LocalDate;

public class Food {

    public String user_name;
    public String food_name;
    public String category;
    public String unit;
    public int calories;
    public double fat;
    public double carbs;
    public double protein;
    public String date;
    public double quantity;

    public Food(){

    }

    public Food(String user_name, String food_name, String category, String unit,int caloriesNum,double fatNum,double carbsNum,double proteinNum,String date, double quantity) {
        this.user_name = user_name;
        this.food_name = food_name;
        this.category = category;
        this.unit = unit;
        this.calories = caloriesNum;
        this.fat = fatNum;
        this.carbs = carbsNum;
        this.protein = proteinNum;
        this.date = date;
        this.quantity = quantity;
    }
}
