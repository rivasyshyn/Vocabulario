package com.irm.vocabulario;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rivasyshyn on 09.06.2015.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper INSTANCE;

    public static DbHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DbHelper(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        }
        return INSTANCE;
    }

    public static SQLiteDatabase getWritableDb(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CardDao.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
