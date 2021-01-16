package com.authentik.ke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
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

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.model.User;
import com.authentik.utils.Constant;
import com.authentik.utils.DatabaseHelper;
import com.authentik.utils.SyncDbService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Settings_Page extends AppCompatActivity {

    ListView settings_lv;
    DatabaseHelper db;
    String usersURL;
    String plantsURL;
    String systemsURL;
    String instrumentsURL;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__page);

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "user has been inactive for 1 hour, logging out", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                finishAffinity();
//                startActivity(intent);
            }
        };
        startHandler();

        settings_lv = findViewById(R.id.settings_lv);

        db = new DatabaseHelper(getApplicationContext());

        SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
        String serverURL = getResources().getString(R.string.server_name);
        final String server_url = sharedpreferences.getString("server_url", serverURL);

        usersURL = server_url + "readUsers.php";
        plantsURL = server_url + "readPlants.php";
        systemsURL = server_url + "readSystems.php";
        instrumentsURL = server_url + "readInstruments.php";

        settings_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    if (isInternetAvailable()) {
//                    Toast.makeText(Settings_Page.this,"Enter New Server Url",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Page.this);
                        builder.setView(null).setMessage(null);
                        builder.setTitle("Enter new ip address: ");
                        final EditText input_server_url = new EditText(Settings_Page.this);
//                    input_server_url.setText("http://");
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
                                    Toast.makeText(Settings_Page.this, "Please Enter a valid url", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("server_url", "http://" + input_server_url.getText().toString() + "/ke_app_api/");
                                    editor.apply();
                                    Toast.makeText(Settings_Page.this, "App needs a restart after server change", Toast.LENGTH_SHORT).show();
                                    Log.i("New Server Url", input_server_url.getText().toString());
//                                    getApplicationContext().deleteDatabase("pvs_ke");
                                    finishAffinity();
                                }
                            }
                        });
                        builder.setView(input_server_url);
                        builder.create().show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    }
                } else if (position == 1) {

                    if (isInternetAvailable()) {
                        dialog = ProgressDialog.show(Settings_Page.this, "Loading", "Please wait....", true);

                        db.rebuildDB(db.getWritableDatabase());

                        startService(new Intent(Settings_Page.this, SyncDbService.class));

                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    }
//
//                }
//            }
//        });
                } else if (position == 2) {

                    startActivity(new Intent(Settings_Page.this,Help_Page.class));
                }
            }
        });
    }

    Handler handler;
    Runnable r;

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }

    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    public void startHandler() {
        handler.postDelayed(r, 3600 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandler();
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onDestroy() {
        stopHandler();
        try {
            dialog.dismiss();
        }
        catch (NullPointerException e) {
            Log.i("settings page","no dialog box");
        }
        super.onDestroy();
    }




}