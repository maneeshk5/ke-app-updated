package com.authentik.ke;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.APIResponse;
import com.authentik.model.Answer;
import com.authentik.model.AssetsQues;
import com.authentik.network.APIService;
import com.authentik.network.ApiUtils;
import com.authentik.service.Service;
import com.authentik.utils.Constant;
import com.authentik.utils.DatabaseHandler;
import com.authentik.utils.DialogBox;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
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

public class MainActivity extends AppCompatActivity {
    private final String TAG = "IntentApiSample";
    private final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    /**
     * Honeywell DataCollection Intent API
     * Claim scanner
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    /**
     * Honeywell DataCollection Intent API
     * Release scanner claim
     * Permissions:
     * "com.honeywell.decode.permission.DECODE"
     */
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the scanner to claim. If scanner is not available or if extra is not used,
     * DataCollection will choose an available scanner.
     * Values : String
     * "dcs.scanner.imager" : Uses the internal scanner
     * "dcs.scanner.ring" : Uses the external ring scanner
     */
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    /**
     * Honeywell DataCollection Intent API
     * Optional. Sets the profile to use. If profile is not available or if extra is not used,
     * the scanner will use factory default properties (not "DEFAULT" profile properties).
     * Values : String
     */
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    /**
     * Honeywell DataCollection Intent API
     * Optional. Overrides the profile properties (non-persistent) until the next scanner claim.
     * Values : Bundle
     */
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private TextView textView;
    private BroadcastReceiver barcodeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BARCODE_DATA.equals(intent.getAction())) {
/*
These extras are available:
"version" (int) = Data Intent Api version
"aimId" (String) = The AIM Identifier
            "charset" (String) = The charset used to convert "dataBytes" to "data" string
"codeId" (String) = The Honeywell Symbology Identifier
"data" (String) = The barcode data as a string
"dataBytes" (byte[]) = The barcode data as a byte array
"timestamp" (String) = The barcode timestamp
*/
                int version = intent.getIntExtra("version", 1);
                if (version >= 1) {
                    String aimId = intent.getStringExtra("aimId");
                    String charset = intent.getStringExtra("charset");
                    String codeId = intent.getStringExtra("codeId");
                    String data = intent.getStringExtra("data");
                    byte[] dataBytes = intent.getByteArrayExtra("dataBytes");
                    String dataBytesStr = bytesToHexString(dataBytes);
                    String timestamp = intent.getStringExtra("timestamp");
                    String text = String.format(
                            "Data:%s\n" +
                                    "Charset:%s\n" +
                                    "Bytes:%s\n" +
                                    "AimId:%s\n" +
                                    "CodeId:%s\n" +
                                    "Timestamp:%s\n",
                            data, charset, dataBytesStr, aimId, codeId, timestamp);

                    String text2 = String.format(
                            "Data:%s\n",
                            data);

                    Log.i("Scan Result ", text2);
//                    setText(text2);
                    goQuestionsActivity(data);
                }
            }
        }
    };

    private APIService mAPIService;
    private HashMap<String,String> userDetail;
    private DatabaseHandler dbHandler;
    Button sync_button, new_record_button;
    Context context_main;
    MyReceiver myReceiver;
    ProgressBar progressBar;
    public static final String FILTER_ACTION_KEY = "send_data_service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        sync_button = (Button) findViewById(R.id.sync_record);
        new_record_button = (Button) findViewById(R.id.new_record);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        context_main = this;

        mAPIService = ApiUtils.getAPIService();
        dbHandler = DatabaseHandler.getDbHandlerInstance(getApplicationContext());

        try {
            userDetail = dbHandler.getUser();
        }
        catch (Exception e) {
            Log.e("Main Activity: 165", e.getMessage());
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getQuestionsList();
//            }
//        }).start();

        getQuestionsList();

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();
    }


    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", true);
        properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER)
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager")
                .putExtra(EXTRA_PROFILE, "MyProfile1")
                .putExtra(EXTRA_PROPERTIES, properties)
        );
    }
    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER));
    }
    private void setText(final String text) {
        if (textView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(text);
                }
            });
        }
    }
    private String bytesToHexString(byte[] arr) {
        String s = "[]";
        if (arr != null) {
            s = "[";
            for (int i = 0; i < arr.length; i++) {
                s += "0x" + Integer.toHexString(arr[i]) + ", ";
            }
            s = s.substring(0, s.length() - 2) + "]";
        }
        return s;
    }

    public void newRecord(View view) {
        AlertDialog dialog = DialogBox.dismissButtonDialog(this,"Alert", "Please Scan the Barcode");
        dialog.show();

//        Intent i = new Intent(this,AssetsForm.class);
////        Intent i = new Intent(this, Test.class);
//        i.putExtra("barcodeStr", "000001");
//        startActivity(i);
    }

    void goQuestionsActivity(String barcodeStr) {
        Intent i =  new Intent(this, AssetsForm.class);
        i.putExtra("barcodeStr", barcodeStr);
        startActivity(i);
    }

    public void getQuestionsList() {
        final Context context = this;
        Map <String, String> headers = new HashMap<>();
        headers.put("x-auth-token",userDetail.get("token"));

        mAPIService.getQuestionsList(headers).enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {

                if(response.isSuccessful()) {
                    Log.i("API CALLS", "post submitted to API." + response.body().toString());
                    Log.i("API CALLS", "post submitted to API." + response.code());
                    APIResponse res = response.body();

                    if(response.code() == 200) {
                        ObjectMapper oMapper = new ObjectMapper();
                        oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        try {
                            Gson gs = new Gson();
                            final List<AssetsQues> assets_ques = oMapper.readValue(gs.toJson(res.getData()), new TypeReference<List<AssetsQues>>(){});

                            try {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dbHandler.addAssetsQues(assets_ques);
                                    }
                                }).start();

                            }
                            catch (Exception e) {
                                Log.e("Error in saving Ques", e.toString());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
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
                String msg = t.getMessage();
                if (msg.contains("Unable to resolve host")) {
                    Log.e("Get Questions", "No Internet");
                    Toast.makeText(context,"No Internet", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Error", "Login API : " + t);
                    dialog.show();
                }
            }
        });
    }

    public void sync(View view) {

        sync_button.setEnabled(false);
        new_record_button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = new Intent(MainActivity.this, Service.class);
        intent.putExtra("token", userDetail.get("token"));
        startService(intent);

//        if (!sync_button.isEnabled()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    dbHandler.updateAnswerStatus(0, 1);
//                    List<Answer> ansList = dbHandler.getAllAnswerNotSent(true);
//
//                    if (ansList.size() > 0) {
//                        sendAnswerToServer(ansList);
//                    } else {
//                        Toast.makeText(context, "Already Synced", Toast.LENGTH_SHORT).show();
//                        sync_button.setEnabled(true);
//                    }
//                }
//            });
//        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String type = intent.getStringExtra("type");
            if(type.equals("error")) {
                AlertDialog dialog = DialogBox.dismissButtonDialog(context_main, "Error", "Login API : " + message);
                dialog.show();
            }
            else {
                  Toast.makeText(context_main,message,Toast.LENGTH_SHORT).show();
            }
            sync_button.setEnabled(true);
            new_record_button.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setReceiver() {
        System.out.println("Set Receiver CAlls");
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FILTER_ACTION_KEY);

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        System.out.println("On Start Calls");
        setReceiver();
        super.onStart();
    }

    @Override
    protected void onStop() {
//        if(myReceiver != null) {
//            unregisterReceiver(myReceiver);
//            System.out.println("UnRegister Receiver");
//        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        System.out.println("UnRegister Receiver");
        System.out.println("On Stop calls");
        super.onStop();
    }

