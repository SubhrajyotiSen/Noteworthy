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

    //Database Information
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    //Table Name
    static final String TABLE_NAME_TRASH = "trash";
    //Column Names
    static final String COL_ID_TRASH = "_id";
    static final String COL_NOTE_TITLE_TRASH = "_title";
    static final String COL_NOTE_DESC_TRASH = "_description";
    static final String COL_NOTE_DATE_TRASH = "_date";

    // creation SQLite statement
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTE_TITLE + " TEXT NOT NULL, " + COL_NOTE_DESC + " TEXT," + COL_NOTE_DATE + " TEXT NOT NULL);";

    private static final String DATABASE_CREATE_TRASH = "CREATE TABLE " + TABLE_NAME_TRASH
            + "(" + COL_ID_TRASH + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTE_TITLE_TRASH + " TEXT NOT NULL, " + COL_NOTE_DESC_TRASH + " TEXT," + COL_NOTE_DATE_TRASH + " TEXT NOT NULL);";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("DB Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_TRASH);
        System.out.println("Table Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRASH);
        onCreate(db);
        System.out.println("DB Updated");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}