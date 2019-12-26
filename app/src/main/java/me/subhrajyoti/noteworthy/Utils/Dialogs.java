package me.subhrajyoti.noteworthy.Utils;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import android.text.Html;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import me.subhrajyoti.noteworthy.BackUp.RestoreDB;
import me.subhrajyoti.noteworthy.BackUp.SaveDB;
import me.subhrajyoti.noteworthy.R;
import me.subhrajyoti.noteworthy.Views.MainView;

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
                .title(R.string.app_name_full)
                .content(((Build.VERSION.SDK_INT >= 24)) ? Html.fromHtml(context.getString(R.string.html_text), Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml("<p>Check out the project on <a href=\"https://github.com/SubhrajyotiSen/Noteworthy\">GitHub</a></p>"))
                .titleGravity(GravityEnum.CENTER)
                .contentGravity(GravityEnum.CENTER)
                .icon(ResourcesCompat.getDrawable(context.getResources(), R.mipmap.ic_launcher, null))
                .show();
    }

    public static void comingSoon(final Context context) {

        new MaterialDialog.Builder(context)
                .title("Online backup")
                .content("Coming soon")
                .titleGravity(GravityEnum.CENTER)
                .contentGravity(GravityEnum.CENTER)
                .show();
    }
}
