    package com.authentik.ke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.model.User;
//import com.authentik.utils.DatabaseHandler;
import com.authentik.utils.BCrypt;
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
//    String usersURL = "http://jaguar.atksrv.net:8090/ke_api/readUsers.php";
//    String plantsURL = "http://jaguar.atksrv.net:8090/ke_api/readPlants.php";
//    String systemsURL = "http://jaguar.atksrv.net:8090/ke_api/readSystems.php";
    DatabaseHelper db;

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(getApplicationContext());

        if (isInternetAvailable()) {

            final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 8000);
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


    private void userLogin() {

        String username = editUserName.getText().toString();
        String password = editPassword.getText().toString();
        SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Log.i("User Name",username);

        if(db.checkUser(username)) {
            User user = db.getPassword(username);
            boolean bcrypt = BCrypt.checkpw(password,user.getPassword());
//            Log.i("Entered Password",BCrypt.hashpw(password,BCrypt.gensalt(8)));
//            Log.i("Db Password",user.getPassword());

            if (bcrypt) {
                editor.putString("Username",username);
                editor.putBoolean("isLoggedIn",true);
                editor.apply();
                finish();
                startActivity(new Intent(getApplicationContext(),Shift_Selection.class));
            }
            else {
                Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid username", Toast.LENGTH_SHORT).show();
        }

    }
}