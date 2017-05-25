package com.magdy.mguide.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.magdy.mguide.Data.Contract.Movie;

/**
 * Created by engma on 5/23/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String NAME = "Movies.db";
    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String builder = "CREATE TABLE " + Movie.TABLE_NAME + " ("
                + Movie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Movie.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + Movie.COLUMN_TITLE + " TEXT NOT NULL, "
                + Movie.COLUMN_DATE + " TEXT NOT NULL, "
                + Movie.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + Movie.COLUMN_PIC_LINK + " TEXT NOT NULL, "
                + Movie.COLUMN_RATE + " TEXT NOT NULL, "
                + "UNIQUE (" + Movie.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL(" DROP TABLE IF EXISTS " + Movie.TABLE_NAME);
        onCreate(db);
    }
}
