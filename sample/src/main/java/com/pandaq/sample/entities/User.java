package com.pandaq.sample.entities;

import androidx.annotation.NonNull;

import com.pandaq.ktpanda.utils.GsonUtil;

//@AutoWired
public class User {

    private UserInfo info;
    private String group;

    @NonNull
    @Override
    public String toString() {
        return GsonUtil.INSTANCE.gson().toJson(this);
    }
}