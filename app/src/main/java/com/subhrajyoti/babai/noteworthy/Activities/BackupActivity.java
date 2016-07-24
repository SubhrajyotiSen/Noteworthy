package com.subhrajyoti.babai.noteworthy.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.subhrajyoti.babai.noteworthy.Adapters.BackupAdapter;
import com.subhrajyoti.babai.noteworthy.BackUp.Backup;
import com.subhrajyoti.babai.noteworthy.BackUp.GoogleDriveBackup;
import com.subhrajyoti.babai.noteworthy.Models.NoteBackup;
import com.subhrajyoti.babai.noteworthy.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/*
    Thanks Paolo Rotolo
*/

public class BackupActivity extends AppCompatActivity {
    private int REQUEST_CODE_PICKER = 2;
    private int REQUEST_CODE_SELECT = 3;
    private int REQUEST_CODE_PICKER_FOLDER = 4;

    private Backup backup;
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "notes_drive_backup";
    private Button backupButton;
    private TextView manageButton;
    private TextView folderTextView;
    private IntentSender intentPicker;
    private String backupFolder;
    private ExpandableHeightListView backupListView;
    private LinearLayout selectFolderButton;
    private String BACKUP_FOLDER_KEY = "backup_folder";

    private SharedPreferences sharedPref;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (getSupportActionBar()!=null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Backup/Restore");

        backup = new GoogleDriveBackup();
        backup.init(this);
        connectClient();
        mGoogleApiClient = backup.getClient();

        backupButton = (Button) findViewById(R.id.activity_backup_drive_button_backup);
        manageButton = (TextView) findViewById(R.id.activity_backup_drive_button_manage_drive);
        folderTextView = (TextView) findViewById(R.id.activity_backup_drive_textview_folder);
        selectFolderButton = (LinearLayout) findViewById(R.id.activity_backup_drive_button_folder);
        backupListView = (ExpandableHeightListView) findViewById(R.id.activity_backup_drive_listview_restore);

        assert backupListView != null;
        backupListView.setExpanded(true);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Folder picker, then upload the file on Drive
                openFolderPicker(true);
            }
        });

        selectFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check first if a folder is already selected
                if (!"".equals(backupFolder)) {
                    //Start the picker to choose a folder
                    //False because we don't want to upload the backup on drive then
                    openFolderPicker(false);
                }
            }
        });

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openOnDrive(DriveId.decodeFromString(backupFolder));
            }
        });

        // Show backup folder, if exists
        backupFolder = sharedPref.getString(BACKUP_FOLDER_KEY, "");
        if (!("").equals(backupFolder)) {
            setBackupFolderTitle(DriveId.decodeFromString(backupFolder));
            manageButton.setVisibility(View.VISIBLE);
        }

        // Populate backup list
        if (!("").equals(backupFolder)) {
            getBackupsFromDrive(DriveId.decodeFromString(backupFolder).asDriveFolder());
        }
    }

    private void setBackupFolderTitle(DriveId id) {
        id.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        folderTextView.setText(metadata.getTitle());
                    }
                }
        );
    }



    private void openFolderPicker(boolean uploadToDrive) {
        if (uploadToDrive) {
            // First we check if a backup folder is set
            if ("".equals(backupFolder)) {
                try {
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        if (intentPicker == null)
                            intentPicker = buildIntent();
                        //Start the picker to choose a folder
                        startIntentSenderForResult(
                                intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
                    }
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Unable to send intent", e);
                    showErrorDialog();
                }
            } else {
                uploadToDrive(DriveId.decodeFromString(backupFolder));
            }
        } else {
            try {
                intentPicker = null;
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    if (intentPicker == null)
                        intentPicker = buildIntent();
                    //Start the picker to choose a folder
                    startIntentSenderForResult(
                            intentPicker, REQUEST_CODE_PICKER_FOLDER, null, 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
                showErrorDialog();
            }
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }

    private void getBackupsFromDrive(DriveFolder folder){
        final Activity activity = this;
        SortOrder sortOrder = new SortOrder.Builder()
                .addSortDescending(SortableField.MODIFIED_DATE).build();
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "glucosio.realm"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    private ArrayList<NoteBackup> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        for (int i=0; i<size; i++){
                            Metadata metadata = buffer.get(i);
                            DriveId driveId = metadata.getDriveId();
                            Date modifiedDate = metadata.getModifiedDate();
                            long backupSize = metadata.getFileSize();
                            backupsArray.add(new NoteBackup(driveId, modifiedDate, backupSize));
                            backupListView.setAdapter(new BackupAdapter(activity, R.layout.list_item, backupsArray));
                        }

                    }
                });
    }

    public void downloadFromDrive(DriveFile file) {
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }

                        // DriveContents object contains pointers
                        // to the actual byte stream
                        DriveContents contents = result.getDriveContents();
                        InputStream input = contents.getInputStream();

                        try {
                            File file = new File(Environment.getDataDirectory(), "//data//" + "com.example.babai.ranndom2"
                                    + "//databases//" + "notes.db");
                            try {
                                try (OutputStream output = new FileOutputStream(file)) {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = input.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } finally {
                            try {
                                input.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getApplicationContext(), "Restart", Toast.LENGTH_LONG).show();

                        // Reboot app
                        Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                });
    }

    private void uploadToDrive(DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.e(TAG, "Error while trying to create new file contents");
                                showErrorDialog();
                                return;
                            }
                            final DriveContents driveContents = result.getDriveContents();

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();

                                    FileInputStream inputStream = null;
                                    try {
                                        inputStream = new FileInputStream(new File(Environment.getDataDirectory(), "//data//" + "com.example.babai.ranndom2"
                                                + "//databases//" + "notes.db"));
                                    } catch (FileNotFoundException e) {
                                        showErrorDialog();
                                        e.printStackTrace();
                                    }

                                    byte[] buf = new byte[1024];
                                    int bytesRead;
                                    try {
                                        if (inputStream != null) {
                                            while ((bytesRead = inputStream.read(buf)) > 0) {
                                                outputStream.write(buf, 0, bytesRead);
                                            }
                                        }
                                    } catch (IOException e) {

                                        showErrorDialog();
                                        e.printStackTrace();
                                    }


                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("glucosio.realm")
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in selected folder
                                    folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                                                    if (!result.getStatus().isSuccess()) {
                                                        Log.d(TAG, "Error while trying to create the file");
                                                        showErrorDialog();
                                                        finish();
                                                        return;
                                                    }
                                                    showSuccessDialog();
                                                    finish();
                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        }
    }

    private void openOnDrive(DriveId driveId){
        driveId.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        String url = metadata.getAlternateLink();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
            // REQUEST_CODE_PICKER
            case 2:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());

                    uploadToDrive(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;
            // REQUEST_CODE_PICKER_FOLDER
            case 4:
                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());
                    // Restart activity to apply changes
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    private void saveBackupFolder(String folderPath) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(BACKUP_FOLDER_KEY, folderPath);
        editor.apply();
    }

    private void showSuccessDialog() {
        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
    }


    public void connectClient() {
        backup.start();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
