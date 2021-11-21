package com.android.Luvio.utilities;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class SQLiteManager {
    File storagePath;


    public SQLiteManager(File storagePath, SQLiteDatabase db) {
        this.storagePath = storagePath;
        this.db = db;
    }

    SQLiteDatabase db=SQLiteDatabase.openDatabase()
}
