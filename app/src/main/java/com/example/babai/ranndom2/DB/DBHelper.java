package com.example.babai.ranndom2.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //Table Name
    public static final String TABLE_NAME = "notes";
    //Column Names
    public static final String COL_EMP_ID = "_id";
    public static final String COL_NOTE_TITLE = "_title";
    public static final String COL_NOTE_DESC = "_description";
    public static final String COL_NOTE_DATE = "_date";
    static final String[] columns = new String[]{DBHelper.COL_EMP_ID,
            DBHelper.COL_NOTE_TITLE, DBHelper.COL_NOTE_DESC,
            DBHelper.COL_NOTE_DATE};
    //Database Information
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // creation SQLite statement
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "(" + COL_EMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTE_TITLE + " TEXT NOT NULL, " + COL_NOTE_DESC + " TEXT," + COL_NOTE_DATE + " TEXT NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("DB Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        System.out.println("Table Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        System.out.println("DB Updated");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}