package com.irm.vocabulario;

/**
 * Created by rivasyshyn on 09.06.2015.
 */
public class CardModel {

    private long id;
    private String word;
    private String translation;

    public CardModel(long id, String word, String translation) {
        this.id = id;
        this.translation = translation;
        this.word = word;
    }

    public CardModel(String word, String translation) {
        this(-1, word, translation);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
