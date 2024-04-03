package com.example.application.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.application.entity.User;

import java.util.ArrayList;
import java.util.List;


public class NutriPalDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nutripal.db";
    private static final int DB_VERSION = 1;
    private static NutriPalDBHelper mHelper = null;
    private static final String TABLE_NAME_USER_INFO = "user_info";
    private static final String TABLE_NAME_USER_PASSWORD = "user_password";

    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private String current_username = null;

    private NutriPalDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static NutriPalDBHelper getInstance(Context context){
        if(mHelper == null){
            mHelper = new NutriPalDBHelper(context);
        }
        Log.d("llxl","returned a instance");
        return mHelper;
    }

    public SQLiteDatabase openReadLink(){
        if (mRDB == null || !mRDB.isOpen()){
            mRDB = mHelper.getReadableDatabase();

        }
        return mRDB;
    }

    public SQLiteDatabase openWriteLink(){
        if (mWDB == null || !mWDB.isOpen()){
            mWDB = mHelper.getWritableDatabase();
        }
        return mRDB;
    }

    public void closeLink(){
        if(mRDB != null && mRDB.isOpen()){
            mRDB.close();
            mRDB = null;
        }

        if(mWDB != null && mWDB.isOpen()){
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("llxl","it is creating a new table");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_INFO + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " profile_photo VARCHAR NOT NULL," +
                " birth VARCHAR NOT NULL," +
                " height INTEGER NOT NULL," +
                " real_weight INTEGER NOT NULL," +
                " age INTEGER NOT NULL," +
                " target_weight INTEGER NOT NULL);" ;

        String sql_pw = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_PASSWORD + " (" +
                " user_name VARCHAR PRIMARY KEY NOT NULL," +
                " password VARCHAR NOT NULL);" ;


        db.execSQL(sql);
        db.execSQL(sql_pw);
    }

    public boolean userExistsInDatabase(String userName){

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

    public boolean checkPassword(String username, String password){

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

    public long insert(User user){
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

    public long update(User user){
        ContentValues values = new ContentValues();
        values.put("name", user.name);
        values.put("age", user.age);
        values.put("profile_photo", user.photo_name);
        values.put("birth", user.birth);
        values.put("height", user.height);
        values.put("real_weight", user.real_weight);
        values.put("target_weight", user.target_weight);
        return mWDB.update(TABLE_NAME_USER_INFO, values, "name=?", new String[]{user.name});
    }

    public long registerUser(String username,String password){
        ContentValues values = new ContentValues();
        values.put("user_name",username);
        values.put("password",password);
        return mWDB.insert(TABLE_NAME_USER_PASSWORD,null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("llxl","it is creating a new table");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_INFO + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " profile_photo VARCHAR ," +
                " birth VARCHAR ," +
                " height INTEGER ," +
                " real_weight INTEGER NOT NULL," +
                " age INTEGER ," +
                " target_weight INTEGER NOT NULL);" ;

        String sql_pw = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USER_PASSWORD + " (" +
                " user_name VARCHAR PRIMARY KEY NOT NULL," +
                " password VARCHAR NOT NULL);" ;


        db.execSQL(sql);
        db.execSQL(sql_pw);
    }

    public void setCurrentUsername(String username){
        mHelper.current_username = username;
    }

    public String getCurrentUsername(){
        return mHelper.current_username;
    }

    public User getUser(String username){
        User user = new User();

        Cursor cursor = mRDB.query(TABLE_NAME_USER_INFO, null, "name=?", new String[]{username}, null, null, null);
        while (cursor.moveToNext()){

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
}
