package com.subhrajyoti.babai.noteworthy.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    //Table Name
    static final String TABLE_NAME = "notes";
    //Column Names
    static final String COL_ID = "_id";
    static final String COL_NOTE_TITLE = "_title";
    static final String COL_NOTE_DESC = "_description";
    static final String COL_NOTE_DATE = "_date";
    static final String[] columns = new String[]{DBHelper.COL_ID,
            DBHelper.COL_NOTE_TITLE, DBHelper.COL_NOTE_DESC,
            DBHelper.COL_NOTE_DATE};
    //Database Information
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // creation SQLite statement
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTE_TITLE + " TEXT NOT NULL, " + COL_NOTE_DESC + " TEXT," + COL_NOTE_DATE + " TEXT NOT NULL);";

    DBHelper(Context context) {
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