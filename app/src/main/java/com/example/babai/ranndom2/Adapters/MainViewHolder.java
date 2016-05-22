package com.example.babai.ranndom2.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.babai.ranndom2.R;

public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    CardView cv;
    TextView title;
    ImageView imageView;
    TextView dateview;

    MainViewHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.card_view);
        title = (TextView) itemView.findViewById(R.id.title);
        imageView = (ImageView) itemView.findViewById(R.id.letter_head);
        dateview = (TextView) itemView.findViewById(R.id.dateView);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}