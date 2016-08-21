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

        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_TITLE, note.getTitle());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DESC, note.getDesc());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DATE, note.getDate());

        database.insert(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, null, values);
        database.close();
    }

    // Getting All notes
    public List<Note> getAllNotes() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        List<Note> noteList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setDesc(cursor.getString(2));
                note.setDate(cursor.getString(3));
                // Adding note to list
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return note list
        return noteList;
    }

    // Updating single note
    public int updateNote(Note note) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_TITLE, note.getTitle());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DESC, note.getDesc());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DATE, note.getDate());

        // updating row
        return db.update(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, values, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    // Deleting single note
    public void deleteNote(Note note) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        db.delete(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
     

    public void addNoteToTrash(Note note) {

        database = DBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_TITLE_TRASH, note.getTitle());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DESC_TRASH, note.getDesc());
        values.put(com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_NOTE_DATE_TRASH, note.getDate());

        database.insert(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME_TRASH, null, values);
        database.close();
    }


    // Getting All notes
    public List<Note> getAllNotesFromTrash() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        List<Note> noteList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME_TRASH;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setDesc(cursor.getString(2));
                note.setDate(cursor.getString(3));
                // Adding note to list
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return note list
        return noteList;
    }

    // Deleting single note
    public void deleteNoteFromTrash(Note note) {
        SQLiteDatabase db = DBHelper.getWritableDatabase();

        db.delete(com.subhrajyoti.babai.noteworthy.DB.DBHelper.TABLE_NAME_TRASH, com.subhrajyoti.babai.noteworthy.DB.DBHelper.COL_ID_TRASH + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }


}