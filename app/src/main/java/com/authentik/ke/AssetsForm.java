package com.authentik.ke;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.Adapter.AssetsFormAdapter;
import com.authentik.model.Answer;
import com.authentik.model.AssetsQues;
import com.authentik.network.APIService;
import com.authentik.network.ApiUtils;
import com.authentik.utils.Constant;
import com.authentik.utils.DatabaseHandler;
import com.authentik.utils.DialogBox;
import com.authentik.utils.PermissionUtils;
import com.authentik.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssetsForm extends Activity {

    private APIService mAPIService;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int EXTERNAL_WRITE_PERMISSION_CODE = 101;
    private static final int EXTERNAL_READ_PERMISSION_CODE = 102;
    List<AssetsQues> ques;
    DatabaseHandler dbHandler;
    Activity self;
    private HashMap<Integer,String> ImagefileName = new HashMap<>();
    private HashMap<String,String> userDetail;

    HashMap<Integer,String> fileNameImg = new HashMap<>();
    HashMap<Integer,byte[]> img = new HashMap<>();
    public HashMap<Integer,String> readings = new HashMap<>();

    View captureBtn_view;
    LinearLayout topView;
    Button save, confirm,cancel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    File photoFile;
    String photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_form);
        self = this;

        mAPIService = ApiUtils.getAPIService();
        dbHandler = DatabaseHandler.getDbHandlerInstance(getApplicationContext());

        Intent intent = getIntent();
        try {
            ques = dbHandler.getAssetQues(intent.getStringExtra("barcodeStr"));

            if (!(ques.size() > 0)) {
                Toast.makeText(this, "Invalid Barcode", Toast.LENGTH_SHORT).show();
                finish();
            }


            userDetail = dbHandler.getUser();
        }
        catch (Exception e) {
            Log.e("AssetForm: 100", e.getMessage());
        }

