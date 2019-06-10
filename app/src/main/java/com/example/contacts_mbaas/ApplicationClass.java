package com.example.contacts_mbaas;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class ApplicationClass extends Application {

    public static final String APPLICATION_ID = "6CB2A578-B7C4-9E5B-FF0F-15D36F192B00";
    public static final String API_KEY = "39D97AB6-F2DE-2C05-FFA9-4BBF460BA900";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Contact> contacts;

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp( getApplicationContext(), APPLICATION_ID, API_KEY );
    }
}
