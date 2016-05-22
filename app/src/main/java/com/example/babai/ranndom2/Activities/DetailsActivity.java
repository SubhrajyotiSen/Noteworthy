package com.example.babai.ranndom2.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babai.ranndom2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

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

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        floatingActionButton.startAnimation(animation);

        final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
        if(cross !=null){
            cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        }
        if(getSupportActionBar()!=null){
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(cross );

        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void update() {

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
        update();
    }

}
