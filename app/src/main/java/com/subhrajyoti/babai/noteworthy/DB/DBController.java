package com.subhrajyoti.babai.noteworthy.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.subhrajyoti.babai.noteworthy.Models.Note;

import java.util.ArrayList;
import java.util.List;

public class DBController {
    // Database fields
    private DBHelper DBHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBController(Context context) {
        DBHelper = new DBHelper(context);
    }

    public void close() {
        DBHelper.close();
    }

    public void addNote(Note note) {

        database = DBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_TITLE, note.gettitle());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DESC, note.getDesc());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DATE, note.getDate());

        database.insert(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, null, values);

        System.out.println("Record Added");
        database.close();
    }

    public Note getNote(int _id) {

        database = DBHelper.getReadableDatabase();

        Cursor cursor = database.query(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, com.subhrajyoti.babai.noteworthy.DB.DBHelper.columns, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID + " =?", new String[]{String.valueOf(_id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        assert cursor != null;
        Note note = new Note(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
        cursor.close();

        return note;
    }

    // Getting All Employees
    public List<Note> getAllNotes() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        List<Note> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.settitle(cursor.getString(1));
                note.setDesc(cursor.getString(2));
                note.setDate(cursor.getString(3));
                // Adding contact to list
                contactList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return contactList;
    }

    // Updating single employee
    public int updateNote(Note note) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_TITLE, note.gettitle());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DESC, note.getDesc());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DATE, note.getDate());

        // updating row
        return db.update(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, values, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    // Deleting single employee
    public void deleteNote(Note note) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        db.delete(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(note.getId())});

        System.out.println("Record Deleted");
        db.close();
    }

    // Deleting single employee
    public void deleteNote(int _id) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        db.delete(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(_id)});
        db.close();
    }
}