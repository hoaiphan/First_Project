package com.example.gio.firstproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.gio.firstproject.R;

/**
 * Created by Gio on 3/9/2017.
 */

public class HeaderInformationLayoutActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_information);

        ImageButton imgBtnBack = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(this);
        ImageButton imgBtnSettings = (ImageButton) findViewById(R.id.imgBtnSettings);
        imgBtnSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imgBtnSettings:
                //Navigate to Information Layout  Screen
                startActivity(new Intent(this, ListUserActivity.class));
                break;
            case  R.id.imgBtnBack:
                finish();
        }
    }
}