//        setTitle("KKS Code: " + intent.getStringExtra("barcodeStr"));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_v);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        mAdapter = new AssetsFormAdapter(ques, this);
        recyclerView.setAdapter(mAdapter);

        save = (Button) findViewById(R.id.save_btn);
        confirm = (Button) findViewById(R.id.confirm_btn);
        cancel = (Button) findViewById(R.id.cancel_btn);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int a = ((LinearLayoutManager)recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();

                    if(a == ques.size()-1) {
                        confirm.setVisibility(View.VISIBLE);
                    }
                    else {
                        confirm.setVisibility(View.GONE);
                    }
                }
            }

        });
    }

    public void captureBtnListener() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if(!PermissionUtils.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                photoName = Utils.imageInStorage(Integer.parseInt(userDetail.get("id")));
                photoFile = new File(Constant.IMAGE_FILE_PATH,photoName);
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.authentik.ke.fileprovider",
                            photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoName = Utils.imageInStorage(Integer.parseInt(userDetail.get("id")));
                    photoFile = new File(Constant.IMAGE_FILE_PATH,photoName);

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.authentik.ke.fileprovider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
//        else  if (requestCode == EXTERNAL_WRITE_PERMISSION_CODE)
//        {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                Toast.makeText(this, "Write permission granted", Toast.LENGTH_LONG).show();
//
//                try{
////                    ImagefileName.put((int)captureBtn_view.getTag(),Utils.saveIamgeInStorage(img.get(captureBtn_view.getTag()), Integer.parseInt(userDetail.get("id"))));
//                    int pos = ((LinearLayoutManager)recyclerView.getLayoutManager())
//                            .findFirstVisibleItemPosition();
//                    fileNameImg.put(pos,Utils.saveIamgeInStorage(img.get(pos), Integer.parseInt(userDetail.get("id"))));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Error in saving image in storage", Toast.LENGTH_LONG).show();
//                }
//            }
//            else
//            {
//                Toast.makeText(this, "Write permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap i = Utils.convertFileToBitmap(String.valueOf(photoFile));

            Bitmap photo = (Bitmap) Bitmap.createScaledBitmap(i,300,300,false);

//            int imgView_id = (int) captureBtn_view.getTag();
//            img.put(imgView_id, photo);
            int pos = ((LinearLayoutManager)recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
            img.put(pos, Utils.convertBitmapToByteArr(photo));

            RecyclerView.ViewHolder view = recyclerView.findViewHolderForAdapterPosition(pos);

            ImageView imageView = (ImageView) view.itemView.findViewById(R.id.img_view);
            imageView.setImageBitmap(photo);
            imageView.setVisibility(View.VISIBLE);

            fileNameImg.put(pos,photoName);

//            if(PermissionUtils.shouldAskForPermission(self, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                PermissionUtils.requestPermissions(self,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_WRITE_PERMISSION_CODE);
//            }
//            else {
//                try {
////                    ImagefileName.put((int)captureBtn_view.getTag(),Utils.saveIamgeInStorage(img.get(captureBtn_view.getTag()), Integer.parseInt(userDetail.get("id"))));
//                    fileNameImg.put(pos,photoName);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Error in saving image in storage", Toast.LENGTH_LONG).show();
//                }
//            }
        }
    }


    public void save(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        List <Answer> ansList = new ArrayList<>();


        try {
            for (int i = 0; i < ques.size(); i++) {
                Answer a = new Answer();
                if (readings.get(i) == null) {
                    throw new Exception("Enter Reading First");
                }
                a.setReading(readings.get(i));
//                a.setReading("51.2");

                a.setImage_fileName(fileNameImg.containsKey(i) ? fileNameImg.get(i) : "");
                a.setQues_id(ques.get(i).getId());
                a.setUser_id(Integer.parseInt(userDetail.get("id")));

                ansList.add(a);
            }

            try {
                dbHandler.addAnswer(ansList);

                ansList.clear();
                ansList = dbHandler.getAllAnswerNotSent(false);

//            for(Answer aw : ansList) {
//                System.out.println(aw.toString());
//            }

                Toast.makeText(self, "Save Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Log.e("Error in saving answers", e.toString());
            }
        }
        catch (Exception e) {
            Log.e("Reading field empty ", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public LinearLayout createLayout(int quesId, String ques_text, String unit, int i) {

        // Root Linear
        LinearLayout rootLinear = new LinearLayout(self);
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootLinear.setLayoutParams(layoutParams);
        rootLinear.setOrientation(LinearLayout.VERTICAL);
        rootLinear.setPadding(20,10, 20,0);

        // Question textview
        TextView question_tv = new TextView(self);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10,20,0,20);
        question_tv.setTextSize(16);
        question_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        question_tv.setLetterSpacing((float) 0.1);
        int qNo = i+1;
        question_tv.setText(qNo + ": " + ques_text);
        question_tv.setLayoutParams(params);

        // Inner Linear
        LinearLayout innerLinear = new LinearLayout(self);
        LinearLayout.LayoutParams layoutParamsInner
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        innerLinear.setLayoutParams(layoutParamsInner);
        innerLinear.setOrientation(LinearLayout.HORIZONTAL);

        // Reading EditText
        EditText reading_et = new EditText(self);
        LinearLayout.LayoutParams layoutParamsEditText
                = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,(float)0.8);
        layoutParamsEditText.setMargins(15,0,0,0);
        reading_et.setHint("Please Enter Reading");
        reading_et.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        reading_et.setBackgroundColor(Color.WHITE);
        reading_et.setInputType(InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL);
        reading_et.setLetterSpacing((float)0.1);
        reading_et.setTextSize(16);
        reading_et.setPadding(10,15,10,15);
        reading_et.setTextColor(getResources().getColor(R.color.textColor));
        reading_et.setId(quesId);
//        reading_et.setShowSoftInputOnFocus(false);
        reading_et.setLayoutParams(layoutParamsEditText);

        // Question textview
        TextView unit_tv = new TextView(self);
        LinearLayout.LayoutParams layoutParamsUnit = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float)0.2);
        layoutParamsUnit.setMargins(10,0,0,0);
        layoutParamsUnit.gravity = Gravity.CENTER_VERTICAL;
        unit_tv.setTextSize(16);
        unit_tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        unit_tv.setLetterSpacing((float) 0.1);
        unit_tv.setText(unit);
        unit_tv.setLayoutParams(layoutParamsUnit);

        // Add EditText and TextView in Inner Linear Layout
        innerLinear.addView(reading_et);
        innerLinear.addView(unit_tv);

        // ImageView for Capture Image Preview
        ImageView img_view = new ImageView(self);
        LinearLayout.LayoutParams layoutParamsImg = new LinearLayout.LayoutParams(200, 150);
        layoutParamsImg.setMargins(0,25,0,15);
        layoutParamsImg.gravity = Gravity.CENTER_HORIZONTAL;
        img_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img_view.setImageResource(R.drawable.image_placeholder);
        img_view.setId(quesId+50);
        img_view.setLayoutParams(layoutParamsImg);

        // Capture Button
        Button capture_btn = new Button(self);
        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParamsButton.setMargins(0,10,0,50);
        capture_btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        capture_btn.setLetterSpacing((float)0.3);
        capture_btn.setPadding(12,12,12,12);
        capture_btn.setText("Capture Image");
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureBtnListener();
            }
        });
        capture_btn.setTextColor(Color.WHITE);
        capture_btn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        capture_btn.setTag(quesId+50);
        capture_btn.setLayoutParams(layoutParamsButton);

        View v = new View(self);
        LinearLayout.LayoutParams layoutParamsView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        v.setBackgroundColor(getResources().getColor(R.color.blueke));
        v.setLayoutParams(layoutParamsView);

        // Add textview, inner Linear Layout, imageview and button in root Linear Layout
        rootLinear.addView(question_tv);
        rootLinear.addView(innerLinear);
        rootLinear.addView(img_view);
        rootLinear.addView(capture_btn);

        if(ques.size() > 1) {
            rootLinear.addView(v);
        }

        return rootLinear;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("Key Code : ------ " + keyCode);
        View v = null;

        switch (keyCode) {
            case KeyEvent.KEYCODE_ALL_APPS:
                if(confirm.getVisibility() == View.VISIBLE) {
                    save(v);
                }
//                else if(save.getVisibility() == View.VISIBLE) {
//                    save(v);
//                }
                return true;
            case KeyEvent.KEYCODE_REFRESH:
                if(confirm.getVisibility() == View.VISIBLE) {
                    showConfirmationDialogBeforeExist();
                }
                else if(cancel.getVisibility() == View.VISIBLE) {
                    cancel(v);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                showConfirmationDialogBeforeExist();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void confirm(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        for(AssetsQues question : ques) {

            EditText reading_et = (EditText) findViewById(question.getId());
            reading_et.setFocusable(false);

            Button capture_Btn = (Button) topView.findViewWithTag(question.getId()+50);
            capture_Btn.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        }

    }

    public void cancel(View view) {

        for(AssetsQues question : ques) {

            EditText reading_et = (EditText) findViewById(question.getId());
            reading_et.setFocusable(true);
            reading_et.setFocusableInTouchMode(true);

            Button capture_Btn = (Button) topView.findViewWithTag(question.getId()+50);
            capture_Btn.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }
    }

    public void showConfirmationDialogBeforeExist() {
        AlertDialog dialog = DialogBox.bothButtonDialog(this,"Confirmation","You want to go back ?","Yes", "No");
        dialog.show();
    }
}
