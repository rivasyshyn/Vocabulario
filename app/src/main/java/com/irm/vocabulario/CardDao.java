package com.irm.vocabulario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Created by rivasyshyn on 09.06.2015.
 */
public class CardDao {

    public static final String CREATE = "CREATE TABLE CARD (id INTEGER PRIMARY KEY, word TEXT NOT NULL, translation TEXT)";
    private static final String SELECT_CARD = "SELECT * FROM CARD WHERE id==?";
    private static final String SELECT_CARDS = "SELECT * FROM CARD ORDER BY word ASC";
    private static final String SELECT_CARD_BY_WORD = "SELECT * FROM CARD WHERE word LIKE '?%' ORDER BY word ASC";
    private static final String SELECT_CARD_BY_TRANSLATION = "SELECT * FROM CARD WHERE translation LIKE '?%' ORDER BY translation ASC";

    private SQLiteDatabase db;

    public CardDao(Context context) {
        db = DbHelper.getWritableDb(context);
    }

    public CardModel getCard(long id) {
        Cursor cursor = db.rawQuery(SELECT_CARD, new String[]{
                getId(id)
        });
        if (cursor.moveToFirst()) {
            CardModel cardModel = cursorToCard(cursor);
            return cardModel;
        }
        return null;
    }

    public CardModel[] getCards() {
        Cursor cursor = db.rawQuery(SELECT_CARDS, null);
        return cursorToCards(cursor);
    }

    public CardModel addOrUpdate(CardModel cardModel) {
        try {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("word", cardModel.getWord());
            contentValues.put("translation", cardModel.getTranslation());
            long id = db.insert("CARD", "id", contentValues);
            if (id > -1) {
                cardModel.setId(id);
                return cardModel;
            }
            db.update("CARD", contentValues, "id==?", new String[]{getId(cardModel.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return cardModel;
    }

    public CardModel delete(CardModel cardModel) {
        try {
            db.beginTransaction();
            db.delete("CARD", "id==?", new String[]{getId(cardModel.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return cardModel;
    }

    public CardModel[] search(String query) {
        Cursor cursor = db.rawQuery(SELECT_CARD_BY_WORD, new String[]{query});
        return cursorToCards(cursor);
    }

    public CardModel[] searchByTranslation(String query) {
        Cursor cursor = db.rawQuery(SELECT_CARD_BY_TRANSLATION, new String[]{query});
        return cursorToCards(cursor);
    }

    @NonNull
    private CardModel cursorToCard(Cursor cursor) {
        long cid = cursor.getLong(0);
        String word = cursor.getString(1);
        String translation = cursor.getString(2);
        return new CardModel(cid, word, translation);
    }

    @NonNull
    private CardModel[] cursorToCards(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return new CardModel[0];
        }
        CardModel[] cardModels = new CardModel[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            cardModels[i] = cursorToCard(cursor);
            i++;
        }
        return cardModels;
    }

    @NonNull
    private String getId(long id) {
        return Long.toString(id);
    }

}
