package com.livepost.javiergzzr.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.livepost.javiergzzr.objects.Session;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "livePost";
    private static final String TABLE_SESSIONS = "listSessions";

    private static final String KEY_AUTO = "_id integer primary key autoincrement, ";
    private static final String KEY_ID = "id_fb";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PICTURE = "picture";

    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE "
            + TABLE_SESSIONS + "(" + KEY_AUTO + KEY_ID + " TEXT ," + KEY_AUTHOR
            + " TEXT, " + KEY_CATEGORY + " TEXT, " + KEY_TITLE
            + " TEXT, " + KEY_PICTURE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SESSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        // create new tables
        onCreate(db);
    }

    public void addSessions(Session sessions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_ID, sessions.getId());
        values.put(KEY_AUTHOR, sessions.getAuthor());
        values.put(KEY_CATEGORY, sessions.getCategory());
        values.put(KEY_TITLE, sessions.getTitle());
        values.put(KEY_PICTURE, "hola");
        // Inserting Row
        db.insert(TABLE_SESSIONS, null, values);
        db.close();
    }

    public List<Session> getAllSessions() {
        List<Session> sessionsList = new ArrayList<Session>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SESSIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //(String author, String category, String lastMessage, String lastTime, String picture, long timestamp, String title)
                Session session = new Session(cursor.getString(0), cursor.getString(1)
                        , cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5), Long.getLong(cursor.getString(6))
                        , cursor.getString(7));
                // Adding contact to list
                sessionsList.add(session);
            } while (cursor.moveToNext());
        }

        // return contact list
        return sessionsList;
    }
}
