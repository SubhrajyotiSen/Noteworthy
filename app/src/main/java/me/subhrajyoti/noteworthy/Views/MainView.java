package me.subhrajyoti.noteworthy.Views;

import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import me.subhrajyoti.noteworthy.Models.Note;

import java.util.ArrayList;
import java.util.List;

public interface MainView {

    void showEmptyTitleError(int redId);

    Context getContext();

    void showNotes(List<Note> noteList);

    void showAddedNote(Note note);

    void showFilteredNotes(ArrayList<Note> arrayList);

    FrameLayout getFrame();

    FloatingActionButton getFAB();

    void resetEditTexts();

    String getNoteTitle();

    String getNoteDescription();

    View getFocus();

    RelativeLayout getFirstLayout();

    LinearLayout getSecondLayout();

    androidx.appcompat.widget.SearchView getSearchView();

    void showRestoredNotes();

    void showSnackBar(Note note, int position);

    void updateNotesAfterDeletion(Note note);




}
