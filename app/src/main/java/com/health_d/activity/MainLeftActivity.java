package com.health_d.activity;

import android.app.Activity;
import android.os.Bundle;

import com.health_d.R;

import butterknife.ButterKnife;


/**
 * Created by Admin on 2016/8/5. 主界面左侧界面
 */
public class MainLeftActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_left);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }
}
