package com.subhrajyoti.babai.noteworthy.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.subhrajyoti.babai.noteworthy.Adapters.RecyclerAdapter;
import com.subhrajyoti.babai.noteworthy.DB.DBController;
import com.subhrajyoti.babai.noteworthy.DB.DBTrashController;
import com.subhrajyoti.babai.noteworthy.Listeners.RecyclerTouchListener;
import com.subhrajyoti.babai.noteworthy.Listeners.SimpleListener;
import com.subhrajyoti.babai.noteworthy.Listeners.SwipeableListener;
import com.subhrajyoti.babai.noteworthy.Models.Note;
import com.subhrajyoti.babai.noteworthy.R;
import com.subhrajyoti.babai.noteworthy.RestoreDB;
import com.subhrajyoti.babai.noteworthy.SaveDB;
import com.subhrajyoti.babai.noteworthy.Utils.ReverseInterpolator;
import com.subhrajyoti.babai.noteworthy.Utils.VerticalSpaceItemDecoration;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnQueryTextListener {

    //Declare views and bind views
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.first)
    LinearLayout first;
    @Bind(R.id.second)
    LinearLayout second;
    @Bind(R.id.coordinator)
    View coordinatorView;
    @Bind(R.id.frameLayout)
    FrameLayout frameLayout;
    private LinearLayoutManager linearLayoutManager;
    private DBController dbController;
    private DBTrashController dbTrashController;
    private SearchView searchView;
    private ArrayList<Note> filteredModelList;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<Note> notes;
    private boolean firstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get runtime permissions
        getPermissions();

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
                    ArcTime();
                else
                    //execute for normal FAB click
                    fabPressed();
            }
        });

        //initialize database helpers
        dbController = new DBController(this);
        dbTrashController = new DBTrashController(this);

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
            getNotes();
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
                                    removeOnSwipe(position);
                                recyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                //remove all swiped items
                                for (final int position : reverseSortedPositions)
                                    removeOnSwipe(position);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(40));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //intent to start viewing and editing activity
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", filteredModelList.get(position).gettitle());
                intent.putExtra("desc", filteredModelList.get(position).getDesc());
                intent.putExtra("position",position);
                String transitionName = getString(R.string.transition_name);
                View cardView;
                cardView = recyclerView.getChildAt(position);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                                cardView,
                                transitionName
                        );
                ActivityCompat.startActivityForResult(MainActivity.this, intent,1, options.toBundle());

            }

            @Override
            public void onLongClick(View view, int position) {
                //share the note
                shareIt(position);
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
            fabPressed();
        } else {
            //default back press behavior
            super.onBackPressed();
        }
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

    //function for fab click
    private void fabPressed() {

        //retrieve note title and content
        final EditText editText1 = (EditText) findViewById(R.id.title_text);
        final EditText editText2 = (EditText) findViewById(R.id.desc_text);
        assert editText1 != null;
        final String s1 = editText1.getText().toString();
        assert editText2 != null;
        final String s2 = editText2.getText().toString();

        //get positions for animation
        final int cx2 =  fab.getLeft();
        final int cx = (frameLayout.getLeft()+frameLayout.getRight())/2;
        final int cy2 = fab.getTop();
        final int cy = (frameLayout.getTop()+frameLayout.getBottom())/2;
        final int radius = Math.max(second.getWidth(), second.getHeight());

        //final position of FAB after arc animation
        fab.setX(cx2);
        fab.setY(cy2);
        searchView.onActionViewCollapsed();

        //check is Android version is KitKat or below
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            //set up circular reveal animator
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);

            //animator for reverse reveal animation
            SupportAnimator animator_reverse = animator.reverse();

            //check if first view is visible
            if (firstView) {

                second.setVisibility(View.VISIBLE);
                first.setVisibility(View.INVISIBLE);
                animator.start();

                //change FAB appearance
                fab.setImageResource(R.drawable.ic_done_white_24dp);
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

                //denote that second view is visible now
                firstView = false;
            }
            //if second view is visible
            else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {

                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        second.setVisibility(View.INVISIBLE);
                        first.setVisibility(View.VISIBLE);
                        hideSoftKeyboard();
                        if (!s1.trim().equals("")) {

                            editText1.setText("");
                            editText2.setText("");
                            addNote(s1,s2);

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.empty_note_message, Toast.LENGTH_LONG).show();
                        }

                        fab.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentSecondary)));
                        firstView = true;
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }

                });
                animator_reverse.start();
            }

        }
        //if Android version is Lollipop and above
        else {
            if (firstView) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                second.setVisibility(View.VISIBLE);
                first.setVisibility(View.INVISIBLE);
                anim.start();
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
                fab.setImageResource(R.drawable.ic_done_white_24dp);

                firstView = false;
            } else {

                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                anim.setInterpolator(new ReverseInterpolator());
                anim.addListener(new android.animation.AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        second.setVisibility(View.INVISIBLE);
                        first.setVisibility(View.VISIBLE);
                        hideSoftKeyboard();
                        if (!s1.trim().equals("")) {
                            editText1.setText("");
                            editText2.setText("");
                            addNote(s1,s2);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.empty_note_message, Toast.LENGTH_LONG).show();
                        }

                        fab.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentSecondary)));

                        firstView = true;
                    }
                });
                anim.start();


            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {

            //online backup option
            case R.id.online:
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
                break;

            //local backup option
            case R.id.offline:
                new MaterialDialog.Builder(this)
                        .title(R.string.backup_title)
                        .content(R.string.backup_content)
                        .positiveText(R.string.backup_button)
                        .negativeText(R.string.restore)
                        .neutralText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                boolean result = SaveDB.save();
                                Toast.makeText(MainActivity.this, result ? getString(R.string.backup_success) : getString(R.string.backup_fail), Toast.LENGTH_SHORT).show();

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                boolean result = RestoreDB.importDB();
                                Toast.makeText(MainActivity.this, result ? getString(R.string.restore_success) : getString(R.string.restore_fail), Toast.LENGTH_SHORT).show();
                                if (result) {
                                    notes.clear();
                                    getNotes();
                                    recyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .show();
                break;

            //about option
            case R.id.about:
                new MaterialDialog.Builder(this)
                        .title("NoteWorthy v1.0.0")
                        .content( ((Build.VERSION.SDK_INT >= 24)) ? Html.fromHtml(getString(R.string.html_text),Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml("<p>Check out the project on <a href=\"https://github.com/SubhrajyotiSen/Noteworthy\">GitHub</a></p>"))
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .icon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null))
                        .show();

                break;

            //trash option
            case R.id.trash:
                startActivity(new Intent(MainActivity.this,TrashActivity.class));


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        filter(notes,query);
        return true;
    }

    //remove a note after being swiped
    private void removeOnSwipe(final int position) {
        final Note note = notes.get(position);
        final boolean[] isDeleted = new boolean[1];
        isDeleted[0] = true;
        notes.remove(position);
        recyclerAdapter.notifyDataSetChanged();
        //show snackbar
        Snackbar.make(coordinatorView, "'" + note.gettitle() + "' was removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isDeleted[0] = false;
                        notes.add(position,note);
                        filteredModelList.add(position,note);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }).show();
        //check if deletion was undo'd
        if (isDeleted[0]){
            dbController.deleteNote(note);
            dbTrashController.addNote(note);
        }
    }

    //function to share a note
    private void shareIt(int pos) {
        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.setType("text/plain");
        sharing.putExtra(Intent.EXTRA_TEXT, notes
                .get(pos).gettitle() + "\n\n" + notes.get(pos).getDesc());
        startActivity(Intent.createChooser(sharing, "Share via"));
    }

    //function to add a single note
    private void addNote(String s1, String s2)  {
        Date date = new Date();
        //get current system time
        String Date = DateFormat.getDateInstance().format(date);
        Note note = new Note(s1, s2);
        note.setDate(Date);
        //add note to databse
        dbController.addNote(note);
        notes.add(note);
        filteredModelList.add(note);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

    }

    //function to get permissions
    private void getPermissions(){
        ArrayList<String> permissions = new ArrayList<>();

        int hasPermissionStorage = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //check if WRITE permission has been granted
        if( hasPermissionStorage != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }

        if( !permissions.isEmpty() ) {
            ActivityCompat.requestPermissions(this,
                    permissions.toArray( new String[permissions.size()] ), 1 );
        }
    }

    //function to retrieve all notes
    private void getNotes(){
        List<Note> notes2 = dbController.getAllNotes();
        for (int i = 0; i < notes2.size(); i++) {
            notes.add(notes2.get(i));
            filteredModelList.add(notes2.get(i));
        }

    }

    //function to start arc movement of FAB
    private void ArcTime() {

        //get center of FAB
        final int cx = (frameLayout.getLeft()+frameLayout.getRight())/2;
        final int cy = (frameLayout.getTop()+frameLayout.getBottom())/2;

        ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(fab, cx,
                cy, 90, Side.LEFT)
                .setDuration(300);
        arcAnimator.addListener(new SimpleListener(){
            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                fabPressed();
            }
        });
        arcAnimator.start();
    }

    //hide keyboard to make animations clearer
    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //update the edited note
        if (resultCode==RESULT_OK)
        {
            int index= data.getIntExtra("position",-1);
            notes.get(index).settitle(data.getStringExtra("title"));
            notes.get(index).setDesc(data.getStringExtra("desc"));
            notes.get(index).setDate(DateFormat.getDateInstance().format(new Date()));
            dbController.updateNote(notes.get(index));
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    //filter notes list based on search query
    private void filter(List<Note> models, String query) {
        //convert query text to lower case for easier searching
        query = query.toLowerCase();
        filteredModelList.clear();
        //iterate over all notes and check if they contain the query string
        for (Note model : models) {
            final String text = model.gettitle().concat(" ").concat(model.getDesc()).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        //reinitialize RecyclerView with search results
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(filteredModelList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //close db connection
        dbController.close();
    }

    //interface for click listeners
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}