package com.ventoray.shaut.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Nick on 2/6/2018.
 */

public class FileHelper {

    public static final String LOG_TAG = "FileHelper";
    public static final String USER_OBJECT_FILE =
            "com.ventoray.shaut.util.FileHelper.USER_OBJECT_FILE";


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


}
