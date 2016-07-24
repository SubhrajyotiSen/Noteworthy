package com.subhrajyoti.babai.noteworthy.Utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.subhrajyoti.babai.noteworthy.BackUp.RestoreDB;
import com.subhrajyoti.babai.noteworthy.BackUp.SaveDB;
import com.subhrajyoti.babai.noteworthy.R;
import com.subhrajyoti.babai.noteworthy.Views.MainView;

public class Dialogs {

    private static MainView mainView;

    public static void offline(final MainView view) {
        mainView = view;
        final Context context = mainView.getContext();
        new MaterialDialog.Builder(context)
                .title(R.string.backup_title)
                .content(R.string.backup_content)
                .positiveText(R.string.backup_button)
                .negativeText(R.string.restore)
                .neutralText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        boolean result = SaveDB.save();
                        Toast.makeText(context, result ? context.getString(R.string.backup_success) : context.getString(R.string.backup_fail), Toast.LENGTH_SHORT).show();

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        boolean result = RestoreDB.importDB();
                        Toast.makeText(context, result ? context.getString(R.string.restore_success) : context.getString(R.string.restore_fail), Toast.LENGTH_SHORT).show();
                        mainView.showRestoredNotes();
                    }
                })
                .show();
    }

    public static void about(final Context context) {

        new MaterialDialog.Builder(context)
                .title("NoteWorthy v1.0.0")
                .content(((Build.VERSION.SDK_INT >= 24)) ? Html.fromHtml(context.getString(R.string.html_text), Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml("<p>Check out the project on <a href=\"https://github.com/SubhrajyotiSen/Noteworthy\">GitHub</a></p>"))
                .titleGravity(GravityEnum.CENTER)
                .contentGravity(GravityEnum.CENTER)
                .icon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_launcher, null))
                .show();
    }
}
