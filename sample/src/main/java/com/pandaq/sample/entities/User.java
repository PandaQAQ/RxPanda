package com.pandaq.sample.entities;

import android.support.annotation.NonNull;

import com.pandaq.rxpanda.utils.GsonUtil;

//@AutoWired
public class User {

    private UserInfo info;
    private String group;

    @NonNull
    @Override
    public String toString() {
        return GsonUtil.gson().toJson(this);
    }
}