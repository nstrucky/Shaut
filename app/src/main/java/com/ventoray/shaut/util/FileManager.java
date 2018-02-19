package com.ventoray.shaut.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nick on 2/6/2018.
 */

public class FileManager {

    public interface OnBitmapRetrievedListener {
        void onBitmapRetrieved(Bitmap bitmap);
    }

    public static final String LOG_TAG = "FileManager";
    public static final String USER_OBJECT_FILE =
            "com.ventoray.shaut.util.FileManager.USER_OBJECT_FILE";
    public static final String PAGE_OBJECT_LIST =
            "com.ventoray.shaut.util.FileManager.PAGE_OBJECT_LIST";

    public static final String CITY_PHOTO_FILE =
            "CITY_PHOTO_FILE";


    public static Bitmap readBitmapFromFile(Context context, String fileName) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");
        Bitmap bitmap = null;


        bitmap = BitmapFactory.decodeFile(mediaStorageDir.getPath() + File.separator + fileName);
        return bitmap;
    }


    /**
     * This came from a stackoverflow post/answer by GoCrazy
     *
     * Create a File for saving an image or video */
    public static File getOutputMediaFile(Context context, String fileName){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }


    public static boolean deleteBitmapFile(Context context, String fileName) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file.delete();
    }



    public static void writeBitmapToFile(Context context, Bitmap bitmap, String fileName) {
        FileOutputStream fileOutputStream = null;
        File pictureFile = getOutputMediaFile(context, fileName);
        if (pictureFile == null) {
            Log.d(LOG_TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            fileOutputStream = new FileOutputStream(pictureFile);
            if (bitmap == null) {
                Log.e(LOG_TAG, "Could not write null bitmap object to file");
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeObjectToFile(Context context, Object o, String fileName) {

        FileOutputStream outputStream;
        ObjectOutputStream objectOutputStream;

        try {

            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }


    public static Object readObjectFromFile(Context context, String fileName) {
        Object object;
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        try {
            fileInputStream = context.openFileInput(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            return null;
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            return null;
        }

        return object;
    }


    public static Bitmap getBitmapFromURL(String resource) {
        Bitmap bitmap = null;
        Log.d(LOG_TAG, "Bitmap URL: " + resource);
        try {
            URL url = new URL(resource);
            HttpURLConnection httpURLConnection =
                    (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            Log.d(LOG_TAG, httpURLConnection.getResponseMessage());

            InputStream inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            if (e == null) return null;
//            Log.d(LOG_TAG, e.getMessage());
        }

        return bitmap;
    }




}
