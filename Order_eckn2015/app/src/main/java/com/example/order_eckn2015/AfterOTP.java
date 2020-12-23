package com.example.order_eckn2015;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AfterOTP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_o_t_p);
        startLogging.startLog();
    }
}