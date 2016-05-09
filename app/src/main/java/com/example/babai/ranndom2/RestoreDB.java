package com.example.babai.ranndom2;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RestoreDB {

    public static boolean importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.example.babai.ranndom2"
                        + "//files//" + "default.realm";
                String backupDBPath = "default.realm"; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            return true;
        } catch (Exception e) {
            Log.v("TAG",e.getLocalizedMessage());
            return false;

        }
        /*String restoreFilePath = Environment.getExternalStorageDirectory().getPath()+"/"+"default.realm";
        copyBundledRealmFile(restoreFilePath, "default.realm");
        Realm realm = Realm.getDefaultInstance();
        realm.refresh();
        return true;*/
    }

    /*private static String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File path = new File(Environment.getDataDirectory(),"//data//" + "com.example.babai.ranndom2"
                    + "//files//");
            File file = new File(path, outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
