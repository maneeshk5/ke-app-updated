package com.authentik.ke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstAppUse extends AppCompatActivity {

    Button submit_ip_btn;
    EditText ip_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_app_use);

        submit_ip_btn = findViewById(R.id.submit_ip_btn);
        ip_et = findViewById(R.id.ip_et);

        submit_ip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip_et.getText().length() == 0) {
                    Toast.makeText(FirstAppUse.this, "Please Enter a valid url", Toast.LENGTH_SHORT).show();
                }
                else {
                    String ip_addr = ip_et.getText().toString();
                    SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("server_url", "http://" + ip_addr + "/ke_app_api/");
                    editor.apply();

                    getSharedPreferences("Preference",MODE_PRIVATE).edit().putBoolean("isFirstRun",true).apply();
                    finish();
                    startActivity(new Intent(FirstAppUse.this,SplashScreen.class));
                }
            }
        });





    }
}