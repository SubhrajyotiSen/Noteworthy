package com.subhrajyoti.babai.noteworthy.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.subhrajyoti.babai.noteworthy.Activities.BackupActivity;
import com.subhrajyoti.babai.noteworthy.Utils.FormatDateTime;
import com.subhrajyoti.babai.noteworthy.Models.NoteBackup;
import com.subhrajyoti.babai.noteworthy.R;
import com.google.android.gms.drive.DriveId;

import java.util.List;

public class BackupAdapter extends ArrayAdapter<NoteBackup> {

    private Context context;
    private FormatDateTime formatDateTime;

    public BackupAdapter(Context context, int resource, List<NoteBackup> items) {
        super(context, resource, items);
        this.context = context;
        formatDateTime = new FormatDateTime(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }

        NoteBackup p = getItem(position);
        final DriveId driveId= p.getDriveId();
        final String modified = formatDateTime.formatDate(p.getModifiedDate());
        final String size = humanReadableByteCount(p.getBackupSize(), true);

        if (p != null) {
            TextView modifiedTextView = (TextView) v.findViewById(R.id.item_history_time);
            TextView typeTextView = (TextView) v.findViewById(R.id.item_history_type);
            modifiedTextView.setText(modified);
            typeTextView.setText(size);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_layout);
                TextView createdTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_created);
                TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
                Button restoreButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_restore);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_cancel);

                createdTextView.setText(modified);
                sizeTextView.setText(size);

                restoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BackupActivity)context).downloadFromDrive(driveId.asDriveFile());
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return v;
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
