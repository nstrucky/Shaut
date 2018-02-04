package com.ventoray.shaut.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ventoray.shaut.R;

public class PreSignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_in);


        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
