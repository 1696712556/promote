package com.kx.promote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kx.promote.R;
import com.kx.promote.utils.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateUserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText userNameEdit;
    private EditText personNameEdit;
    private EditText userPhoneEdit;
    private EditText userMailEdit;
    private EditText newPasswordEdit;
    private EditText confirmPasswordEdit;
    // 主线程创建消息处理器
    protected static final int IMAGE_UPDATE = 1;
    protected static final int ERROR = 2;
//    private Handler handler = new UpdateUserInfoActivity.MyHandler(this);
    private String appPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        appPath = getString(R.string.app_path);

        userNameEdit=(EditText)findViewById(R.id.editText_userName);
        personNameEdit=(EditText)findViewById(R.id.editText_personName);
        personNameEdit.setFocusableInTouchMode(false);
        userPhoneEdit=(EditText)findViewById(R.id.editText_userPhone);
        userMailEdit=(EditText)findViewById(R.id.editText_mail);
        newPasswordEdit=(EditText)findViewById(R.id.editText_newPassword);
        newPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordEdit=(EditText)findViewById(R.id.editText_confirmPassword);

        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        Button btn_save=(Button)findViewById(R.id.btn_save);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        Intent intent=getIntent();
        String userName=intent.getStringExtra("userName");
        userNameEdit.setText(userName);

        //获取登录用户信息
        HttpUtil.get(appPath+"/interface/user?name="+userNameEdit.getText().toString(),new okhttp3.Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.d("update++",result);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(UpdateUserInfoActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateUserInfoActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("退出后将不会保存修改的信息，确定要退出吗");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //返回上一个上一个界面
                        Intent intent=new Intent(UpdateUserInfoActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
                break;

            case R.id.btn_save:
                Toast.makeText(UpdateUserInfoActivity.this, "点击了完成", Toast.LENGTH_SHORT).show();

                MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
                Map<String,String> user = new HashMap<>();
                user.put("userNameEdit",userNameEdit.getText().toString());
                user.put("personNameEdit",personNameEdit.getText().toString());
                user.put("userPhoneEdit",userPhoneEdit.getText().toString());
                user.put("userMailEdit",userMailEdit.getText().toString());
                user.put("newPasswordEdit",newPasswordEdit.getText().toString());
                user.put("confirmPasswordEdit",confirmPasswordEdit.getText().toString());
                RequestBody body = RequestBody.create(JSON.toJSONString(user),mediaType);
                HttpUtil.post(appPath+"/interface/user",body, new okhttp3.Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Toast.makeText(UpdateUserInfoActivity.this, "修改失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                String result = response.body().string();
                                parseJSONWithJSONObject(result);
                                Toast.makeText(UpdateUserInfoActivity.this,"result="+result,Toast.LENGTH_SHORT).show();
                                Log.d("修改成功++",result);
                            }
                        });
                break;
            default:
                break;
        }
    }
    private void parseJSONWithJSONObject(String jsonDate){
//        try {
//            JSONArray jsonArray = new JSONArray(jsonDate);
//            for(int i=0;i<jsonArray.length();i++){
//
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
