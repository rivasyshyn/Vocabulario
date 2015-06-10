package com.irm.vocabulario;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
    private CardModel edited;
    private Handler handler;
    private boolean editMode;
    private DisplayModes displayMode = DisplayModes.FULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        cardDao = new CardDao(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CardsAdapter(displayMode, new CardsAdapter.OnSelectionListener() {
            @Override
            public void onSelected(CardModel cardModel) {
                setEditMode(true);
                edited = cardModel;
                etWord.setText(cardModel.getWord());
                etTranslation.setText(cardModel.getTranslation());
            }
        }));

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
                    return saveCard();
                }
                return false;
            }
        });

        loadCards();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (edited == null) {
            menu.removeItem(R.id.action_delete);
        }
        MenuItem item = null;
        switch (displayMode){
            case FULL:
                item = menu.findItem(R.id.action_show_both);
                break;
            case LEFT:
                item = menu.findItem(R.id.action_show_left);
                break;
            case RIGHT:
                item = menu.findItem(R.id.action_show_right);
                break;
        }
        if(item != null){
            item.setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                deleteCard();
                return true;
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
            case R.id.action_show_both:
                item.setChecked(true);
                setHideOption(DisplayModes.FULL);
                return true;
            case R.id.action_show_left:
                item.setChecked(true);
                setHideOption(DisplayModes.LEFT);
                return true;
            case R.id.action_show_right:
                item.setChecked(true);
                setHideOption(DisplayModes.RIGHT);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addCard(View view) {
        etWord.setText("");
        etWord.setError(null);
        etTranslation.setText("");
        etTranslation.setError(null);
        setEditMode(true);
    }

    private boolean saveCard() {
        final String word = String.valueOf(etWord.getText());
        final String translation = String.valueOf(etTranslation.getText());
        if (TextUtils.isEmpty(word)) {
            etWord.setError("Can't be empty");
            etWord.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(translation)) {
            etTranslation.setText("Can't be empty");
            etTranslation.requestFocus();
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                CardModel cardModel = new CardModel(edited != null ? edited.getId() : -1, word, translation);
                edited = null;
                cardDao.addOrUpdate(cardModel);
                loadCards();
            }
        }).start();

        setEditMode(false);
        return true;
    }

    private void deleteCard() {
        if (edited != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cardDao.delete(edited);
                    loadCards();
                }
            }).start();
        }
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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                cardModels = cardDao.getCards();
                updateList();
            }
        };
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    private void updateList() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CardsAdapter adapter = (CardsAdapter) recyclerView.getAdapter();
                adapter.updateDataSet(cardModels);
            }
        };
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }

    public void setHideOption(DisplayModes mode) {
        this.displayMode = mode;
        CardsAdapter adapter = (CardsAdapter) recyclerView.getAdapter();
        adapter.updateDisplayMode(mode);
    }
}
