package com.irm.vocabulario;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rivasyshyn on 09.06.2015.
 */
public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.VH> {

    private CardModel[] cardModels;

    public void updateDataSet(CardModel[] cardModels){
        this.cardModels = cardModels;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VH extends RecyclerView.ViewHolder {

        private VH(View itemView) {
            super(itemView);
        }
    }

}
