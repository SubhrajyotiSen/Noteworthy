package me.subhrajyoti.noteworthy.Presenters;

import android.content.Intent;

import me.subhrajyoti.noteworthy.Activities.DetailsActivity;
import me.subhrajyoti.noteworthy.Models.Note;
import me.subhrajyoti.noteworthy.Views.DetailsView;

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
            intent.putExtra("title", note.getTitle());
            intent.putExtra("desc", note.getDesc());
            intent.putExtra("position",position);
            detailsView.finishIntent(intent);
        } else
            detailsView.showEmptyTitleSnackBar();
    }

    public void updateTexts(Intent intent){
        detailsView.setNoteTitle(intent.getStringExtra("title"));
        detailsView.setNoteDetail(intent.getStringExtra("desc"));
        DetailsActivity.position = intent.getIntExtra("position",-1);

    }

    public void showNotEditableError(){
        detailsView.showNotEditableSnackBar();
    }
}
