package com.irm.vocabulario;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rivasyshyn on 09.06.2015.
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.VH> {

    private CardModel[] cardModels;
    private OnSelectionListener selectionListener;
    private DisplayModes displayMode;

    public CardsAdapter(DisplayModes displayMode, OnSelectionListener selectionListener) {
        this.displayMode = displayMode;
        this.selectionListener = selectionListener;
        cardModels = new CardModel[0];
    }

    public void updateDataSet(CardModel[] cardModels) {
        this.cardModels = cardModels;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bind(cardModels[position]);
    }

    @Override
    public int getItemCount() {
        return cardModels.length;
    }

    public void updateDisplayMode(DisplayModes mode) {
        this.displayMode = mode;
        notifyDataSetChanged();
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private boolean scheduled = false;
        private CardModel cardModel;
        private TextView word, translation;
        private ImageView iword, itranslation;

        private VH(View itemView) {
            super(itemView);
            word = (TextView) itemView.findViewById(R.id.tv_word);
            translation = (TextView) itemView.findViewById(R.id.tv_translation);
            iword = (ImageView) itemView.findViewById(R.id.img_word);
            itranslation = (ImageView) itemView.findViewById(R.id.img_translation);
            itemView.setOnClickListener(this);
            iword.setOnClickListener(this);
            itranslation.setOnClickListener(this);
        }

        private void bind(CardModel model) {
            scheduled = false;
            this.cardModel = model;
            word.setText(model.getWord());
            translation.setText(model.getTranslation());
            switch (displayMode) {
                case FULL:
                    iword.setVisibility(View.INVISIBLE);
                    itranslation.setVisibility(View.INVISIBLE);
                    word.setVisibility(View.VISIBLE);
                    translation.setVisibility(View.VISIBLE);
                    break;
                case RIGHT:
                    iword.setVisibility(View.VISIBLE);
                    itranslation.setVisibility(View.INVISIBLE);
                    word.setVisibility(View.INVISIBLE);
                    translation.setVisibility(View.VISIBLE);
                    break;
                case LEFT:
                    iword.setVisibility(View.INVISIBLE);
                    itranslation.setVisibility(View.VISIBLE);
                    word.setVisibility(View.VISIBLE);
                    translation.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_word:
                    iword.setVisibility(View.INVISIBLE);
                    word.setVisibility(View.VISIBLE);
                    scheduleHide();
                    break;
                case R.id.img_translation:
                    itranslation.setVisibility(View.INVISIBLE);
                    translation.setVisibility(View.VISIBLE);
                    scheduleHide();
                    break;
                default:
                    if (selectionListener != null)
                        selectionListener.onSelected(cardModel);
                    break;
            }

        }

        private void scheduleHide() {
            scheduled = true;
            itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scheduled)
                        bind(cardModel);
                }
            }, 5000);
        }
    }

    public interface OnSelectionListener {
        void onSelected(CardModel cardModel);
    }

}
