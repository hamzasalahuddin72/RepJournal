package com.hamzasalahuddin.repjournal.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamzasalahuddin.repjournal.R;

import java.util.ArrayList;
import java.util.Locale;

public class MuscleGroupsAdapter extends RecyclerView.Adapter<MuscleGroupsAdapter.ViewHolder> {
    private ArrayList<String> list;

    public MuscleGroupsAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.musclegroup_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.muscle_group_title_today.setText(list.get(position).toUpperCase(Locale.ROOT));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView muscle_group_title_today;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            muscle_group_title_today = itemView.findViewById(R.id.muscle_group_title_today);
        }
    }
}
