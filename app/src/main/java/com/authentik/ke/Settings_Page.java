package com.authentik.ke;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.authentik.utils.Constant;

public class Settings_Page extends AppCompatActivity {

    ListView settings_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__page);

//        String server_url = Constant.SERVER_URL;
//        Log.i("Base url",server_url);

        settings_lv = findViewById(R.id.settings_lv);

        settings_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    Toast.makeText(Settings_Page.this,"Enter New Server Url",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Page.this);
                    builder.setView(null).setMessage(null);
                    builder.setMessage("Enter new Server Url: ");
                    final EditText input_server_url = new EditText(Settings_Page.this);
                    input_server_url.setText("http://");
                    input_server_url.setSelection(input_server_url.getText().length());
//                    input_server_url.setLayoutParams(new LinearLayout.LayoutParams(10, 10,1f));
//                    input_server_url.setPadding(30,10,30,10);


                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (input_server_url.getText().length() == 0) {
                                Toast.makeText(Settings_Page.this,"Please Enter a valid url",Toast.LENGTH_SHORT).show();
                            }
                            else {

                                SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("server_url",input_server_url.getText().toString());
                                editor.apply();
                                Toast.makeText(Settings_Page.this,"App needs a restart after server change",Toast.LENGTH_SHORT).show();
                                Log.i("New Server Url",input_server_url.getText().toString());
                                finishAffinity();
                            }
                        }
                    });
                    builder.setView(input_server_url);
                    builder.create().show();
                }
            }
        });
    }
}