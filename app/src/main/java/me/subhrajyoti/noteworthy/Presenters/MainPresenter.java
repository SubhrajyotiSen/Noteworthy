package me.subhrajyoti.noteworthy.Presenters;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import me.subhrajyoti.noteworthy.Activities.DetailsActivity;
import me.subhrajyoti.noteworthy.DB.DBController;
import me.subhrajyoti.noteworthy.Models.Note;
import me.subhrajyoti.noteworthy.R;
import me.subhrajyoti.noteworthy.Utils.ReverseInterpolator;
import me.subhrajyoti.noteworthy.Utils.SimpleListener;
import me.subhrajyoti.noteworthy.Views.MainView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static me.subhrajyoti.noteworthy.Activities.MainActivity.firstView;

public class MainPresenter {

    private MainView mainView;
    private DBController dbController;
    private FrameLayout frameLayout;
    private FloatingActionButton fab;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        dbController = new DBController(mainView.getContext());
    }

    public void getPermissions() {
        ArrayList<String> permissions = new ArrayList<>();

        int hasPermissionStorage = ContextCompat.checkSelfPermission(mainView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //check if WRITE permission has been granted
        if (hasPermissionStorage != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) mainView.getContext(),
                    permissions.toArray(new String[permissions.size()]), 1);
        }
    }

    public void startDetailActivity(int position, String title, String desc, View view) {
        Intent intent = new Intent(mainView.getContext(), DetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        intent.putExtra("position", position);
        intent.putExtra("caller","Main");
        String transitionName = mainView.getContext().getString(R.string.transition_name);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mainView.getContext(),
                        view,
                        transitionName
                );
        ActivityCompat.startActivityForResult((Activity) mainView.getContext(), intent, 1, options.toBundle());
    }

    private void hideSoftKeyboard() {
        if (mainView.getFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mainView.getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mainView.getFocus().getWindowToken(), 0);
        }
    }

    private void showEmptyTitleError(int redId) {
        mainView.showEmptyTitleError(redId);
    }

    public void getNotes() {
        mainView.showNotes(dbController.getAllNotes());
    }

    public void closeDB() {
        dbController.close();
    }

    public void addNoteToDB(Note note) {
        dbController.addNote(note);
    }

    private void deleteNoteFromDB(Note note) {
        dbController.deleteNote(note);
        dbController.addNoteToTrash(note);
    }

    public void updateNoteInDB(Note note) {
        dbController.updateNote(note);
    }

    public void deleteNote(Note note, int position) {
        deleteNoteFromDB(note);
        mainView.updateNotesAfterDeletion(note);
        mainView.showSnackBar(note, position);
    }

    private void addNote(String title, String desc) {
        Note note = new Note(title, desc);
        Date date = new Date();
        //get current system date
        String Date = DateFormat.getDateInstance().format(date);
        note.setDate(Date);
        mainView.showAddedNote(note);
    }

    public void filter(List<Note> models, String query) {
        //convert query text to lower case for easier searching
        query = query.toLowerCase();
        ArrayList<Note> filteredModelList = new ArrayList<>();
        //iterate over all notes and check if they contain the query string
        for (Note model : models) {
            final String text = model.getTitle().concat(" ").concat(model.getDesc()).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        mainView.showFilteredNotes(filteredModelList);
    }

    public void ArcTime() {

        frameLayout = mainView.getFrame();
        fab = mainView.getFAB();
        //get center of FAB
        final int cx = (frameLayout.getLeft() + frameLayout.getRight()) / 2;
        final int cy = (frameLayout.getTop() + frameLayout.getBottom()) / 2;

        ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(fab, cx,
                cy, 90, Side.LEFT)
                .setDuration(300);
        arcAnimator.addListener(new SimpleListener() {
            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                fabPressed();
            }
        });
        arcAnimator.start();
    }

    public void fabPressed() {

        //retrieve note title and content
        final String s1 = mainView.getNoteTitle();
        final String s2 = mainView.getNoteDescription();

        final RelativeLayout first = mainView.getFirstLayout();
        final LinearLayout second = mainView.getSecondLayout();
        SearchView searchView = mainView.getSearchView();

        Animation animation = AnimationUtils.loadAnimation(mainView.getContext(), R.anim.simple_grow);



        //get positions for animation
        final int cx2 = fab.getLeft();
        final int cx = (frameLayout.getLeft() + frameLayout.getRight()) / 2;
        final int cy2 = fab.getTop();
        final int cy = (frameLayout.getTop() + frameLayout.getBottom()) / 2;
        final int radius = Math.max(second.getWidth(), second.getHeight());

        //final position of FAB after arc animation
        fab.setX(cx2);
        fab.setY(cy2);
        fab.startAnimation(animation);
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
                fab.setImageResource(R.drawable.ic_done_white);
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainView.getContext(), R.color.colorPrimary)));

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

                            mainView.resetEditTexts();
                            addNote(s1, s2);

                        } else {
                            showEmptyTitleError(R.string.empty_note_message);
                        }

                        fab.setImageResource(R.drawable.ic_mode_edit_black);
                        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainView.getContext(), R.color.colorAccentSecondary)));
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
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainView.getContext(), R.color.colorPrimary)));
                fab.setImageResource(R.drawable.ic_done_white);

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
                            mainView.resetEditTexts();
                            addNote(s1, s2);
                        } else {
                            showEmptyTitleError(R.string.empty_note_message);
                        }

                        fab.setImageResource(R.drawable.ic_mode_edit_black);
                        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mainView.getContext(), R.color.colorAccentSecondary)));

                        firstView = true;
                    }
                });
                anim.start();


            }
        }

    }



}
