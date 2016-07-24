package com.subhrajyoti.babai.noteworthy.Presenters;

import android.content.Intent;

import com.subhrajyoti.babai.noteworthy.Activities.DetailsActivity;
import com.subhrajyoti.babai.noteworthy.Models.Note;
import com.subhrajyoti.babai.noteworthy.Views.DetailsView;

public class DetailsPresenter {
    private DetailsView detailsView;

    public DetailsPresenter(DetailsView detailsView){
        this.detailsView = detailsView;
    }

    public void shareNote(Note note) {
        Intent sharing = new Intent(Intent.ACTION_SEND);
        sharing.setType("text/plain");
        sharing.putExtra(Intent.EXTRA_TEXT, note.getTitle() + "\n\n" + note.getDesc());
        detailsView.getContext().startActivity(Intent.createChooser(sharing, "Share via"));
    }

    public void update(Note note, int position) {

        //stores the new/edited note in the intent
        //checks if note title is empty
        if (!note.getTitle().trim().equals("")) {
            Intent intent = new Intent();
            intent.putExtra("title",note.getTitle().toString());
            intent.putExtra("desc",note.getDesc().toString());
            intent.putExtra("position",position);
            detailsView.finishIntent(intent);
        } else
            detailsView.showSnackBar();
    }

    public void updateTexts(Intent intent){
        detailsView.setNoteTitle(intent.getStringExtra("title"));
        detailsView.setNoteDetail(intent.getStringExtra("desc"));
        DetailsActivity.position = intent.getIntExtra("position",-1);

    }
}
