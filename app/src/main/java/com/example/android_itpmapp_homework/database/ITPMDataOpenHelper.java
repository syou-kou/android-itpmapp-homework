package com.example.android_itpmapp_homework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ITPMDataOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "itpmDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "itpmDatabase";
    public static final String _ID = "_id";
    public static final String COLUMN_TITLE = "title";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY, " + COLUMN_TITLE + " TEXT)";
    private static final String INIT_TABLE = "INSERT INTO " + TABLE_NAME + " VALUES " +
            "(1, 'ホーム')," +
            "(2, '事業内容')," +
            "(3, '企業情報')," +
            "(4, '採用情報')," +
            "(5, 'お問い合わせ')";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ITPMDataOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
