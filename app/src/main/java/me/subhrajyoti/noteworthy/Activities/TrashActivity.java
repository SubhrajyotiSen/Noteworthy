package me.subhrajyoti.noteworthy.Activities;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.subhrajyoti.noteworthy.Adapters.RecyclerAdapter;
import me.subhrajyoti.noteworthy.Models.Note;
import me.subhrajyoti.noteworthy.Presenters.TrashPresenter;
import me.subhrajyoti.noteworthy.R;
import me.subhrajyoti.noteworthy.Utils.RecyclerTouchListener;
import me.subhrajyoti.noteworthy.Utils.SwipeableListener;
import me.subhrajyoti.noteworthy.Utils.VerticalSpaceItemDecoration;
import me.subhrajyoti.noteworthy.Views.TrashView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class TrashActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, TrashView {

    //declarations
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coordinator)
    View coordinatorView;
    private ArrayList<Note> notes;
    private ArrayList<Note> filteredModelList;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private TrashPresenter trashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        ButterKnife.bind(this);

        //set up toolbar
        toolbar.setTitle("Trash");
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize database helper
        trashPresenter = new TrashPresenter(this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        try{
            notes = new ArrayList<>();
            filteredModelList = new ArrayList<>();

            //get notes
            trashPresenter.getNotes();
        }
        catch(Exception e) {
            notes = new ArrayList<>();
            filteredModelList = new ArrayList<>();
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //listener for swipe on RecyclerView items
        SwipeableListener swipeTouchListener =
                new SwipeableListener(recyclerView,
                        new SwipeableListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions)
                                    trashPresenter.deleteNote(notes.get(position));
                                recyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions)
                                    trashPresenter.deleteNote(notes.get(position));
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                trashPresenter.startDetailActivity(position, filteredModelList.get(position).getTitle(), filteredModelList.get(position).getDesc(), recyclerView.getChildAt(position));

            }

            @Override
            public void onLongClick(View view, int position) {
                trashPresenter.restoreNote(notes.get(position));
                recyclerAdapter.notifyDataSetChanged();

            }
        }));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        //clear focus  from the SearchView after searching done
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!searchView.isIconified())
            trashPresenter.filter(notes, query);
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //close database connections
        trashPresenter.closeDB();
    }


    @Override
    public Context getContext() {
        return TrashActivity.this;
    }

    @Override
    public void showNotes(List<Note> noteList) {
        for (int i = 0; i < noteList.size(); i++) {
            notes.add(noteList.get(i));
            filteredModelList.add(noteList.get(i));
        }

    }

    @Override
    public void showFilteredNotes(ArrayList<Note> arrayList) {
        //reinitialize RecyclerView with search results
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        filteredModelList.clear();
        filteredModelList.addAll(arrayList);

    }

    @Override
    public void showSnackBarDelete(Note note) {
        Snackbar.make(coordinatorView, "'" + note.getTitle() + getString(R.string.delete_permanent), Snackbar.LENGTH_LONG)
                .show();

    }

    @Override
    public void showSnackBarRestore(Note note) {
        Snackbar.make(coordinatorView, "'" + note.getTitle() + getString(R.string.restore_trash), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void updateNotesAfterChanges(Note note) {
        notes.remove(note);
        recyclerAdapter.notifyDataSetChanged();
    }
}