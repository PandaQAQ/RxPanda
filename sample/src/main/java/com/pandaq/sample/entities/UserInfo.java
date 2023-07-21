package com.pandaq.sample.entities;

import androidx.annotation.NonNull;

import com.pandaq.ktpanda.utils.GsonUtil;

//@AutoWired
public class UserInfo {

    private int userId;
    private String userName;
    private String nickName;
    private int age;
    private String notExist;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return GsonUtil.INSTANCE.gson().toJson(this);
    }
}