package com.health_d.activity.test;

import android.app.Activity;
import android.os.Bundle;

import com.health_d.R;

import butterknife.ButterKnife;

/**
 * Created by Admin on 2016/8/3.
 */
public class UmActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_um);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }
}
