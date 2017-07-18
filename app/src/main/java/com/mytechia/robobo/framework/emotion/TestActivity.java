package com.mytechia.robobo.framework.emotion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mytechia.robobo.framework.hri.emotion.unity.UnityPlayerActivity;
import com.mytechia.robobo.framework.hri.emotion.webgl.WebGLEmotionDisplayActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent myIntent = new Intent(this,UnityPlayerActivity.class);
        this.startActivity(myIntent);

    }
}
