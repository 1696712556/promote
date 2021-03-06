package com.kx.promote.ui._do;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kx.promote.R;
import com.kx.promote.bean.Group;
import com.kx.promote.bean.Order;
import com.kx.promote.dao.TaskDao;
import com.kx.promote.entity.TabEntity;
import com.kx.promote.ui.HomeActivity;
import com.kx.promote.utils.HttpUtil;
import com.kx.promote.utils.Msg;
import com.kx.promote.utils.MyApplication;
import com.kx.promote.utils.MyCallback;
import com.kx.promote.utils.ViewFindUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String appPath;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private CommonTabLayout header;
    private MyPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private View view;
    private Group group;

    private OverviewFragment overviewFragment;
    private List<TaskFragment> taskFragmentList;
    Random mRandom = new Random();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Handler handler = new MyHandler(this);


    public final static int UPDATE_UI = 0;
    public final static int SHOW_MESSAGE = 1;

    public DoFragment() {
        // Required empty public constructor
     }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DoFragment newInstance(String param1, String param2) {
        DoFragment fragment = new DoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_do, container, false);
        this.view = view;
        mViewPager = ViewFindUtils.find(view, R.id.do_body);//在view中寻找do_body
        /** with ViewPager */
        header = ViewFindUtils.find(view, R.id.do_header);

        initHeaderBar();
        updateUI();
        return view;
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabEntities.get(position).getTabTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
        public int getItemPosition(Object object) {
            int index = mFragments.indexOf (object);

            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }
    }

    protected int dp2px(float dp) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void initHeaderBar() {
        if(mTabEntities!=null && !mTabEntities.isEmpty()) {
            header.setTabData(mTabEntities);
        }
        header.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                header.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(0);

    }
    public void updateUI(){
        mFragments= new ArrayList<>();
        mAdapter = new MyPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mTabEntities.clear();
        if(group==null || group.getOrderlist()==null)
            return;
        overviewFragment = OverviewFragment.newInstance(group);
        mFragments.add(overviewFragment);
        mTabEntities.add(new TabEntity("概览", 0,0));
        for(Order order:group.getOrderlist()){
            TaskFragment taskFragment = TaskFragment.newInstance(order);
            mFragments.add(taskFragment);
            TabEntity tabEntity = new TabEntity(String.valueOf(mTabEntities.size()), R.mipmap.tab_speech_select, R.mipmap.tab_speech_unselect);
            mTabEntities.add(tabEntity);
        }
        mFragments.add(SubmitFragment.newInstance(group));
        mTabEntities.add(new TabEntity("提交", 0,0));
        header.setTabData(mTabEntities);
        mAdapter.notifyDataSetChanged();
        header.setCurrentTab(0);
    }
    public Group getGroup(){
        return group;
    }
    public void getGroupById(Integer groupId){
        this.group = new Group();
        TaskDao dao = TaskDao.getInstance();
        MyApplication.loading(true,getActivity());
        dao.getGroupById(groupId, new MyCallback() {
            @Override
            public void success(Msg msg) {
                if(msg.getCode()==0) {
                    group = (Group) msg.get("group");
                    Message.obtain(handler, UPDATE_UI).sendToTarget();//通知主线程更新界面
                    if(group.getState()==Group.FINISHED){
                        handler.obtainMessage(SHOW_MESSAGE,"当前任务已完成！").sendToTarget();
                    }
                }
                else{
                    handler.obtainMessage(SHOW_MESSAGE,msg.getMsg()).sendToTarget();
                }
                MyApplication.loading(false,getActivity());
            }
            @Override
            public void failed(Msg msg) {
                group = null;
                handler.obtainMessage(SHOW_MESSAGE,msg.getMsg()).sendToTarget();
                MyApplication.loading(false,getActivity());
            }
        });
    }
    static class MyHandler extends Handler {
        private WeakReference<DoFragment> mOuter;

        public MyHandler(DoFragment activity) {
            mOuter = new WeakReference<DoFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DoFragment outer = mOuter.get();
            if (outer != null) {
                if (msg.what == UPDATE_UI) {
                    outer.updateUI();
                }
                else if(msg.what == SHOW_MESSAGE){
                    String text = (String) msg.obj;
                    HomeActivity homeActivity = MyApplication.getHomeActivity();
                    Toast.makeText(homeActivity,text,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
