package com.authentik.ke;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.authentik.model.APIResponse;
import com.authentik.network.APIService;
import com.authentik.network.ApiUtils;
import com.authentik.utils.DatabaseHandler;
import com.authentik.utils.DialogBox;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private APIService mAPIService;
    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHandler = DatabaseHandler.getDbHandlerInstance(getApplicationContext());
        mAPIService = ApiUtils.getAPIService();
    }

    public void sendAPIRequest(HashMap<String, Object> data) {
        final Context context = this;

        mAPIService.login(data).enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if(response.isSuccessful()) {
                    Log.i("API CALLS", "post submitted to API." + response.body().toString());
                    Log.i("API CALLS", "post submitted to API." + response.code());
                    APIResponse res = response.body();

                    if(response.code() == 200) {
                        ObjectMapper oMapper = new ObjectMapper();
                        Map<String, Object> map = oMapper.convertValue(res.getData(), Map.class);

                        try {
                            dbHandler.addUser((int) Math.round(Double.parseDouble(map.get("id").toString())), map.get("name").toString(), map.get("token").toString());

                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }
                        catch (Exception e) {
                            Log.e("adding User info db:" , " " + e);
                            Toast.makeText(context,"Error in Saving User info in Local DB",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Status : " + response.code(),
                                res.getMessage() );
                        dialog.show();
                    }

                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.e("API CALLS", "Unable to submit post to API. " + t);
                AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Error", "Login API : " + t);
                dialog.show();
            }
        });
    }

    public void Login(View view) {

        HashMap<String, Object> data = new HashMap();

        EditText userName =  (EditText) findViewById(R.id.usr_name);
        EditText password =  (EditText) findViewById(R.id.password);

        data.put("userName", userName.getText().toString());
        data.put("password", password.getText().toString());

        sendAPIRequest(data);
    }
}
