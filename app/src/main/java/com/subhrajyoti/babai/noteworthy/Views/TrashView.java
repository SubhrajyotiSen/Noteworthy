package com.subhrajyoti.babai.noteworthy.Views;


import android.content.Context;

import com.subhrajyoti.babai.noteworthy.Models.Note;

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
