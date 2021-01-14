package com.authentik.ke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Help_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help__page);
    }

    public void goBack(View view) {
        finish();
    }
}