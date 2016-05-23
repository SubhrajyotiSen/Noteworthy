package com.example.babai.ranndom2.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.babai.ranndom2.Adapters.RecyclerAdapter;
import com.example.babai.ranndom2.DB.DBController;
import com.example.babai.ranndom2.DB.DBTrashController;
import com.example.babai.ranndom2.Listeners.RecyclerTouchListener;
import com.example.babai.ranndom2.Listeners.RecyclerTouchListener2;
import com.example.babai.ranndom2.Listeners.SimpleListener;
import com.example.babai.ranndom2.Listeners.SwipeableListener;
import com.example.babai.ranndom2.Models.Note;
import com.example.babai.ranndom2.R;
import com.example.babai.ranndom2.RestoreDB;
import com.example.babai.ranndom2.SaveDB;
import com.example.babai.ranndom2.Utils.ReverseInterpolator;
import com.example.babai.ranndom2.Utils.VerticalSpaceItemDecoration;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

public class TrashActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayList<Note> notes;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    View coordinatorView;
    LinearLayoutManager linearLayoutManager;
    DBController dbController;
    DBTrashController dbTrashController;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trash");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
            getNotes();
        }
        catch(Exception e) {
            notes = new ArrayList<>();
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        SwipeableListener swipeTouchListener =
                new SwipeableListener(recyclerView,
                        new SwipeableListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {

                                    removeOnSwipe(position);
                                }
                                recyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    removeOnSwipe(position);
                                }
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(40));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener2(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent intent = new Intent(TrashActivity.this, DetailsActivity.class);
                intent.putExtra("title", notes.get(position).gettitle());
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
        getMenuInflater().inflate(R.menu.main, menu);
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private void removeOnSwipe(final int position) {
        final Note note = notes.get(position);
        final boolean[] isDeleted = new boolean[1];
        isDeleted[0] = true;
        notes.remove(position);
        recyclerAdapter.notifyDataSetChanged();
        Snackbar.make(coordinatorView, "'" + note.gettitle() + "' was removed", Snackbar.LENGTH_LONG)
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

    private void shareIt(int pos) {
        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.setType("text/plain");
        sharing.putExtra(Intent.EXTRA_TEXT, notes
                .get(pos).gettitle() + "\n\n" + notes.get(pos).getDesc());
        startActivity(Intent.createChooser(sharing, "Share via"));
    }

    private void addNote(String s1, String s2)  {
        Date date = new Date();
        String Date= DateFormat.getDateInstance().format(date);
        Note note = new Note(s1, s2);
        note.setDate(Date);
        dbTrashController.addNote(note);
        notes.add(note);
        recyclerAdapter.notifyDataSetChanged();

    }



    private void getNotes(){
        List<Note> notes2 = dbTrashController.getAllNotes();
        for (int i = 0; i < notes2.size(); i++) {
            notes.add(notes2.get(i));
        }

    }




    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK)
        {
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

    private void filter(List<Note> models, String query) {
        query = query.toLowerCase();

        final ArrayList<Note> filteredModelList = new ArrayList<>();
        for (Note model : models) {
            final String text = model.gettitle().concat(" ").concat(model.getDesc()).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(TrashActivity.this));
        recyclerAdapter = new RecyclerAdapter(filteredModelList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

}