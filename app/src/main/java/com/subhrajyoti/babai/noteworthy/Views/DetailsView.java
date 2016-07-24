package com.subhrajyoti.babai.noteworthy.Views;

import android.content.Context;
import android.content.Intent;

public interface DetailsView {

    Context getContext();

    void setNoteTitle(String string);

    void setNoteDetail(String string);

    void finishIntent(Intent intent);

    void showSnackBar();

}
