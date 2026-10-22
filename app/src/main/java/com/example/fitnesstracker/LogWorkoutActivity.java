package com.example.fitnesstracker;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Screen for entering and saving a new workout. */
public class LogWorkoutActivity extends AppCompatActivity {

    private TextInputLayout tilType, tilDuration, tilCalories;
    private TextInputEditText etType, etDuration, etCalories;
    private Spinner spinnerIntensity;
    private WorkoutDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_workout);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        dbHelper = new WorkoutDatabaseHelper(this);

        tilType = findViewById(R.id.tilType);
        tilDuration = findViewById(R.id.tilDuration);
        tilCalories = findViewById(R.id.tilCalories);
        etType = findViewById(R.id.etType);
        etDuration = findViewById(R.id.etDuration);
        etCalories = findViewById(R.id.etCalories);
        spinnerIntensity = findViewById(R.id.spinnerIntensity);

        // Fill the spinner from the string-array resource (Low, Medium, High)
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.intensity_levels, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIntensity.setAdapter(spinnerAdapter);

        findViewById(R.id.btnSave).setOnClickListener(v -> saveWorkout());
    }

    private void saveWorkout() {
        // Clear old errors
        tilType.setError(null);
        tilDuration.setError(null);
        tilCalories.setError(null);

        String type = etType.getText() == null ? "" : etType.getText().toString().trim();
        String durationStr = etDuration.getText() == null ? "" : etDuration.getText().toString().trim();
        String caloriesStr = etCalories.getText() == null ? "" : etCalories.getText().toString().trim();

        boolean valid = true;

        if (type.isEmpty()) {
            tilType.setError(getString(R.string.error_type_required));
            valid = false;
        }

        int duration = parsePositiveInt(durationStr);
        if (duration <= 0) {
            tilDuration.setError(getString(durationStr.isEmpty() || isNumeric(durationStr)
                    ? R.string.error_duration_invalid : R.string.error_number_too_large));
            valid = false;
        }

        int calories = parsePositiveInt(caloriesStr);
        if (calories <= 0) {
            tilCalories.setError(getString(caloriesStr.isEmpty() || isNumeric(caloriesStr)
                    ? R.string.error_calories_invalid : R.string.error_number_too_large));
            valid = false;
        }

        if (!valid) return;

        String intensity = spinnerIntensity.getSelectedItem().toString();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        long id = dbHelper.addWorkout(type, duration, calories, intensity, date);
        if (id != -1) {
            Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
            finish(); // MainActivity refreshes in onResume()
        } else {
            Toast.makeText(this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /** Returns the parsed int, or -1 if the text is not a valid int. */
    private int parsePositiveInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isNumeric(String s) {
        return s.matches("\\d+") && s.length() < 10;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
