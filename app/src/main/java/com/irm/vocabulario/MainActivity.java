package com.irm.vocabulario;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private View editView;
    private EditText etWord, etTranslation;
    private FloatingActionButton actionButton;
    private CardDao cardDao;
    private CardModel[] cardModels;
    private Handler handler;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        cardDao = new CardDao(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CardsAdapter());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.ic_v_outline);
        setSupportActionBar(toolbar);

        editView = findViewById(R.id.edit);
        actionButton = (FloatingActionButton) findViewById(R.id.action_button);
        etWord = (EditText) editView.findViewById(R.id.et_word);
        etTranslation = (EditText) editView.findViewById(R.id.et_translation);

        etTranslation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveCard();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editMode) {
            getMenuInflater().inflate(R.menu.menu_main_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_cancel:
                setEditMode(false);
                return true;
            case R.id.action_save:
                saveCard();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_play:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addCard(View view) {
        setEditMode(true);
    }

    private void saveCard() {
        setEditMode(false);
    }

    private void setEditMode(boolean edit) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editView.setVisibility(edit ? View.VISIBLE : View.GONE);
        actionButton.setVisibility(edit ? View.GONE : View.VISIBLE);
        if (edit) {
            etWord.requestFocus();
            imm.showSoftInput(etWord, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(editView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        editMode = edit;
        invalidateOptionsMenu();
    }

    private void loadCards() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cardModels = cardDao.getCards();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                    }
                });
            }
        }).run();
    }

    private void updateList() {
        CardsAdapter adapter = (CardsAdapter) recyclerView.getAdapter();
        adapter.updateDataSet(cardModels);
    }
}
