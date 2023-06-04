package com.hamzasalahuddin.repjournal.recyclerviews;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamzasalahuddin.repjournal.R;

import java.util.ArrayList;

public class RepCountAdapter extends RecyclerView.Adapter<RepCountAdapter.RepCountViewHolder> {
    private ArrayList<RepCountButtonsModel> repCountButtonsArray;
    IRepCount mListener;

    public RepCountAdapter(ArrayList<RepCountButtonsModel> repCountButtonsArray, IRepCount mListener) {
        this.repCountButtonsArray = repCountButtonsArray;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RepCountAdapter.RepCountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rep_button_item, parent, false);
        return new RepCountViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RepCountAdapter.RepCountViewHolder holder, int position) {
        String repCountItem = repCountButtonsArray.get(position).getRepCountButtonIndex();
        holder.rep_count_button.setText(repCountItem);
        holder.rep_count_button.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("repCount", repCountItem);
            mListener.getRepCount(intent);
        });
    }

    @Override
    public int getItemCount() {
        return repCountButtonsArray.size();
    }

    public class RepCountViewHolder extends RecyclerView.ViewHolder {
        private Button rep_count_button;
        IRepCount mListener;

        public RepCountViewHolder(@NonNull View itemView, IRepCount mListener) {
            super(itemView);

            this.mListener = mListener;
            rep_count_button = itemView.findViewById(R.id.rep_count_button);
        }
    }

    public interface IRepCount {
        void getRepCount(Intent intent);
    }
}