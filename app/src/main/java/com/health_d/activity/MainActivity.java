package com.health_d.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.health_d.R;
import com.health_d.activity.test.UmActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 2016/7/27. 主界面
 */
public class MainActivity extends ActivityGroup {


    @BindView(R.id.m_name)
    TextView mName;
    @BindView(R.id.m_sex)
    TextView mSex;
    @BindView(R.id.m_age)
    TextView mAge;
    @BindView(R.id.m_scan)
    Button mScan;
    @BindView(R.id.m_save)
    Button mSave;
    @BindView(R.id.m_user_info)
    Button mUserInfo;
    @BindView(R.id.m_test_info)
    Button mTestInfo;
    @BindView(R.id.m_sys_info)
    Button mSysInfo;
    @BindView(R.id.data_pager)
    ViewPager dataPager;

    private ArrayList<View> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        init();
    }

    private void initView() {

        list = new ArrayList<>();
        View viewLeft = getLocalActivityManager().startActivity("activity01",
                new Intent(this, MainLeftActivity.class)).getDecorView();
        list.add(viewLeft);

        View viewRight = getLocalActivityManager().startActivity("activity02",
                new Intent(this, MainRightActivity.class)).getDecorView();
        list.add(viewRight);

        dataPager.setAdapter(new MyPagerView());
        dataPager.clearAnimation();
        dataPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //SetBtnColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private void init() {

    }

    private class MyPagerView extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(list.get(arg1));
        }

        /***
         * 获取每一个item， 类于listview中的getview
         */
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(list.get(arg1));
            return list.get(arg1);
        }
    }

    @OnClick(R.id.m_save)
    protected void Save() {

    }

    @OnClick(R.id.m_user_info)
    protected void setUserInfo() {
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.m_test_info)
    protected void setTestInfo() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.m_sys_info)
    protected void setSysInfo() {
        Intent intent = new Intent(this, SystemActivity.class);
        startActivity(intent);
    }

}
