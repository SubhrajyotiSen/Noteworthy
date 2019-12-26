package me.subhrajyoti.noteworthy.Views;


import android.content.Context;

import me.subhrajyoti.noteworthy.Models.Note;

import java.util.ArrayList;
import java.util.List;

public interface TrashView {

    Context getContext();

    void showNotes(List<Note> noteList);

    void showFilteredNotes(ArrayList<Note> arrayList);

    void showSnackBarDelete(Note note);

    void showSnackBarRestore(Note note);

    void updateNotesAfterChanges(Note note);


}
