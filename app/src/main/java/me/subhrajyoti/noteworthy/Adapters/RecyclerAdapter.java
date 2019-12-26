package me.subhrajyoti.noteworthy.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import me.subhrajyoti.noteworthy.Models.Note;
import me.subhrajyoti.noteworthy.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<MainViewHolder> {


    private ArrayList<Note> notes;
    private ColorGenerator generator = ColorGenerator.MATERIAL;


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
        mainViewHolder.title.setText(notes.get(i).getTitle());
        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(notes.get(i).getTitle().charAt(0)).toUpperCase(), generator.getColor(notes.get(i).getTitle()));
        mainViewHolder.imageView.setImageDrawable(drawable);
        mainViewHolder.dateView.setText(notes.get(i).getDate());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


}