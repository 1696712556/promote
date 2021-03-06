package com.kx.promote.ui._do;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kx.promote.R;
import com.kx.promote.bean.Group;
import com.kx.promote.bean.Order;
import com.kx.promote.bean.User;
import com.kx.promote.dao.TaskDao;
import com.kx.promote.ui.HomeActivity;
import com.kx.promote.ui.LoginActivity;
import com.kx.promote.utils.HttpUtil;
import com.kx.promote.utils.Msg;
import com.kx.promote.utils.MyApplication;
import com.kx.promote.utils.MyCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubmitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubmitFragment extends Fragment {
    private static final String TAG = "SubmitFragment";
    private EditText actpriceET;
    private EditText customerET;
    private EditText noteET;
    private ImageUploaderFragment submitImageFragment;
    private Button submitButton;

    private Group group;

    // 主线程创建消息处理器
    protected static final int SHOW_TOAST = 0;
    protected static final int GO_TASK_CENTER = 1;
    protected static final int ERROR = 2;
    protected static final int FRESH_GROUP = 3;
    protected static final int SUBMITED = 4;
    private Handler handler = new MyHandler(this);

    public SubmitFragment() {
        // Required empty public constructor
    }

    public static SubmitFragment newInstance(Group group) {
        SubmitFragment fragment = new SubmitFragment();
        Bundle args = new Bundle();
        args.putSerializable("group",group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = (Group) getArguments().getSerializable("group");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_do_submit, container, false);
        actpriceET = view.findViewById(R.id.group_actprice);
        customerET = view.findViewById(R.id.group_customer);
        noteET = view.findViewById(R.id.group_note);
        submitButton = view.findViewById(R.id.group_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        customerET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                group.setCustomer(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                group.setNote(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        submitImageFragment = (ImageUploaderFragment) getChildFragmentManager().findFragmentById(R.id.group_submit_image);
        updateUI();
        return view;
    }
    private void updateUI(){
        if(group==null)
            return;
        submitImageFragment.set(group.getImagelist(), MyApplication.getGroupImageMaxNumber());
        actpriceET.setText(group.getActprice()+"元");
        customerET.setText(group.getCustomer());
        noteET.setText(group.getNote());
    }
    private void submit(){
        Group group = new Group();
        group.setId(this.group.getId());
        group.setCustomer(this.group.getCustomer());
        group.setNote(this.group.getNote());
        group.setImagelist(this.group.getImagelist());
        List<Order> orderList = new ArrayList<>();
        boolean allImageUploaded = true;
        for(Order order:this.group.getOrderlist()){
            Order newOrder = new Order();
            newOrder.setId(order.getId());
            newOrder.setPrice(order.getPrice());
            newOrder.setNo(order.getNo());
            newOrder.setState(order.getState());
            if(order.getImagelist()!=null) {//检查每个Order任务图片是否已经上传
                for (String imgUrl : order.getImagelist()){
                    if(imgUrl.indexOf("http")<0){
                        allImageUploaded = false;
                        break;
                    }
                }
                if(!allImageUploaded)
                    break;
            }
            newOrder.setImagelist(order.getImagelist());
            orderList.add(newOrder);
        }
        if(allImageUploaded && group.getImagelist()!=null){//检查group的每个图片是否都已上传
            for (String imgUrl : group.getImagelist()){
                if(imgUrl.indexOf("http")<0){
                    allImageUploaded = false;
                    break;
                }
            }
        }
        if(!allImageUploaded){
            Toast.makeText(getActivity(),"有图片上传失败了，请重传上传失败的图片！",Toast.LENGTH_SHORT).show();
            return;
        }
        group.setOrderlist(orderList);
        TaskDao dao = TaskDao.getInstance();
        dao.submitTask(group, new MyCallback() {
            @Override
            public void success(Msg msg) {
                if(msg.getCode()==0){//判断是否成功
                    Message.obtain(handler,SHOW_TOAST,"提交成功！").sendToTarget();
                    Message.obtain(handler,SUBMITED).sendToTarget();//前往主页、执行刷新数据等操作
                }
                else{//登录失败
                    Message.obtain(handler,SHOW_TOAST,msg.getMsg()).sendToTarget();//给主线程发送信息，让主线程弹出Toast
                }
            }

            @Override
            public void failed(Msg msg) {
                Message.obtain(handler,SHOW_TOAST,msg.getMsg()).sendToTarget();//给主线程发送信息，让主线程弹出Toast
            }
        });
    }
    private void submited(){
        HomeActivity homeActivity = MyApplication.getHomeActivity();
        homeActivity.getTaskCenterFragment().getTodayTaskFragment().getTodayTask();//刷新今日任务列表
        goTaskCenter();
        freshGroup();
    }
    private void goTaskCenter(){
        HomeActivity homeActivity = MyApplication.getHomeActivity();
        homeActivity.getViewPager().setCurrentItem(HomeActivity.TASK_CENTER);
    }
    private void freshGroup(){
        HomeActivity homeActivity = MyApplication.getHomeActivity();
        homeActivity.getDoFragment().getGroupById(group.getId());
    }
    static class MyHandler extends Handler {
        private WeakReference<SubmitFragment> mOuter;

        public MyHandler(SubmitFragment activity) {
            mOuter = new WeakReference<SubmitFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SubmitFragment outer = mOuter.get();
            if (outer != null) {
                if (msg.what == SHOW_TOAST) {
                    String text = (String) msg.obj;
                    Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT).show();
                } else if (msg.what == GO_TASK_CENTER) {
                    outer.goTaskCenter();
                } else if (msg.what == FRESH_GROUP) {
                    outer.freshGroup();
                } else if (msg.what == SUBMITED) {
                    outer.submited();
                } else if (msg.what == ERROR) {
                    Toast.makeText(MyApplication.getContext(), "显示图片错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
