package com.mrizkypk.umsdaily.manager;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

public class DatabaseManager {
    public static FirebaseDatabase instance;

    public static FirebaseDatabase getInstance() {
        if (instance == null) {
            instance = FirebaseDatabase.getInstance();
            //instance.setLogLevel(Logger.Level.DEBUG);
            instance.setPersistenceEnabled(true);
        }

        return instance;
    }
}
