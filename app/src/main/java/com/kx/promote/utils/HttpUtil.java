package com.kx.promote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.kx.promote.dao.UserDao;
import com.kx.promote.ui.HomeActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static Msg dealMsgResponse(Response response) throws IOException {
        String json = response.body().string();
        Msg msg = JSON.parseObject(json,Msg.class);
        if(msg.getCode()==100){//登录状态失效
            UserDao.getInstance().logout();//强制下线
        }
        return msg;
    }

    public static void get(String url, final MyCallback callback){
        Callback oCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Msg msg = new Msg();
                msg.set(1000,e.getMessage());
                callback.failed(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Msg msg = dealMsgResponse(response);
                callback.success(msg);
            }
        };
        get(url, oCallback);
    }

    public static void get(String url, Callback callback){
        OkHttpClient client = new OkHttpClient().newBuilder().cookieJar(new MyCookieJar(MyApplication.getContext())).build();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
    public static void post(String url, RequestBody requestBody, Callback callback){
        OkHttpClient client = new OkHttpClient().newBuilder().cookieJar(new MyCookieJar(MyApplication.getContext())).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
    public static void post(String url, RequestBody requestBody, final MyCallback callback){
        Callback oCallback = new Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Msg msg = dealMsgResponse(response);
                callback.success(msg);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Msg msg = new Msg();
                msg.set(1000,e.getMessage());
                callback.failed(msg);
            }
        };
        post(url,requestBody,oCallback);
    }
    public static void getImage(String url, final Handler handler, final int successMess){
        get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                byte[] bytes = response.body().bytes();
                //使用BitmapFactory工厂，把字节数组转化为bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Message.obtain(handler,successMess,bitmap).sendToTarget();
            }
        });
    }
    public static void getImage(String url, final MyCallback callback){
        get(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.failed(Msg.fail(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                byte[] bytes = response.body().bytes();
                //使用BitmapFactory工厂，把字节数组转化为bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Msg msg = Msg.success().add("bitmap",bitmap);
                callback.success(msg);
            }
        });
    }

}
