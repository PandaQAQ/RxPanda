package com.pandaq.rxpanda.entity;

/**
 * Created by huxinyu on 2019/6/18.
 * Email : panda.h@foxmail.com
 * <p>
 * Description :
 */
public interface IApiData<T> {

    Long getCode();

    String getMsg();

    T getData();

    boolean isSuccess();
}
