package com.authentik.ke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.model.User;
//import com.authentik.utils.DatabaseHandler;
import com.authentik.utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Login extends AppCompatActivity {

//    private APIService mAPIService;
//    DatabaseHandler dbHandler;
    EditText editUserName;
    EditText editPassword;
    Button btnLogin;
    String usersURL = "http://jaguar.atksrv.net:8090/ke_api/readUsers.php";
    String plantsURL = "http://jaguar.atksrv.net:8090/ke_api/readPlants.php";
    String systemsURL = "http://jaguar.atksrv.net:8090/ke_api/readSystems.php";
    DatabaseHelper db;

    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create local database
        db = new DatabaseHelper(getApplicationContext());

        // sync server db to local db if internet is available
        if (isInternetAvailable()) {
            DatabaseSync();
        }
        else {
            Toast.makeText(getApplicationContext(),"Sever Sync not available due to no internet",Toast.LENGTH_SHORT).show();
        }

        editUserName = findViewById(R.id.userName_et);
        editPassword = findViewById(R.id.password_et);
        btnLogin = findViewById(R.id.login_btn);

        //login user
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    userLogin();
            }
        });
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void DatabaseSync() {


        class UserSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

                    if (arr.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < arr.length(); i++) {
                            User user = new User();
                            JSONObject obj = arr.getJSONObject(i);
                            user.setId(obj.getInt("user_id"));
                            user.setName(obj.getString("userName"));
                            user.setPassword(obj.getString("password"));
                            user.setIsActive(obj.getInt("isActive"));
                            user.setIsDeleted(obj.getInt("isDeleted"));

                            if (!db.checkUser(user.getName()) || user.getIsDeleted() == 0 || user.getIsActive() == 1)  {
                                db.addUser(user);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returing the response
                return requestHandler.sendReadRequest(usersURL);
            }
        }

        UserSync ul = new UserSync();
        ul.execute();
//        Toast.makeText(getApplicationContext(), "Database Synced", Toast.LENGTH_SHORT).show();

        class PlantSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

                    if (arr.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < arr.length(); i++) {
                            User user = new User();
                            Plant plant = new Plant();
                            JSONObject obj = arr.getJSONObject(i);
                            plant.setPlant_id(obj.getInt("plant_id"));
                            plant.setPlant_name(obj.getString("plant_name"));
                            plant.setIsActive(obj.getInt("isActive"));
                            plant.setReadingTimeId(obj.getInt("readingTimeId"));

                            if (!db.checkPlant(plant.getPlant_id())) {
                                db.addPlant(plant);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returing the response
                return requestHandler.sendReadRequest(plantsURL);
            }
        }

        PlantSync pl = new PlantSync();
        pl.execute();
//        Toast.makeText(getApplicationContext(), "Database Synced", Toast.LENGTH_SHORT).show();

        class SystemSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

                    if (arr.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < arr.length(); i++) {
                            System system = new System();
                            JSONObject obj = arr.getJSONObject(i);
                            system.setId(obj.getInt("system_id"));
                            system.setName(obj.getString("system_name"));
                            system.setIsActive(obj.getInt("isActive"));
                            system.setLogSheet(obj.getString("logSheet"));
                            system.setPlantId(obj.getInt("systemPlantId"));

                            if (!db.checkSystem(system.getId())) {
                                db.addSystem(system);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returing the response
                return requestHandler.sendReadRequest(systemsURL);
            }
        }

        SystemSync sl = new SystemSync();
        sl.execute();
//        Toast.makeText(getApplicationContext(), "Database Synced", Toast.LENGTH_SHORT).show();
    }


    private void userLogin() {

        final String username = editUserName.getText().toString();
        final String password = editPassword.getText().toString();
        SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if(db.checkUser(username,password)) {
            editor.putString("Username",username);
            editor.apply();
            finish();
            startActivity(new Intent(getApplicationContext(),Shift_Selection.class));
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }

    }
}