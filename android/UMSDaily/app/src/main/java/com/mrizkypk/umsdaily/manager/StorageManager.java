package com.mrizkypk.umsdaily.manager;

import com.google.firebase.storage.FirebaseStorage;

public class StorageManager {
    public static FirebaseStorage instance;

    public static FirebaseStorage getInstance() {
        if (instance == null) {
            instance = FirebaseStorage.getInstance();
        }
        return instance;
    }
}
