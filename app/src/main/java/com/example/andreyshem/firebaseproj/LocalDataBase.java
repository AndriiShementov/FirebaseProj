package com.example.andreyshem.firebaseproj;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by andreyshem on 18.12.2016.
 */

public class LocalDataBase extends SQLiteOpenHelper {
    public static final String DB_NAME = "savedURI";
    private static final int DB_VERSION = 1;

    public LocalDataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IMG_SAVER (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME_CATEGORY TEXT NOT NULL, "
                + "NAME_IMG TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // UpdateMyDB (db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS IMG_SAVER");
        onCreate(db);
    }

}
