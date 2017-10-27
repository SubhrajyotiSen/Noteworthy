package com.subhrajyoti.babai.noteworthy.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.subhrajyoti.babai.noteworthy.Adapters.RecyclerAdapter;
import com.subhrajyoti.babai.noteworthy.Models.Note;
import com.subhrajyoti.babai.noteworthy.Presenters.MainPresenter;
import com.subhrajyoti.babai.noteworthy.R;
import com.subhrajyoti.babai.noteworthy.Utils.Dialogs;
import com.subhrajyoti.babai.noteworthy.Utils.RecyclerTouchListener;
import com.subhrajyoti.babai.noteworthy.Utils.RecyclerViewEmptySupport;
import com.subhrajyoti.babai.noteworthy.Utils.SwipeableListener;
import com.subhrajyoti.babai.noteworthy.Utils.VerticalSpaceItemDecoration;
import com.subhrajyoti.babai.noteworthy.Views.MainView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, MainView {

    public static boolean firstView;
    //Declare views and bind views
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.recyclerView)
    RecyclerViewEmptySupport recyclerView;
    @BindView(R.id.first)
    RelativeLayout first;
    @BindView(R.id.second)
    LinearLayout second;
    @BindView(R.id.coordinator)
    View coordinatorView;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.title_text)
    EditText titleText;
    @BindView(R.id.desc_text)
    EditText descText;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private ArrayList<Note> filteredModelList;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<Note> notes;
    private MainPresenter mainPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainPresenter = new MainPresenter(this);

        //get runtime permissions
        mainPresenter.getPermissions();

        //Mandatory ButterKnife bind
        ButterKnife.bind(this);

        //initially the first view is visible
        firstView = true;

        //animation for FAB
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
         fab.startAnimation(animation);

        //listener for FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstView) //check with which view is visible
                    //start arc animation of FAB
                    mainPresenter.ArcTime();
                else
                    //execute for normal FAB click
                    mainPresenter.fabPressed();
            }
        });

        //make second view invisible
        second.setVisibility(View.INVISIBLE);

        //add navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        //initialize and set LinearLayout properties
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        //initialize notes ArrayList
        try{
            notes = new ArrayList<>();
            filteredModelList = new ArrayList<>();

            //fetch the notes from database
            mainPresenter.getNotes();
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
                                //remove all swiped items
                                for (final int position : reverseSortedPositions)
                                    mainPresenter.deleteNote(notes.get(position), position);
                                recyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                //remove all swiped items
                                for (final int position : reverseSortedPositions)
                                    mainPresenter.deleteNote(notes.get(position), position);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        });
        recyclerView.setEmptyView(findViewById(R.id.emptyTextView));
        recyclerView.addOnItemTouchListener(swipeTouchListener);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mainPresenter.startDetailActivity(position, filteredModelList.get(position).getTitle(), filteredModelList.get(position).getDesc(), recyclerView.getChildAt(position));
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //if drawer is open, close it when back button is pressed
            drawer.closeDrawer(GravityCompat.START);
        } else if (!firstView) {
            mainPresenter.fabPressed();
        } else if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }
        else {
            //default back press behavior
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {

            //online backup option
            case R.id.online:
                Dialogs.comingSoon(this);
                break;

            //local backup option
            case R.id.offline:
                Dialogs.offline(this);
                break;

            //about option
            case R.id.about:
                Dialogs.about(this);
                break;

            //trash option
            case R.id.trash:
                startActivity(new Intent(MainActivity.this,TrashActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return false;
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
            mainPresenter.filter(notes, query);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //update the edited note
        if (resultCode==RESULT_OK)
        {
            int index= data.getIntExtra("position",-1);
            notes.get(index).setTitle(data.getStringExtra("title"));
            notes.get(index).setDesc(data.getStringExtra("desc"));
            notes.get(index).setDate(DateFormat.getDateInstance().format(new Date()));
            mainPresenter.updateNoteInDB(notes.get(index));
            recyclerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //close db connection
        mainPresenter.closeDB();
    }


    @Override
    public void showEmptyTitleError(int redId) {
        Toast.makeText(getApplicationContext(), R.string.empty_note_message, Toast.LENGTH_LONG).show();
    }


    @Override
    public Context getContext() {
        return MainActivity.this;
    }

    @Override
    public void showNotes(List<Note> noteList) {
        for (int i = 0; i < noteList.size(); i++) {
            notes.add(noteList.get(i));
            filteredModelList.add(noteList.get(i));
        }
    }

    @Override
    public void showAddedNote(Note note) {
        mainPresenter.addNoteToDB(note);
        notes.add(note);
        filteredModelList.add(note);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
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
    public FrameLayout getFrame() {
        return frameLayout;
    }

    @Override
    public FloatingActionButton getFAB() {
        return fab;
    }

    @Override
    public void resetEditTexts() {
        titleText.setText("");
        descText.setText("");
    }

    @Override
    public String getNoteTitle() {
        return titleText.getText().toString();
    }

    @Override
    public String getNoteDescription() {
        return descText.getText().toString();
    }

    @Override
    public View getFocus() {
        return getCurrentFocus();
    }

    @Override
    public RelativeLayout getFirstLayout() {
        return first;
    }

    @Override
    public LinearLayout getSecondLayout() {
        return second;
    }

    @Override
    public SearchView getSearchView() {
        return searchView;
    }

    @Override
    public void showRestoredNotes() {
        notes.clear();
        mainPresenter.getNotes();
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSnackBar(final Note note, final int position) {
        Snackbar.make(coordinatorView, "'" + note.getTitle() + getString(R.string.moved_trash), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void updateNotesAfterDeletion(Note note) {
        notes.remove(note);
        recyclerAdapter.notifyDataSetChanged();
    }


}
