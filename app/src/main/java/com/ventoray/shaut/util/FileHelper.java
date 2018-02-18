package com.ventoray.shaut.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
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

public class FileHelper {

    public interface OnBitmapRetrievedListener {
        void onBitmapRetrieved(Bitmap bitmap);
    }

    public static final String LOG_TAG = "FileHelper";
    public static final String USER_OBJECT_FILE =
            "com.ventoray.shaut.util.FileHelper.USER_OBJECT_FILE";
    public static final String PAGE_OBJECT_LIST =
            "com.ventoray.shaut.util.FileHelper.PAGE_OBJECT_LIST";


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
            e.printStackTrace();
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
