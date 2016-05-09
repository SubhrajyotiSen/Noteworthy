package com.example.babai.ranndom2.Application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class NoteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //SugarContext.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //SugarContext.terminate();
    }
}
