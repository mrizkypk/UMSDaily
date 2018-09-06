package com.mrizkypk.umsdaily.helper;

import com.mrizkypk.umsdaily.manager.IntentManager;
import com.mrizkypk.umsdaily.manager.SharedPreferenceManager;

public class SessionHelper {
    public static void check(SharedPreferenceManager sharedPreferenceManager, IntentManager intentManager) {
        if (sharedPreferenceManager.getUserId().equals("EMPTY") && sharedPreferenceManager.getUserName().equals("EMPTY") && sharedPreferenceManager.getUserPassword().equals("EMPTY")) {
            intentManager.goLogin();
        }
    }

    public static void checkReverse(SharedPreferenceManager sharedPreferenceManager, IntentManager intentManager) {
        if (!sharedPreferenceManager.getUserId().equals("EMPTY") && !sharedPreferenceManager.getUserName().equals("EMPTY") && !sharedPreferenceManager.getUserPassword().equals("EMPTY")) {
            intentManager.goHome();
        }
    }
}