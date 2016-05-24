package com.example.babai.ranndom2.Activities;

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
import com.example.babai.ranndom2.Adapters.RecyclerAdapter;
import com.example.babai.ranndom2.DB.DBController;
import com.example.babai.ranndom2.DB.DBTrashController;
import com.example.babai.ranndom2.Listeners.RecyclerTouchListener;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnQueryTextListener {

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
    LinearLayoutManager linearLayoutManager;
    @Bind(R.id.frameLayout)
    FrameLayout frameLayout;
    DBController dbController;
    DBTrashController dbTrashController;
    SearchView searchView;
    ArrayList<Note> filteredModelList;
    RecyclerAdapter recyclerAdapter;
    ArrayList<Note> notes;
    private boolean firstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPermissions();

        ButterKnife.bind(this);

        firstView = true;
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
        dbTrashController = new DBTrashController(this);


        second.setVisibility(View.INVISIBLE);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        try{
            notes = new ArrayList<>();
            filteredModelList = new ArrayList<>();
            getNotes();
        }
        catch(Exception e) {
            notes = new ArrayList<>();
            filteredModelList = new ArrayList<>();
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
        searchView.onActionViewCollapsed();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(second, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(200);
            SupportAnimator animator_reverse = animator.reverse();

            if (firstView) {

                second.setVisibility(View.VISIBLE);
                first.setVisibility(View.INVISIBLE);
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
                        first.setVisibility(View.VISIBLE);
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
                first.setVisibility(View.INVISIBLE);
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
                        first.setVisibility(View.VISIBLE);
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
        switch (id) {
            case R.id.online:
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
                break;
            case R.id.offline:
                new MaterialDialog.Builder(this)
                        .title("Backup/Restore")
                        .content("Backup your notes locally so that you can retrieve later")
                        .positiveText("Backup")
                        .negativeText("Restore")
                        .neutralText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                boolean result = SaveDB.save();
                                Toast.makeText(MainActivity.this, result ? "Backup successful" : "Backup unsuccessful", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                boolean result = RestoreDB.importDB();
                                Toast.makeText(MainActivity.this, result ? "Restore successful" : "Restore unsuccessful", Toast.LENGTH_SHORT).show();
                                if (result) {
                                    notes.clear();
                                    getNotes();
                                    recyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.about:
                new MaterialDialog.Builder(this)
                        .title("NoteWorthy v1.0.2")
                        .content(Html.fromHtml("<p>Check out the project on <a href=\"https://github.com/SubhrajyotiSen/Noteworthy\">GitHub</a></p>"))
                        .titleGravity(GravityEnum.CENTER)
                        .contentGravity(GravityEnum.CENTER)
                        .icon(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null))
                        .show();

                break;
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
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!searchView.isIconified())
        filter(notes,query);
        return true;
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
                        notes.add(position, note);
                        filteredModelList.add(position, note);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }).show();
        if (isDeleted[0]) {
            dbController.deleteNote(note);
            dbTrashController.addNote(note);
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
        filteredModelList.add(note);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(notes);
        recyclerView.setAdapter(recyclerAdapter);
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
            filteredModelList.add(notes2.get(i));
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
            notes.get(index).settitle(data.getStringExtra("title"));
            notes.get(index).setDesc(data.getStringExtra("desc"));
            notes.get(index).setDate(DateFormat.getDateInstance().format(new Date()));
            dbController.updateNote(notes.get(index));
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void filter(List<Note> models, String query) {
        query = query.toLowerCase();
        filteredModelList.clear();
        for (Note model : models) {
            final String text = model.gettitle().concat(" ").concat(model.getDesc()).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(filteredModelList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}