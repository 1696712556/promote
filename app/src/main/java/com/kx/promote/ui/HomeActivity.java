package com.kx.promote.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kx.promote.R;
import com.kx.promote.entity.TabEntity;
import com.kx.promote.ui._do.DoFragment;
import com.kx.promote.ui._do.ImageUploaderFragment;
import com.kx.promote.ui.image_selector.MultiImageSelectorActivity;
import com.kx.promote.ui.task_center.TaskCenterFragment;
import com.kx.promote.utils.MyApplication;
import com.kx.promote.utils.ViewFindUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    private DoFragment doFragment;
    private TaskCenterFragment taskCenterFragment;
    private UserCenterFragment userCenterFragment;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ImageUploaderFragment imageUploader;
    public final static int IMAGE_SELECTED = 2;
    private String[] mTitles = {"任务中心", "当前任务", "我"};
    public final static int TASK_CENTER = 0;
    public final static int DO_TASK = 1;
    public final static int USER_CENTER = 2;

    private int[] mIconUnselectIds = {
            R.mipmap.tab_home_unselect, R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect};
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select};

    private CommonTabLayout footer;
    private MyPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private View mDecorView;
    Random mRandom = new Random();
    private Handler handler = new MyHandler(this);

    public final static int SHOW_MESSAGE = 0;
    public void setImageUploader(ImageUploaderFragment imageUploader){
        this.imageUploader = imageUploader;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MyApplication.setHomeActivity(this);
        taskCenterFragment = new TaskCenterFragment();
        doFragment = new DoFragment();
        userCenterFragment = new UserCenterFragment();
        mFragments.add(taskCenterFragment);
        mFragments.add(doFragment);
        mFragments.add(userCenterFragment);


        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }


        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.home_body);
        mViewPager.setOffscreenPageLimit(3);//设置缓存3个
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        /** with ViewPager */
        footer = ViewFindUtils.find(mDecorView, R.id.home_footer);
        initFooterBar();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_SELECTED) {
            if (resultCode == RESULT_OK) {
                List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                List<String> urlList = new ArrayList<>(mSelectPath.size());
                for (String p : mSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                    Uri uri = Uri.fromFile(new File(p));
                    urlList.add(uri.toString());
                }
                Log.d("Image", sb.toString());
                Toast.makeText(this,sb.toString(),Toast.LENGTH_SHORT).show();
                imageUploader.add(urlList);
            }
        }
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    protected int dp2px(float dp) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    private void initFooterBar() {
        footer.setTabData(mTabEntities);
        footer.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
                    footer.showMsg(0, mRandom.nextInt(100) + 1);
//                    UnreadMsgUtils.show(header.getMsgView(0), mRandom.nextInt(100) + 1);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                footer.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(HomeActivity.TASK_CENTER);
    }
    public DoFragment getDoFragment(){
        return doFragment;
    }
    public TaskCenterFragment getTaskCenterFragment(){
        return taskCenterFragment;
    }
    public ViewPager getViewPager(){
        return mViewPager;
    }
    public Handler getHandler(){return handler;}
    static class MyHandler extends Handler {
        private WeakReference<HomeActivity> mOuter;

        public MyHandler(HomeActivity activity) {
            mOuter = new WeakReference<HomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeActivity outer = mOuter.get();
            if (outer != null) {
                if (msg.what == SHOW_MESSAGE) {
                    String tip = (String) msg.obj;
                    Toast.makeText(outer, tip, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
