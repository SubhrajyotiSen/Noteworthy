package com.subhrajyoti.babai.noteworthy.Activities;

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

import com.subhrajyoti.babai.noteworthy.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    //bind views
    @Bind(R.id.titleText)
    TextView titleText;
    @Bind(R.id.detailsText)
    TextView detailsText;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        //initial animation for FAB
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        floatingActionButton.startAnimation(animation);

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
        titleText.setText(getIntent().getStringExtra("title"));
        detailsText.setText(getIntent().getStringExtra("desc"));
        position = getIntent().getIntExtra("position",-1);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
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
                shareNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //update the note
    private void update() {

        //stores the new/edited note in the intent
        //checks if note title is empty
        if (!titleText.getText().toString().trim().equals("")) {
            Intent intent = new Intent();
            intent.putExtra("title",titleText.getText().toString());
            intent.putExtra("desc",detailsText.getText().toString());
            intent.putExtra("position",position);
            setResult(RESULT_OK, intent);
            finish();
        } else
            Snackbar.make(coordinatorLayout, "Please enter a title", Snackbar.LENGTH_SHORT).show();


    }
    @Override
    public void onBackPressed() {
        //update the note if back button pressed
        update();
    }

    private void shareNote() {
        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.setType("text/plain");
        sharing.putExtra(Intent.EXTRA_TEXT, titleText.getText().toString() + "\n\n" + detailsText.getText().toString());
        startActivity(Intent.createChooser(sharing, "Share via"));
    }

}
