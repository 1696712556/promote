package com.kx.promote.ui.task_center;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kx.promote.R;
import com.kx.promote.bean.HistoryDateGroup;
import com.kx.promote.dao.TaskDao;
import com.kx.promote.utils.Msg;
import com.kx.promote.utils.MyApplication;
import com.kx.promote.utils.MyCallback;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryTaskFragment extends Fragment {
    List<HistoryDateGroup> dateList;
    private Handler handler = new MyHandler(this);
    protected static final int SHOW_TOAST = 0;
    protected static final int UPDATE_UI = 1;

    public HistoryTaskFragment() {
        // Required empty public constructor
    }

    public static HistoryTaskFragment newInstance() {
        HistoryTaskFragment fragment = new HistoryTaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_task, container, false);
        getHistoryDate();
        return view;
    }
    public void getHistoryDate(){
        TaskDao dao = TaskDao.getInstance();
        dao.getHistoryDate(new MyCallback() {
            @Override
            public void success(Msg msg) {
                dateList = (List<HistoryDateGroup>) msg.get("dateList");
                Log.d("历史任务", dateList.toString());
            }

            @Override
            public void failed(Msg msg) {
                handler.obtainMessage(SHOW_TOAST,msg.getMsg()).sendToTarget();
            }
        });
    }

    public void updateUI(){

    }


    static class MyHandler extends Handler {
        private WeakReference<HistoryTaskFragment> mOuter;

        public MyHandler(HistoryTaskFragment activity) {
            mOuter = new WeakReference<HistoryTaskFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HistoryTaskFragment outer = mOuter.get();
            if (outer != null) {
                if(msg.what == SHOW_TOAST){
                    String text = (String) msg.obj;
                    Toast.makeText(MyApplication.getContext(),text,Toast.LENGTH_SHORT).show();
                }
                else if (msg.what == UPDATE_UI) {
                    outer.updateUI();
                }
            }
        }
    }

}
