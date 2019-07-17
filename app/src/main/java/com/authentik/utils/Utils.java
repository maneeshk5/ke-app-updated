package com.authentik.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String saveIamgeInStorage(byte[] photo, int userId) throws IOException {
        File dir = new File(Constant.IMAGE_FILE_PATH);
        if(!dir.exists())
            dir.mkdirs();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy-hh-ss");


        String fileName = "ke-app" + df.format(c) + "-" + userId + ".png";

        File file = new File(dir, fileName);
        FileOutputStream fOut = new FileOutputStream(file);
        fOut.write(photo);
//        photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

        return fileName;
    }

    public static String imageInStorage(int userId) throws IOException {
        File dir = new File(Constant.IMAGE_FILE_PATH);
        if(!dir.exists())
            dir.mkdirs();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");


        String fileName = "ke-app" + df.format(c) + "-" + userId + ".jpg";
//        FileOutputStream fOut = new FileOutputStream(file);
//        fOut.write(photo);
////        photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//        fOut.flush();
//        fOut.close();

        return fileName;
    }

    public static byte[] convertBitmapToByteArr(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static Bitmap convertByteArrToBitmap(byte[] byteArray){
        return new BitmapFactory().decodeByteArray(byteArray, 0/* starting index*/, byteArray.length/*no of byte to read*/);
    }

    public static Bitmap convertFileToBitmap(String filePath){
        return BitmapFactory.decodeFile(filePath);
    }
}