//    void sendAnswerToServer(List<Answer> ansList){
//
//        List<MultipartBody.Part> parts = new ArrayList<>();
////        List<HashMap<String, RequestBody>> body = new ArrayList<>();
////        List<HashMap<String, String>> body = new ArrayList<>();
//
//        List<String> body = new ArrayList<>();
//
//        Gson gs = new Gson();
//
//        boolean isImageFile = false;
//
//        for(Answer a : ansList) {
//
//            if(!a.getImage_fileName().equals("")) {
//                isImageFile = true;
//                File file = new File(Constant.IMAGE_FILE_PATH + "/" + a.getImage_fileName());
//
//                // Create a request body with file and image media type
//                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
//
//                // Create MultipartBody.Part using file request-body,file name and part name
//                parts.add(MultipartBody.Part.createFormData("file", file.getName(), fileReqBody));
//            }
//
//            HashMap<String, String> map = new HashMap<>();
//            map.put("appQuesId", String.valueOf(a.getId()));
//            map.put("reading", a.getReading());
//            map.put("fileName", a.getImage_fileName());
//            map.put("quesId", String.valueOf(a.getQues_id()));
//            map.put("userId", String.valueOf(a.getUser_id()));
//            map.put("recordedTime", a.getRecorded_time());
//
//            body.add(gs.toJson(map));
//
//        }
//
//        if (!isImageFile) {
//            Drawable d = getDrawable(R.drawable.image_placeholder);
//            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bitmapdata = stream.toByteArray();
//
//            // Create a request body with file and image media type
//            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), bitmapdata);
//
//            // Create MultipartBody.Part using file request-body,file name and part name
//            parts.add(MultipartBody.Part.createFormData("file", "noImg.png", fileReqBody));
//        }
//
//
//        final Context context = this;
//        Map <String, String> headers = new HashMap<>();
//        headers.put("x-auth-token",userDetail.get("token"));
//
//        mAPIService.sendAnswers(headers, parts, body).enqueue(new Callback<APIResponse>() {
//            @Override
//            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
//
//                if(response.isSuccessful()) {
//                    Log.i("API CALLS", "post submitted to API." + response.body().toString());
//                    Log.i("API CALLS", "post submitted to API." + response.code());
//                    APIResponse res = response.body();
//
//                    ObjectMapper oMapper = new ObjectMapper();
//                    Map<String, Object> map = oMapper.convertValue(res.getData(), Map.class);
//
//                    if(response.code() == 200) {
//                        updateSentAnswersStatus(map);
//                        sync_button.setEnabled(true);
//                    }
//                    else {
//                        AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Status : " + response.code(),
//                                res.getMessage() );
//                        dialog.show();
//                        sync_button.setEnabled(true);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<APIResponse> call, Throwable t) {
//                Log.e("API CALLS", "Unable to submit post to API. " + t);
//                AlertDialog dialog = DialogBox.dismissButtonDialog(context, "Error", "Login API : " + t);
//                dialog.show();
//                sync_button.setEnabled(true);
//            }
//        });
//
//
//    }

//    void updateSentAnswersStatus(Map<String, Object> map) {
//        List<String> savedIds = (List<String>) map.get("savedIds");
//        List<String> failedIds = (List<String>) map.get("failedIds");
//
//        for(int i=0; i < savedIds.size(); i++){
//            int id = Integer.parseInt(savedIds.get(i));
//            dbHandler.updateAnswerStatus(2,1, id);
//            dbHandler.deleteSentAnswerRecord(id);
//        }
//
//        for(int i=0; i < failedIds.size(); i++){
//            int id = Integer.parseInt(failedIds.get(i));
//            dbHandler.updateAnswerStatus(0,1,id);
//        }
//
//        Toast.makeText(this,"Sync Completed",Toast.LENGTH_SHORT).show();
//    }

    public void logOut(View view) {
        try{
            dbHandler.deleteUser();
            finishAffinity();
            startActivity(new Intent(this,Login.class));
        }
        catch (Exception e) {
            Toast.makeText(this,"Error in Logout",Toast.LENGTH_SHORT).show();
        }

    }
}