package com.example.babai.ranndom2.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.babai.ranndom2.Adapters.RecyclerAdapter;
import com.example.babai.ranndom2.DB.DBController;
import com.example.babai.ranndom2.Listeners.RecyclerTouchListener;
import com.example.babai.ranndom2.Listeners.SwipeableListener;
import com.example.babai.ranndom2.Models.Note;
import com.example.babai.ranndom2.R;
import com.example.babai.ranndom2.RestoreDB;
import com.example.babai.ranndom2.SaveDB;
import com.example.babai.ranndom2.SimpleListener;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Note> notes;
    FloatingActionButton fab;
    DrawerLayout drawer;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    FrameLayout first;
    FrameLayout second;
    View coordinatorView;
    LinearLayoutManager linearLayoutManager;
    FrameLayout frameLayout;
    private boolean firstView;
    DBController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPermissions();

        firstView = true;
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        fab.startAnimation(animation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstView)
                    ArcTime();
                else
                    fabPressed();
            }
        });
        dbController = new DBController(this);


        first = (FrameLayout) findViewById(R.id.first);
        second = (FrameLayout) findViewById(R.id.second);
        coordinatorView = findViewById(R.id.coordinator);
        second.setVisibility(View.INVISIBLE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

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
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", notes.get(position).gettitle());
                intent.putExtra("desc", notes.get(position).getDesc());
                intent.putExtra("position",position);
                String transitionName = getString(R.string.transition_name);
                View cardView;
                cardView = recyclerView.getChildAt(position);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                                cardView,   // The view which starts the transition
                                transitionName    // The transitionName of the view we’re transitioning to
                        );
                ActivityCompat.startActivityForResult(MainActivity.this, intent,1, options.toBundle());

            }

            @Override
            public void onLongClick(View view, int position) {

                shareIt(position);

            }
        }));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!firstView) {
            fabPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void fabPressed() {

        final EditText editText1 = (EditText) findViewById(R.id.title_text);
        final EditText editText2 = (EditText) findViewById(R.id.desc_text);
        assert editText1 != null;
        final String s1 = editText1.getText().toString();
        assert editText2 != null;
        final String s2 = editText2.getText().toString();
        final int cx2 =  fab.getLeft();
        final int cx = (frameLayout.getLeft()+frameLayout.getRight())/2;
        final int cy2 = fab.getTop();
        final int cy = (frameLayout.getTop()+frameLayout.getBottom())/2;
        final int radius = Math.max(second.getWidth(), second.getHeight());
        fab.setX(cx2);
        fab.setY(cy2);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);
            SupportAnimator animator_reverse = animator.reverse();

            if (firstView) {

                second.setVisibility(View.VISIBLE);
                animator.start();
                fab.setImageResource(R.drawable.ic_done_white_24dp);
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
                /*if(editText1.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT);
                }*/

                firstView = false;
                //first.setVisibility(View.INVISIBLE);
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {

                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        second.setVisibility(View.INVISIBLE);
                        hideSoftKeyboard();
                        if (!s1.trim().equals("")) {

                            editText1.setText("");
                            editText2.setText("");
                            addNote(s1,s2);

                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter a valid note", Toast.LENGTH_LONG).show();
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

        } else {
            if (firstView) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                second.setVisibility(View.VISIBLE);
                anim.start();
                /*if(editText1.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT);
                }*/

                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
                fab.setImageResource(R.drawable.ic_done_white_24dp);

                firstView = false;
            } else {

                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                anim.setInterpolator(new ReverseInterpolator());
                anim.addListener(new android.animation.AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                                /*super.onAnimationEnd(animation);*/
                        second.setVisibility(View.INVISIBLE);
                        hideSoftKeyboard();
                        if (!s1.trim().equals("")) {
                            editText1.setText("");
                            editText2.setText("");
                            addNote(s1,s2);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter a valid note", Toast.LENGTH_LONG).show();
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

        if (id == R.id.action_backup) {
            boolean result = SaveDB.save();
            Toast.makeText(MainActivity.this, result ? "Backup successful":"Backup unsuccessful", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_restore) {
            boolean result = RestoreDB.importDB();
            Toast.makeText(MainActivity.this, result ? "Restore successful":"Restore unsuccessful", Toast.LENGTH_SHORT).show();
            if (result) {
                notes.clear();
                getNotes();
                recyclerAdapter.notifyDataSetChanged();
            }
        }
        else if (id == R.id.about){
            new MaterialDialog.Builder(this)
                    .title("NoteWorthy")
                    .content("Version: 1.0\n" +
                            "Developer: Subhrajyoti Sen\n"+
                            "Source: https://github.com/SubhrajyotiSen/Noteworthy")
                    .positiveText("Kbye")
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
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
            dbController.deleteNote(note);
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
        dbController.addNote(note);
        notes.add(note);
        recyclerAdapter.notifyDataSetChanged();

    }


    private void getPermissions(){
        ArrayList<String> permissions = new ArrayList<>();

        int hasPermissionStorage = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasPermissionAudio = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);

        if( hasPermissionAudio != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.RECORD_AUDIO );
        }

        if( hasPermissionStorage != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }

        if( !permissions.isEmpty() ) {
            ActivityCompat.requestPermissions(this,
                    permissions.toArray( new String[permissions.size()] ), 1 );
        }
    }

    private void getNotes(){
        List<Note> notes2 = dbController.getAllNotes();
        for (int i = 0; i < notes2.size(); i++) {
            notes.add(notes2.get(i));
        }

    }


    public void ArcTime(){
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
            dbController.deleteNote(note);
            notes.remove(index);
            note = new Note(data.getStringExtra("title"),data.getStringExtra("desc"));
            Date date = new Date();
            String Date= DateFormat.getDateInstance().format(date);
            note.setDate(Date);
            notes.add(index,note);
            dbController.addNote(note);
            recyclerAdapter.notifyDataSetChanged();
        }
    }


}