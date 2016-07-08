package com.subhrajyoti.babai.noteworthy.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.subhrajyoti.babai.noteworthy.Models.Note;
import com.subhrajyoti.babai.noteworthy.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<MainViewHolder> {



    ArrayList<Note> notes;
    ColorGenerator generator = ColorGenerator.MATERIAL;


    public RecyclerAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder mainViewHolder, int i) {
        mainViewHolder.title.setText(notes.get(i).gettitle());
        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(notes.get(i).gettitle().charAt(0)).toUpperCase(), generator.getColor(notes.get(i).gettitle()));
        mainViewHolder.imageView.setImageDrawable(drawable);
        mainViewHolder.dateview.setText(notes.get(i).getDate());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


}