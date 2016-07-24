package com.subhrajyoti.babai.noteworthy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.subhrajyoti.babai.noteworthy.Adapters.RecyclerAdapter;
import com.subhrajyoti.babai.noteworthy.DB.DBController;
import com.subhrajyoti.babai.noteworthy.DB.DBTrashController;
import com.subhrajyoti.babai.noteworthy.Models.Note;
import com.subhrajyoti.babai.noteworthy.R;
import com.subhrajyoti.babai.noteworthy.Utils.RecyclerTouchListener2;
import com.subhrajyoti.babai.noteworthy.Utils.SwipeableListener;
import com.subhrajyoti.babai.noteworthy.Utils.VerticalSpaceItemDecoration;
import com.subhrajyoti.babai.noteworthy.Views.TrashView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrashActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, TrashView {

    //declarations
    private ArrayList<Note> notes;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private View coordinatorView;
    private LinearLayoutManager linearLayoutManager;
    private DBController dbController;
    private DBTrashController dbTrashController;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        //set up toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trash");
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize database helpers
        dbTrashController = new DBTrashController(this);
        dbController = new DBController(this);

        coordinatorView = findViewById(R.id.coordinator);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        try{
            notes = new ArrayList<>();
            //get notes
            getNotes();
        }
        catch(Exception e) {
            notes = new ArrayList<>();
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
                                    removeOnSwipe(position);
                                recyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions)
                                    removeOnSwipe(position);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener2(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent intent = new Intent(TrashActivity.this, DetailsActivity.class);
                intent.putExtra("title", notes.get(position).getTitle());
                intent.putExtra("desc", notes.get(position).getDesc());
                intent.putExtra("position",position);
                String transitionName = getString(R.string.transition_name);
                View cardView;
                cardView = recyclerView.getChildAt(position);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(TrashActivity.this,
                                cardView,
                                transitionName
                        );
                ActivityCompat.startActivityForResult(TrashActivity.this, intent,1, options.toBundle());

            }

            @Override
            public void onLongClick(View view, int position) {

                dbController.addNote(notes.get(position));
                dbTrashController.deleteNote(notes.get(position));
                notes.remove(position);
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
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filter(notes,query);
        return true;
    }

    private void removeOnSwipe(final int position) {
        final Note note = notes.get(position);
        final boolean[] isDeleted = new boolean[1];
        isDeleted[0] = true;
        notes.remove(position);
        recyclerAdapter.notifyDataSetChanged();
        Snackbar.make(coordinatorView, "'" + note.getTitle() + "' was removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isDeleted[0] = false;
                        notes.add(position,note);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }).show();
        if (isDeleted[0]){
            dbTrashController.deleteNote(note);
        }
    }

    //function to fetch all notes from database
    private void getNotes(){
        List<Note> notes2 = dbTrashController.getAllNotes();
        for (int i = 0; i < notes2.size(); i++) {
            notes.add(notes2.get(i));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            int index= data.getIntExtra("position",-1);
            Note note = notes.get(index);
            dbTrashController.deleteNote(note);
            notes.remove(index);
            note = new Note(data.getStringExtra("title"),data.getStringExtra("desc"));
            Date date = new Date();
            String Date= DateFormat.getDateInstance().format(date);
            note.setDate(Date);
            notes.add(index,note);
            dbTrashController.addNote(note);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    //function for filtering the RecyclerView with search query
    private void filter(List<Note> models, String query) {
        //convert query text to lower case for easier searching
        query = query.toLowerCase();
        final ArrayList<Note> filteredModelList = new ArrayList<>();
        //iterate over all notes and check if they contain the query string
        for (Note model : models) {
            final String text = model.getTitle().concat(" ").concat(model.getDesc()).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        //reinitialize RecyclerView with search results
        recyclerView.setLayoutManager(new LinearLayoutManager(TrashActivity.this));
        recyclerAdapter = new RecyclerAdapter(filteredModelList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //close database connections
        dbController.close();
        dbTrashController.close();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}