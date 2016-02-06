package com.example.babai.ranndom2.Application;

import android.app.Application;

import com.orm.SugarContext;


public class NoteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
