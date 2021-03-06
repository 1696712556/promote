package com.kx.promote.dao;

import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.kx.promote.bean.Group;
import com.kx.promote.bean.HistoryDateGroup;
import com.kx.promote.bean.User;
import com.kx.promote.utils.DateToolkit;
import com.kx.promote.utils.HttpUtil;
import com.kx.promote.utils.Msg;
import com.kx.promote.utils.MyApplication;
import com.kx.promote.utils.MyCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskDao {
    private static final TaskDao instance = new TaskDao();//直接先实例化一个，确保单例模式
    public void getGroupListByDate(Date date, final MyCallback callback){
        date = DateToolkit.getZeroOfDay(date);//把日期转为0点
        String url = MyApplication.getAppPath()+"/interface/worker/groupListByTime/"+date.getTime();
        HttpUtil.get(url, new GroupListCallback(callback));
    }
    public void getTodayGroupList(final MyCallback callback){
        String url = MyApplication.getAppPath()+"/interface/worker/groupList/today";
        HttpUtil.get(url, new GroupListCallback(callback));
    }
    public void getHistoryDate(final MyCallback callback){
        String url = MyApplication.getAppPath()+"/interface/worker/historyDate";
        HttpUtil.get(url, new MyCallback() {
            @Override
            public void success(Msg msg) {
                if(msg.getCode()==0){
                    JSONArray jsonArray = (JSONArray)msg.get("dateList");
                    String json = jsonArray.toJSONString();
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong());//时间戳自动转时间
                        }
                    });
                    Gson gson = builder.create();
                    List<HistoryDateGroup> dateList = (List<HistoryDateGroup>) gson.fromJson(json, new TypeToken<List<HistoryDateGroup>>() {
                    }.getType());//把字符串转换成集合
                    msg.put("dateList",dateList);
                }
                callback.success(msg);
            }

            @Override
            public void failed(Msg msg) {
                callback.failed(msg);
            }
        });
    }
    public void getGroupById(Integer id, final MyCallback callback){
        String url = MyApplication.getAppPath() +"/interface/worker/do/"+id;
        HttpUtil.get(url, new MyCallback() {
            @Override
            public void success(Msg msg) {
                if(msg.getCode()==0){
                    Group group = JSONObject.toJavaObject((JSON) msg.get("group"),Group.class);
                    msg.put("group",group);
                }
                callback.success(msg);
            }

            @Override
            public void failed(Msg msg) {
                callback.failed(msg);
            }
        });
    }
    public void submitTask(Group group,final MyCallback callback){
        String url = MyApplication.getAppPath() +"/interface/worker/do";
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(JSON.toJSONString(group),mediaType);
        HttpUtil.post(url,body,callback);
    }
    public static TaskDao getInstance(){//单例模式，需要的时候获取已经new好的
        return instance;
    }
    class GroupListCallback implements MyCallback{
        MyCallback callback;
        public GroupListCallback(MyCallback callback){
            this.callback = callback;
        }
        @Override
        public void success(Msg msg) {
            if(msg.getCode()==0) {
                JSONArray groupJsonArray = (JSONArray) msg.get("groupList");
                String answerString = groupJsonArray.toJSONString();//将array数组转换成字符串
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());//时间戳自动转时间
                    }
                });
                Gson gson = builder.create();
                List<Group> groupList = (List<Group>) gson.fromJson(answerString, new TypeToken<List<Group>>() {
                }.getType());//把字符串转换成集合
                msg.put("groupList", groupList);
            }
            callback.success(msg);
        }

        @Override
        public void failed(Msg msg) {
            callback.failed(msg);
        }
    }
}
