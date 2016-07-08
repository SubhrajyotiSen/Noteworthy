package com.subhrajyoti.babai.noteworthy.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhrajyoti.babai.noteworthy.R;

public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    CardView cv;
    TextView title;
    ImageView imageView;
    TextView dateView;

    MainViewHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.card_view);
        title = (TextView) itemView.findViewById(R.id.title);
        imageView = (ImageView) itemView.findViewById(R.id.letter_head);
        dateView = (TextView) itemView.findViewById(R.id.dateView);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}