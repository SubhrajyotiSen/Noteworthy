package com.example.babai.ranndom2;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import io.realm.Realm;

public class SaveDB{
    public static boolean save(){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.example.babai.ranndom2"
                        + "//files//" + "default.realm";
                String backupDBPath = "NotesBackup.realm";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            return true;
        } catch (Exception e){
            return false;
        }
        /*Realm realm = Realm.getDefaultInstance();
        try {
            realm.writeCopyTo(new File(Environment.getExternalStorageDirectory(),"default.realm"));

            realm.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;*/

    }
}