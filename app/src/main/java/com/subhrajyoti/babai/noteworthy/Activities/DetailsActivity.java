package com.subhrajyoti.babai.noteworthy.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.subhrajyoti.babai.noteworthy.Models.Note;
import com.subhrajyoti.babai.noteworthy.Presenters.DetailsPresenter;
import com.subhrajyoti.babai.noteworthy.R;
import com.subhrajyoti.babai.noteworthy.Views.DetailsView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements DetailsView {

    //bind views
    @Bind(R.id.titleText)
    TextView titleText;
    @Bind(R.id.detailsText)
    TextView detailsText;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    public static int position;
    private DetailsPresenter detailsPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        //initial animation for FAB
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        floatingActionButton.startAnimation(animation);

        //initialise presenter
        detailsPresenter = new DetailsPresenter(this);

        //switch drawer toggle icon with exit icon
        final Drawable cross = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_clear_white,null);
        if(cross !=null){
            cross.setColorFilter(ResourcesCompat.getColor(getResources(),R.color.icons,null), PorterDuff.Mode.SRC_ATOP);
        }
        if(getSupportActionBar()!=null){
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(cross );

        }

        //initialize views with data from the note
        detailsPresenter.updateTexts(getIntent());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsPresenter.update(getNote(),position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                detailsPresenter.shareNote(getNote());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //update the note
    @Override
    public void onBackPressed() {
        //update the note if back button pressed
        detailsPresenter.update(getNote(),position);
    }



    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setNoteTitle(String string) {
        titleText.setText(string);
    }

    @Override
    public void setNoteDetail(String string) {
        detailsText.setText(string);
    }

    @Override
    public void finishIntent(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showSnackBar() {
        Snackbar.make(coordinatorLayout, "Please enter a title", Snackbar.LENGTH_SHORT).show();
    }

    private Note getNote(){
        return new Note(titleText.getText().toString(),detailsText.getText().toString());
    }

}
