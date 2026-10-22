package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/** Landing screen: progress overview + list of recent workouts. */
public class MainActivity extends AppCompatActivity {

    private WorkoutDatabaseHelper dbHelper;
    private WorkoutAdapter adapter;
    private TextView tvTotalCalories, tvTotalWorkouts, tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new WorkoutDatabaseHelper(this);

        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvEmpty = findViewById(R.id.tvEmpty);
        MaterialButton btnLogWorkout = findViewById(R.id.btnLogWorkout);
        RecyclerView recyclerView = findViewById(R.id.recyclerWorkouts);

        adapter = new WorkoutAdapter(this, new ArrayList<>(), this::confirmDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnLogWorkout.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LogWorkoutActivity.class)));
    }

    /** Reload data every time the screen becomes visible (e.g. after saving a workout). */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        try {
            List<Workout> workouts = dbHelper.getAllWorkouts();
            adapter.setWorkouts(workouts);
            tvEmpty.setVisibility(workouts.isEmpty() ? View.VISIBLE : View.GONE);

            NumberFormat nf = NumberFormat.getInstance();
            tvTotalCalories.setText(getString(R.string.total_calories_value,
                    nf.format(dbHelper.getTotalCalories())));
            tvTotalWorkouts.setText(getString(R.string.total_workouts_label)
                    + ": " + dbHelper.getWorkoutCount());
        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_load_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /** Ask the user to confirm before deleting. */
    private void confirmDelete(Workout workout) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, workout.getWorkoutType()))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                    if (dbHelper.deleteWorkout(workout.getId())) {
                        Toast.makeText(this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, R.string.msg_delete_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
