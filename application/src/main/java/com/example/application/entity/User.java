package com.example.application.entity;

import java.time.LocalDate;

public class User {
    public int id; // 序号
    public String name; // 姓名
    public int age; // 年龄
    public String photo_name; //头像图片文件名
    public String birth;
    public int height;
    public int real_weight;
    public int target_weight;

    public User(){

    }

    public User(String name, int age,String photo_name,String birth,int height,int real_weight,int target_weight) {
        this.name = name;
        this.age = age;
        this.photo_name = photo_name;
        this.birth = birth;
        this.height = height;
        this.real_weight = real_weight;
        this.target_weight = target_weight;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}
