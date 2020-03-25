package com.pandaq.sample.entities;

import android.support.annotation.NonNull;

import com.pandaq.rxpanda.annotation.AutoWired;
import com.pandaq.rxpanda.utils.GsonUtil;

//@AutoWired
public class UserInfo {

    private int userId;
    private String userName;
    private String nickName;
    private String companyId;
    private Integer age;
    private Integer notExist;

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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
        return GsonUtil.gson().toJson(this);
    }
}