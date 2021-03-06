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
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.model.User;
import com.authentik.utils.CompleteDbService;
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
    ProgressDialog progDialogue;
    Intent serviceIntent;

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Page.this);
                    builder.setView(null).setMessage(null);
                    builder.setTitle("Change Server ");

                    String curr_ip = getSharedPreferences("ServerData", Context.MODE_PRIVATE).getString("server_url", "-");
                    if (curr_ip != null) {
                        int last_index = 0;
                        for (int i = 7; i < curr_ip.length(); i++) {
                            if (curr_ip.charAt(i) == '/') {
                                last_index = i;
                                break;
                            }
                        }
                        builder.setMessage("Current IP: " + curr_ip.substring(7, last_index));
                    } else {
                        builder.setMessage("Current IP: N/A");
                    }
                    final EditText input_server_url = new EditText(Settings_Page.this);
                    input_server_url.setHint("Enter new ip address");
                    final TextView curr_server_url = new TextView(Settings_Page.this);
                    curr_server_url.setText(getSharedPreferences("ServerData", Context.MODE_PRIVATE).getString("server_url", "-"));
                    input_server_url.setSelection(input_server_url.getText().length());

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isInternetAvailable()) {
                                if (input_server_url.getText().length() == 0) {
                                    Toast.makeText(Settings_Page.this, "Please Enter a valid url", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences sharedpreferences2 = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = sharedpreferences2.edit();
                                    editor2.putBoolean("isLoggedIn", false);
                                    editor2.putString("Username", "-");
                                    editor2.putString("shift_id", "-");
                                    editor2.apply();

                                    SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("server_url", "http://" + input_server_url.getText().toString() + "/ke_app_api/");
                                    editor.apply();

                                    db.rebuildDB(db.getWritableDatabase());

                                    Toast.makeText(Settings_Page.this, "Restarting App", Toast.LENGTH_SHORT).show();
                                    Log.i("New Server Url", input_server_url.getText().toString());
                                    finishAffinity();
                                    startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setView(input_server_url);
                    builder.create().show();

                } else if (position == 1) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Page.this);

                    builder.setTitle("Confirmation");
                    builder.setMessage("Sync Data with Server?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isInternetAvailable()) {
                                progDialogue = ProgressDialog.show(Settings_Page.this, "Loading", "Please wait....", true);
                                db.rebuildDB(db.getWritableDatabase());
                                //Start sync service
                                serviceIntent = new Intent(getApplicationContext(), CompleteDbService.class);
                                startService(serviceIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.create().show();

                } else if (position == 2) {

                    startActivity(new Intent(Settings_Page.this, Help_Page.class));
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
            progDialogue.dismiss();
            stopService(serviceIntent);
        } catch (NullPointerException e) {
            Log.i("settings page", "no dialog box");
        }
        super.onDestroy();
    }


    public void goBack(View view) {
        finish();
    }
}