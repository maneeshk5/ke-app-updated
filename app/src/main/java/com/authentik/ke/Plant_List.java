package com.authentik.ke;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Plant_List extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant__list);

//        Button btn = findViewById(R.id.open_system_list);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Plant_List.this, System_List.class);
//                startActivity(intent);
//            }
//        });

//        int itemCount = 10;
//
//        TableLayout tl = findViewById(R.id.plant_table);
//
//        for (int i=0; i<itemCount; i++) {
//            TextView sys_name = new TextView(this);
//            TextView status = new TextView(this);
//
//            sys_name.setText("System " + i);
//            sys_name.setTextColor(Color.BLACK);
//            status.setText("Status " + i);
//            status.setTextColor(Color.BLACK);
//
//            TableRow tr = new TableRow(this);
//            tr.addView(sys_name);
//            tr.addView(status);
//
//            Log.v("System and Status:" ,sys_name.toString());
//
//            tl.addView(tr);
//        }


    }
}