package com.example.fitnesstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;

/** RecyclerView adapter that displays workouts as Material cards. */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    /** Callback so the Activity can handle the delete confirmation. */
    public interface OnDeleteClickListener {
        void onDeleteClick(Workout workout);
    }

    private final Context context;
    private List<Workout> workouts;
    private final OnDeleteClickListener listener;

    public WorkoutAdapter(Context context, List<Workout> workouts, OnDeleteClickListener listener) {
        this.context = context;
        this.workouts = workouts;
        this.listener = listener;
    }

    /** Replace the data and refresh the list. */
    public void setWorkouts(List<Workout> newWorkouts) {
        this.workouts = newWorkouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout w = workouts.get(position);
        NumberFormat nf = NumberFormat.getInstance();

        holder.tvType.setText(w.getWorkoutType());
        holder.tvDetails.setText(context.getString(R.string.duration_format, w.getDuration())
                + "  \u2022  " + w.getIntensity());
        holder.tvCalories.setText(context.getString(R.string.calories_format,
                nf.format(w.getCaloriesBurned())));
        holder.tvDate.setText(w.getDate());

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(w));
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvType, tvDetails, tvCalories, tvDate;
        final ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
