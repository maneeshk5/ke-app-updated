package com.authentik.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.authentik.ke.MainActivity;
import com.authentik.ke.R;
import com.authentik.model.APIResponse;
import com.authentik.model.Answer;
import com.authentik.network.APIService;
import com.authentik.network.ApiUtils;
import com.authentik.utils.Constant;
import com.authentik.utils.DatabaseHandler;
import com.authentik.utils.DialogBox;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Service extends IntentService {

    DatabaseHandler dbHandler;

    public Service() {
        super("Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dbHandler = DatabaseHandler.getDbHandlerInstance(getApplicationContext());
        try {
            dbHandler.updateAnswerStatus(0, 1);
            String token = intent.getStringExtra("token");
            List<Answer> ansList = dbHandler.getAllAnswerNotSent(true);

            intent.setAction(MainActivity.FILTER_ACTION_KEY);

            if (ansList.size() > 0) {

                sendAnswerToServer(ansList, token, intent);

            } else {
//            Toast.makeText(context, "Already Synced", Toast.LENGTH_SHORT).show();
//            sync_button.setEnabled(true);
                intent.putExtra("type", "synced");
                intent.putExtra("message", "Already Synced");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
        catch (Exception e) {
            Log.e("Service: 72", e.getMessage());
            intent.putExtra("type", "error");
            intent.putExtra("message", e.getMessage());

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    void sendAnswerToServer(List<Answer> ansList, String token, final Intent intent){

        List<MultipartBody.Part> parts = new ArrayList<>();
//        List<HashMap<String, RequestBody>> body = new ArrayList<>();
//        List<HashMap<String, String>> body = new ArrayList<>();

        List<String> body = new ArrayList<>();

        Gson gs = new Gson();

        boolean isImageFile = false;

        for(Answer a : ansList) {

            if(!a.getImage_fileName().equals("")) {
                isImageFile = true;
                File file = new File(Constant.IMAGE_FILE_PATH + "/" + a.getImage_fileName());

                // Create a request body with file and image media type
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);

                // Create MultipartBody.Part using file request-body,file name and part name
                parts.add(MultipartBody.Part.createFormData("file", file.getName(), fileReqBody));
            }

            HashMap<String, String> map = new HashMap<>();
            map.put("appQuesId", String.valueOf(a.getId()));
            map.put("reading", a.getReading());
            map.put("fileName", a.getImage_fileName());
            map.put("quesId", String.valueOf(a.getQues_id()));
            map.put("userId", String.valueOf(a.getUser_id()));
            map.put("recordedTime", a.getRecorded_time());

            body.add(gs.toJson(map));

        }

        if (!isImageFile) {
            Drawable d = getDrawable(R.drawable.image_placeholder);
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            // Create a request body with file and image media type
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), bitmapdata);

            // Create MultipartBody.Part using file request-body,file name and part name
            parts.add(MultipartBody.Part.createFormData("file", "noImg.png", fileReqBody));
        }


        final Context context = this;
        Map<String, String> headers = new HashMap<>();
        headers.put("x-auth-token",token);

        APIService mAPIService = ApiUtils.getAPIService();
        mAPIService.sendAnswers(headers, parts, body).enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if(response.isSuccessful()) {
                    Log.i("API CALLS", "post submitted to API." + response.body().toString());
                    Log.i("API CALLS", "post submitted to API." + response.code());
                    APIResponse res = response.body();

                    ObjectMapper oMapper = new ObjectMapper();
                    Map<String, Object> map = oMapper.convertValue(res.getData(), Map.class);

                    if(response.code() == 200) {
                        updateSentAnswersStatus(map, intent);
//                        sync_button.setEnabled(true);
                    }
                    else {
//                        AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Status : " + response.code(),
//                                res.getMessage() );
//                        dialog.show();
//                        sync_button.setEnabled(true);

                        intent.putExtra("type", "error");
                        intent.putExtra("message", "Status Code: " + response.code() + " " +res.getMessage());

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }

                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.e("API CALLS", "Unable to submit post to API. " + t);

//                AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Error", "Login API : " + t);
//                dialog.show();
//                sync_button.setEnabled(true);


                intent.putExtra("type", "error");
                intent.putExtra("message", t);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });


    }

    void updateSentAnswersStatus(Map<String, Object> map, Intent intent) {
        List<String> savedIds = (List<String>) map.get("savedIds");
        List<String> failedIds = (List<String>) map.get("failedIds");

        for(int i=0; i < savedIds.size(); i++){
            int id = Integer.parseInt(savedIds.get(i));
            dbHandler.updateAnswerStatus(2,1, id);
            dbHandler.deleteSentAnswerRecord(id);
        }

        for(int i=0; i < failedIds.size(); i++){
            int id = Integer.parseInt(failedIds.get(i));
            dbHandler.updateAnswerStatus(0,1,id);
        }


        intent.putExtra("type", "success");
        intent.putExtra("message", "Sync Completed");

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        Toast.makeText(this,"Sync Completed",Toast.LENGTH_SHORT).show();
    }
}
