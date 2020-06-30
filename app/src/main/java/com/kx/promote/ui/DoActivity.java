package com.kx.promote.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kx.promote.R;
import com.kx.promote.entity.TabEntity;
import com.kx.promote.ui._do.OverviewFragment;
import com.kx.promote.ui._do.TaskFragment;
import com.kx.promote.utils.ViewFindUtils;

import java.util.ArrayList;
import java.util.Random;





//已废弃，被DoFragment取代 ，可以留着参考学习用






public class DoActivity extends AppCompatActivity {
    private Context mContext = this;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"纵览", "1", "2", "3", "4", "5", "提交"};
    private int[] mIconUnselectIds = {
            R.mipmap.tab_home_unselect, R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect, R.mipmap.tab_more_unselect
            , R.mipmap.tab_more_unselect, R.mipmap.tab_more_unselect
            , R.mipmap.tab_more_unselect};
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select, R.mipmap.tab_more_select
            , R.mipmap.tab_more_select, R.mipmap.tab_more_select
            , R.mipmap.tab_more_select};

    private CommonTabLayout header;
    private DoActivity.MyPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private View mDecorView;
    Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do);
        mFragments.add(new OverviewFragment());
        for (int i =1;i<mTitles.length;i++) {
            mFragments.add(new TaskFragment());
//            mFragments.add(SimpleCardFragment.getInstance("Switch ViewPager " + title));
        }


        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }


        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.home_body);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        /** with ViewPager */
        header = ViewFindUtils.find(mDecorView, R.id.do_header);
        tl_2();

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
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    private void tl_2() {
        header.setTabData(mTabEntities);
        header.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
                    header.showMsg(0, mRandom.nextInt(100) + 1);
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
                header.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(1);
    }
}