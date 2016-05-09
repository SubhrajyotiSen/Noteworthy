package com.example.babai.ranndom2.Activities;

import android.Manifest;
import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.example.babai.ranndom2.Adapters.RecyclerAdapter;
import com.example.babai.ranndom2.Listeners.RecyclerTouchListener;
import com.example.babai.ranndom2.Listeners.SwipeableListener;
import com.example.babai.ranndom2.Models.Note;
import com.example.babai.ranndom2.R;
import com.example.babai.ranndom2.RestoreDB;
import com.example.babai.ranndom2.SaveDB;
import com.example.babai.ranndom2.Utils.ReverseInterpolator;
import com.example.babai.ranndom2.Utils.VerticalSpaceItemDecoration;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Note> notes;
    FloatingActionButton fab;
    DrawerLayout drawer;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    LinearLayout first;
    LinearLayout second;
    View coordinatorView;
    LinearLayoutManager linearLayoutManager;
    FrameLayout frameLayout;
    private boolean x;
    Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPermissions();

        x = true;
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        fab.startAnimation(animation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabPressed();
            }
        });


        first = (LinearLayout) findViewById(R.id.first);
        second = (LinearLayout) findViewById(R.id.second);
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
        notes = new ArrayList<>();
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
        getNotes();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

                //Toast.makeText(getApplicationContext(), position + " is pressed!", Toast.LENGTH_SHORT).show();
                //((FrameLayout) view).setForeground(new ColorDrawable(getResources().getColor(R.color.overlay)));
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
        } else if (!x) {
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

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public void fabPressed() {

        final EditText editText1 = (EditText) findViewById(R.id.title_text);
        final EditText editText2 = (EditText) findViewById(R.id.desc_text);
        assert editText1 != null;
        final String s1 = editText1.getText().toString();
        assert editText2 != null;
        final String s2 = editText2.getText().toString();
        final int cx2 =  fab.getLeft();  //(fab.getLeft() + fab.getRight()) / 2;
        final int cx = (frameLayout.getLeft()+frameLayout.getRight())/2;
        final int cy2 = fab.getTop(); //(fab.getTop() + fab.getBottom())/2;
        final int cy = (frameLayout.getTop()+frameLayout.getBottom())/2;
        final int radius = Math.max(second.getWidth(), second.getHeight());
        ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(fab, cx,
                cy, 90, Side.LEFT)
                .setDuration(300);
        arcAnimator.addListener(new SimpleListener(){
            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                //fab.setVisibility(View.INVISIBLE);
                fab.setX(cx2);
                fab.setY(cy2);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(200);
                    SupportAnimator animator_reverse = animator.reverse();

                    if (x) {

                        second.setVisibility(View.VISIBLE);
                        animator.start();
                        fab.setImageResource(R.drawable.ic_done_white_24dp);
                /*if(editText1.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT);
                }*/

                        x = false;
                        //first.setVisibility(View.INVISIBLE);
                    } else {
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {

                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {
                                second.setVisibility(View.INVISIBLE);
                                hideKeyboard();
                                if (!s1.trim().equals("")) {

                                    editText1.setText("");
                                    editText2.setText("");
                                    addNote(s1,s2);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Please enter a valid note", Toast.LENGTH_LONG).show();
                                }

                                fab.setImageResource(R.mipmap.ic_mode_edit_white_24dp);
                                x = true;
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
                    if (x) {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                        second.setVisibility(View.VISIBLE);
                        anim.start();
                /*if(editText1.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT);
                }*/
                        fab.setImageResource(R.drawable.ic_done_white_24dp);

                        x = false;
                    } else {

                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
                        anim.setInterpolator(new ReverseInterpolator());
                        anim.addListener(new android.animation.AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                second.setVisibility(View.INVISIBLE);
                                hideKeyboard();
                                if (!s1.trim().equals("")) {
                                    editText1.setText("");
                                    editText2.setText("");
                                    addNote(s1,s2);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please enter a valid note", Toast.LENGTH_LONG).show();
                                }

                                fab.setImageResource(R.mipmap.ic_mode_edit_white_24dp);
                                x = true;
                            }
                        });
                        anim.start();


                    }
                }

            }
        });
        arcAnimator.start();


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_backup) {
            boolean result = SaveDB.save();
            Toast.makeText(MainActivity.this, result ? "Backup successful":"Restore unsuccessful", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_restore) {
            boolean result = RestoreDB.importDB();
            Toast.makeText(MainActivity.this, result ? "Restore successful":"Restore unsuccessful", Toast.LENGTH_SHORT).show();
            if (result) {
                notes.clear();
                getNotes();
                Intent mStartActivity = new Intent(this, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
                /*ArrayList<Note> notes2 = (ArrayList<Note>) Note.listAll(Note.class);
                for (int i = 0; i < notes2.size(); i++)
                    notes.add(notes2.get(i));
                recyclerAdapter.notifyDataSetChanged();*/
            }
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
            realm.beginTransaction();
            note.deleteFromRealm();
            realm.commitTransaction();

        }
    }

    private void shareIt(int pos) {
        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.setType("text/plain");
        sharing.putExtra(Intent.EXTRA_TEXT, notes
                .get(pos).gettitle() + "\n" + notes.get(pos).getDesc());
        startActivity(Intent.createChooser(sharing, "Share via"));
    }

    private void addNote(String s1, String s2)  {
        Note note = new Note(s1, s2);
        realm.beginTransaction();
        realm.copyToRealm(note);
        realm.commitTransaction();
        notes.add(note);
        recyclerAdapter.notifyDataSetChanged();

    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        realm.refresh();
        realm.setAutoRefresh(true);
        RealmResults<Note> realmResults = realm.where(Note.class).findAll();
        Log.d("Size", String.valueOf(realmResults.size()));
        notes.clear();
        for (int i = 0; i < realmResults.size(); i++) {
            notes.add(realmResults.get(i));
            Log.d("fav add", realmResults.get(i).gettitle());
        }
        recyclerAdapter.notifyDataSetChanged();
        Log.d("Array Size",String.valueOf(notes.size()));
    }

    private static class SimpleListener implements SupportAnimator.AnimatorListener, ObjectAnimator.AnimatorListener{

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {

        }

        @Override
        public void onAnimationCancel() {

        }

        @Override
        public void onAnimationRepeat() {

        }


        @Override
        public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

        }

        @Override
        public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {

        }

        @Override
        public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

        }

        @Override
        public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

        }
    }
}





