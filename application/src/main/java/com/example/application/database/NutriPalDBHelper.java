package com.example.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.application.entity.Food;
import com.example.application.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NutriPalDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nutripal.db";
    private static final int DB_VERSION = 1;
    private static NutriPalDBHelper mHelper = null;
    private static final String TABLE_NAME_USER_INFO = "user_info";
    private static final String TABLE_NAME_USER_PASSWORD = "user_password";
    private static final String TABLE_NAME_MEALS = "meals";
    private static final String TABLE_NAME_WEIGHT = "weight";

    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private String current_username = null;

    private NutriPalDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static NutriPalDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new NutriPalDBHelper(context);
        }
        Log.d("llxl", "returned a instance");
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();

        }
        return mRDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mRDB;
    }

    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }

        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("llxl", "it is creating a new table");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_INFO + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " profile_photo VARCHAR NOT NULL," +
                " birth VARCHAR NOT NULL," +
                " height INTEGER NOT NULL," +
                " real_weight INTEGER NOT NULL," +
                " age INTEGER NOT NULL," +
                " target_weight INTEGER NOT NULL);";

        String sql_pw = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_PASSWORD + " (" +
                " user_name VARCHAR PRIMARY KEY NOT NULL," +
                " password VARCHAR NOT NULL," +
                " email VARCHAR NOT NULL);";

        String sql_meals = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MEALS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " user_name VARCHAR NOT NULL," +
                " food_name VARCHAR NOT NULL,"+
                " category VARCHAR NOT NULL,"+
                " unit VARCHAR NOT NULL," +
                " calories INTEGER NOT NULL," +
                " fat DOUBLE NOT NULL," +
                " carbs DOUBLE NOT NULL," +
                " protein DOUBLE NOT NULL," +
                " quantity DOUBLE NOT NULL," +
                " date VARCHAR NOT NULL);";

        String sql_weight = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_WEIGHT + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " user_name VARCHAR NOT NULL," +
                " date VARCHAR NOT NULL," +
                " weight FLOAT NOT NULL);";


        db.execSQL(sql);
        db.execSQL(sql_pw);
        db.execSQL(sql_meals);
        db.execSQL(sql_weight);
    }

    public boolean userExistsInDatabase(String userName) {

        List<User> list = new ArrayList<>();
        try (Cursor cursor = mRDB.query(TABLE_NAME_USER_INFO, null, "name=?", new String[]{userName}, null, null, null)) {
            // 使用 Cursor 获取数据的逻辑

            while (cursor.moveToNext()) {
                User user = new User();
                user.id = cursor.getInt(0);
                list.add(user);
            }
        }

        return !list.isEmpty();

    }

    public boolean checkPassword(String username, String password) {

        List<User> list = new ArrayList<>();
        try (Cursor cursor = mRDB.query(TABLE_NAME_USER_PASSWORD, null, "user_name=? AND password=?", new String[]{username, password}, null, null, null)) {
            // 使用 Cursor 获取数据的逻辑

            while (cursor.moveToNext()) {
                User user = new User();
                user.id = cursor.getInt(0);
                list.add(user);
            }
        }

        return !list.isEmpty();
    }

    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.name);
        values.put("age", user.age);
        values.put("profile_photo", user.photo_name);
        values.put("birth", user.birth);
        values.put("height", user.height);
        values.put("real_weight", user.real_weight);
        values.put("target_weight", user.target_weight);

        return mWDB.insert(TABLE_NAME_USER_INFO, null, values);
    }

    public long update(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.name);
        values.put("age", user.age);
        values.put("profile_photo", user.photo_name);
        values.put("birth", user.birth);
        values.put("height", user.height);
        values.put("real_weight", user.real_weight);
        values.put("target_weight", user.target_weight);
        return mWDB.update(TABLE_NAME_USER_INFO, values, "name=?", new String[]{mHelper.getCurrentUsername()});
    }

    public long updatePWTable(User user, String current_username) {
        String password = getPassword(current_username);
        ContentValues values = new ContentValues();
        values.put("user_name", user.name);
        values.put("password", password);
        Log.d("llxl", "values have been put");
        return mWDB.update(TABLE_NAME_USER_PASSWORD, values, "user_name=?", new String[]{current_username});
    }

    public long updatePassword(String password) {

        ContentValues values = new ContentValues();
        values.put("user_name", current_username);
        values.put("password", password);
        values.put("email", mHelper.getUserEmail(current_username));
        return mWDB.update(TABLE_NAME_USER_PASSWORD, values, "user_name=?", new String[]{current_username});
    }

    public String getPassword(String current_username) {

        Cursor cursor = mRDB.query(TABLE_NAME_USER_PASSWORD, null, "user_name=?", new String[]{current_username}, null, null, null);
        while (cursor.moveToNext()) {

            String password = cursor.getString(1);
            return password;
        }
        return null;
    }

    public long registerUser(String username, String password, String email) {
        ContentValues values = new ContentValues();
        values.put("user_name", username);
        values.put("password", password);
        values.put("email", email);
        return mWDB.insert(TABLE_NAME_USER_PASSWORD, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("llxl", "it is creating a new table");

    }

    public void setCurrentUsername(String username) {
        mHelper.current_username = username;
    }

    public String getCurrentUsername() {

//        Log.d("llxl",String.format("current_username: %s",mHelper.getCurrentUsername()));
        return mHelper.current_username;
    }

    public User getUser(String username) {
        User user = new User();

        Cursor cursor = mRDB.query(TABLE_NAME_USER_INFO, null, "name=?", new String[]{username}, null, null, null);
        while (cursor.moveToNext()) {

            user.id = cursor.getInt(0);
            user.name = cursor.getString(1);
            user.photo_name = cursor.getString(2);
            user.birth = cursor.getString(3);
            user.height = cursor.getInt(4);
            user.real_weight = cursor.getInt(5);
            user.age = cursor.getInt(6);
            user.target_weight = cursor.getInt(7);
        }
        return user;
    }

    public String getUserEmail(String username) {
        User user = new User();

        Cursor cursor = mRDB.query(TABLE_NAME_USER_PASSWORD, null, "user_name=?", new String[]{username}, null, null, null);
        while (cursor.moveToNext()) {

            return cursor.getString(2);
        }
        return null;
    }

    public float getWeight(int days) {
        String username = mHelper.getCurrentUsername();
        LocalDate daysBefore = LocalDate.now().minusDays(days);

        Cursor cursor = mRDB.query(TABLE_NAME_WEIGHT, null, "user_name=? AND date=?", new String[]{username, daysBefore.toString()}, null, null, null);
        while (cursor.moveToNext()) {

            return cursor.getFloat(3);
        }
        return 0;
    }

    public void insertWeight(String username, int weight){
        ContentValues values = new ContentValues();
        values.put("user_name", username);
        values.put("weight", weight);
        values.put("date", LocalDate.now().toString());
        mWDB.insert(TABLE_NAME_WEIGHT, null, values);
    }

    public void updateWeight(int weight){
        ContentValues values = new ContentValues();
        values.put("user_name", current_username);
        values.put("weight", weight);
        values.put("date", LocalDate.now().toString());
        mWDB.update(TABLE_NAME_WEIGHT, values, "user_name=? AND date=?", new String[]{current_username, LocalDate.now().toString()});

        ContentValues infoValues = new ContentValues();
        infoValues.put("name", current_username);
        infoValues.put("real_weight", weight);
        mWDB.update(TABLE_NAME_USER_INFO, infoValues, "name=?", new String[]{current_username});


    }

    public void deleteUser(String username) {
        mWDB.delete(TABLE_NAME_USER_INFO, "name=?", new String[]{username});
        mWDB.delete(TABLE_NAME_USER_PASSWORD, "user_name=?", new String[]{username});
    }

    public long storeFood(String selectedFood, double quantity, String category) {
        Log.d("tag:llxl", "storeFood called");

        String regex = "(.+), Per (.+) - Calories: (\\d+)kcal \\| Fat: (\\d+\\.\\d+)g \\| Carbs: (\\d+\\.\\d+)g \\| Protein: (\\d+\\.\\d+)g";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(selectedFood);
        if (matcher.find()) {
            Log.d("tag:llxl", "matcher found");
            String foodName = matcher.group(1);
            String unit = matcher.group(2);
            int calories = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
            double fat = Double.parseDouble(Objects.requireNonNull(matcher.group(4)));
            double carbs = Double.parseDouble(Objects.requireNonNull(matcher.group(5)));
            double protein = Double.parseDouble(Objects.requireNonNull(matcher.group(6)));

            // 输出提取的信息
//            Log.d("tag:llxl", "foodName: " + foodName);
//            Log.d("tag:llxl", "unit: " + unit);
//            Log.d("tag:llxl", "calories: " + calories);
//            Log.d("tag:llxl", "fat: " + fat);
//            Log.d("tag:llxl", "carbs: " + carbs);
//            Log.d("tag:llxl", "protein: " + protein);
            String currentDate = (LocalDate.now()).toString();

            String user_name = mHelper.getCurrentUsername();
            Food food = new Food(user_name, foodName, category, unit, calories, fat, carbs, protein, currentDate, quantity);
            return insertFood(food);

        } else {
            System.out.println("No matching pattern found");
            return -1;
        }
    }

    public long insertFood(Food food) {

        ContentValues values = new ContentValues();
        values.put("user_name", food.user_name);
        values.put("food_name", food.food_name);
        values.put("category", food.category);
        values.put("unit", food.unit);
        values.put("calories", food.calories);
        values.put("fat", food.fat);
        values.put("carbs", food.carbs);
        values.put("protein", food.protein);
        values.put("date", food.date);
        values.put("quantity", food.quantity);

        return mWDB.insert(TABLE_NAME_MEALS, null, values);
    }

    public List<List<Food>> getFoodsToday(){
        List<List<Food>> foodList = new ArrayList<>();
        List<Food> breakfastList = new ArrayList<>();
        List<Food> lunchList = new ArrayList<>();
        List<Food> dinnerList = new ArrayList<>();
        String currentDate = (LocalDate.now()).toString();
        String currentUserName = mHelper.getCurrentUsername();
        Cursor cursor = mRDB.query(TABLE_NAME_MEALS, null, "date=? AND user_name=?", new String[]{currentDate, currentUserName}, null, null, null);

        while (cursor.moveToNext()) {
            Food tempFood = new Food();
            tempFood.food_name = cursor.getString(2);
            tempFood.quantity = cursor.getDouble(9);
            tempFood.category = cursor.getString(3);
            tempFood.calories = cursor.getInt(5);
            tempFood.unit = cursor.getString(4);
            tempFood.fat = cursor.getDouble(6);
            tempFood.carbs = cursor.getDouble(6);
            tempFood.protein = cursor.getDouble(7);
            if(tempFood.category.equals("breakfast")){
                breakfastList.add(tempFood);
            } else if(tempFood.category.equals("lunch")){
                lunchList.add(tempFood);
            } else if(tempFood.category.equals("dinner")){
                dinnerList.add(tempFood);
            }
        }
        foodList.add(breakfastList);
        foodList.add(lunchList);
        foodList.add(dinnerList);

        return foodList;
    }

    public Map<String, Object> calculateNutrition(String category){

        Map<String, Object> nutritionMap = new HashMap<>();
        int totalCalories = 0;
        double totalFat = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        String currentDate = (LocalDate.now()).toString();
        String currentUserName = mHelper.getCurrentUsername();
        Cursor cursor = mRDB.query(TABLE_NAME_MEALS, null, "date=? AND user_name=? AND category=?", new String[]{currentDate, currentUserName, category}, null, null, null);
        while (cursor.moveToNext()) {
            Food tempFood = new Food();
            tempFood.quantity = cursor.getDouble(9);
            tempFood.calories = cursor.getInt(5);
            tempFood.fat = cursor.getDouble(6);
            tempFood.carbs = cursor.getDouble(6);
            tempFood.protein = cursor.getDouble(7);
            totalCalories += (int) (tempFood.quantity * tempFood.calories);
            totalFat += (int) (tempFood.quantity * tempFood.fat);
            totalCarbs += (int) (tempFood.quantity * tempFood.carbs);
            totalProtein += (int) (tempFood.quantity * tempFood.protein);
        }
        nutritionMap.put("totalCalories", totalCalories);
        nutritionMap.put("totalFat", totalFat);
        nutritionMap.put("totalCarbs", totalCarbs);
        nutritionMap.put("totalProtein", totalProtein);

        return nutritionMap;
    }

    public int calculateCalories(){

        int totalCalories = 0;

        String currentDate = (LocalDate.now()).toString();
        String currentUserName = mHelper.getCurrentUsername();
        Cursor cursor = mRDB.query(TABLE_NAME_MEALS, null, "date=? AND user_name=?", new String[]{currentDate, currentUserName}, null, null, null);
        while (cursor.moveToNext()) {
            Food tempFood = new Food();
            tempFood.quantity = cursor.getDouble(9);
            tempFood.calories = cursor.getInt(5);

            totalCalories += (int) (tempFood.quantity * tempFood.calories);

        }

        return totalCalories;
    }

    public List<Double> calculateMacroNutrition(){
        List<Double> macroNutritionList = new ArrayList<>();

        double totalFat = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        String currentDate = (LocalDate.now()).toString();
        String currentUserName = mHelper.getCurrentUsername();
        Cursor cursor = mRDB.query(TABLE_NAME_MEALS, null, "date=? AND user_name=?", new String[]{currentDate, currentUserName}, null, null, null);
        while (cursor.moveToNext()) {
            Food tempFood = new Food();
            tempFood.quantity = cursor.getDouble(9);
            tempFood.fat = cursor.getDouble(6);
            tempFood.carbs = cursor.getDouble(6);
            tempFood.protein = cursor.getDouble(7);
            totalFat += (int) (tempFood.quantity * tempFood.fat);
            totalCarbs += (int) (tempFood.quantity * tempFood.carbs);
            totalProtein += (int) (tempFood.quantity * tempFood.protein);
        }
        macroNutritionList.add(0, totalFat);
        macroNutritionList.add(1, totalCarbs);
        macroNutritionList.add(2, totalProtein);

        return macroNutritionList;
    }
}
