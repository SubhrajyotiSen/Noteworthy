package com.example.babai.ranndom2.BackUp;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;

public interface Backup {
    void init(@NonNull final Activity activity);

    void start();

    void stop();

    GoogleApiClient getClient();
}