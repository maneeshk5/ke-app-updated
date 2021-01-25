package com.authentik.ke;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class reading_picture extends AppCompatActivity {

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    TextView currUser;
    TextView dateAndTime;
    ImageView readingImage;
    ImageView take_image_btn;
    SharedPreferences sharedPreferences;

    DatabaseHelper db;
    Reading reading;
    Instrument instrument;

    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_picture);

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
                Intent intent = new Intent(getApplicationContext(),Login.class);
                finishAffinity();
//                startActivity(intent);
            }
        };
        startHandler();

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        readingImage = findViewById(R.id.reading_image);
        take_image_btn = findViewById(R.id.take_img_btn);

        db = new DatabaseHelper(getApplicationContext());

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        currUser.setText(value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        reading = (Reading) getIntent().getSerializableExtra("reading_object");
        instrument = (Instrument) getIntent().getSerializableExtra("tag_instrument");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    REQUEST_PERMISSION);
        }


    }

    public void open_camera(View view) {
        openCameraIntent();
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, "com.authentik.ke.fileprovider", photoFile);

            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
//                readingImage.setImageURI(Uri.parse(imageFilePath));
                Uri photoUri = Uri.fromFile(new File(imageFilePath));
//                byte[] inputData = null;
//                try {
//                    InputStream iStream =  getContentResolver().openInputStream(photoUri);
//                    inputData = getBytes(iStream);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                try {
//                    Uri uri = data.getData();

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);

                    readingImage.setImageBitmap(bitmap);

                } catch (IOException e) {

                    e.printStackTrace();
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(reading_picture.this);
                builder.setTitle("Confirmation!");
                builder.setMessage("Do you want to save this picture?");

//                Bitmap thumbnail = BitmapFactory.decodeByteArray(inputData,0,inputData.length);
//                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                final byte[] finalInputData = inputData;

                ByteArrayOutputStream byteArrayOutputStreamObject ;

                byteArrayOutputStreamObject = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStreamObject);

                final byte[] finalInputData = byteArrayOutputStreamObject.toByteArray();

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        db.insertReadingImage(reading_id, finalInputData);
//                        imageInBase64Str = Base64.encodeBytes(imageInByte)
                        reading.setImage_path(finalInputData);
                        db.addReading(reading);

                        //next instrument with the barcode
                        List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(instrument.getBarcodeId());
                        if (instrumentList.size() == 1) {
                            Toast.makeText(getApplicationContext(),"Data Saved Successfully",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(reading_picture.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Data Saved Successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(reading_picture.this,Barcode_Instrument_List.class);
                            intent.putExtra("Instrument_list", (Serializable) instrumentList);
                            intent.putExtra("barcode_id", instrument.getBarcodeId());
                            startActivity(intent);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();

            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Image Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
//        Toast.makeText(reading_picture.this,imageFilePath,Toast.LENGTH_LONG).show();
        return image;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void cancelPic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(reading_picture.this);
        builder.setTitle("Exit");
        builder.setMessage("Save Reading without taking a picture?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.addReading(reading);
                List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(instrument.getBarcodeId());

                if (instrumentList.size() == 1) {
                    Toast.makeText(getApplicationContext(),"Data Saved Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(reading_picture.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Data Saved Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(reading_picture.this, Barcode_Instrument_List.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("Instrument_list", (Serializable) instrumentList);
                    intent.putExtra("barcode_id", instrument.getBarcodeId());
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
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
        handler.postDelayed(r, 3600*1000);
    }

    public void goBack(View view) {
        cancelPic();
    }

    @Override
    public void onBackPressed() {
        cancelPic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
    }

    public void settingsPage(View view) {
        startActivity(new Intent(reading_picture.this,Settings_Page.class));
    }

    public void log_Out(View view) {
        AlertDialog.Builder logout_dialogue_builder = new AlertDialog.Builder(reading_picture.this);
        logout_dialogue_builder.setTitle("Are you sure you want to Log Out and Exit?");
        logout_dialogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(),SplashScreen.class);
                finishAffinity();
                startActivity(intent);
            }
        });

        logout_dialogue_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.i("Status", "logout confirmed");
            }
        });
        logout_dialogue_builder.create().show();
    }

}