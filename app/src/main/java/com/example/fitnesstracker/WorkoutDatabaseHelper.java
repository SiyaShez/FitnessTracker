package com.example.fitnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the FitnessTrackerDB SQLite database and the Workouts table.
 */
public class WorkoutDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "WorkoutDatabaseHelper";

    private static final String DATABASE_NAME = "FitnessTrackerDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_WORKOUTS = "Workouts";
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "workoutType";
    public static final String COL_DURATION = "duration";
    public static final String COL_CALORIES = "caloriesBurned";
    public static final String COL_INTENSITY = "intensity";
    public static final String COL_DATE = "date";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_WORKOUTS + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TYPE + " TEXT, "
                    + COL_DURATION + " INTEGER, "
                    + COL_CALORIES + " INTEGER, "
                    + COL_INTENSITY + " TEXT, "
                    + COL_DATE + " TEXT)";

    public WorkoutDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        onCreate(db);
    }

    /**
     * Inserts a new workout.
     * @return the new row id, or -1 if the insert failed
     */
    public long addWorkout(String type, int duration, int calories,
                           String intensity, String date) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TYPE, type);
            values.put(COL_DURATION, duration);
            values.put(COL_CALORIES, calories);
            values.put(COL_INTENSITY, intensity);
            values.put(COL_DATE, date);
            return db.insertOrThrow(TABLE_WORKOUTS, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error adding workout", e);
            return -1;
        }
    }

    /** Returns all workouts, newest first. Returns an empty list on error. */
    public List<Workout> getAllWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            try (Cursor c = db.query(TABLE_WORKOUTS, null, null, null,
                    null, null, COL_ID + " DESC")) {
                while (c.moveToNext()) {
                    workouts.add(new Workout(
                            c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                            c.getString(c.getColumnIndexOrThrow(COL_TYPE)),
                            c.getInt(c.getColumnIndexOrThrow(COL_DURATION)),
                            c.getInt(c.getColumnIndexOrThrow(COL_CALORIES)),
                            c.getString(c.getColumnIndexOrThrow(COL_INTENSITY)),
                            c.getString(c.getColumnIndexOrThrow(COL_DATE))));
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error reading workouts", e);
        }
        return workouts;
    }

    /**
     * Deletes a workout by id.
     * @return true if a row was deleted
     */
    public boolean deleteWorkout(long id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            int rows = db.delete(TABLE_WORKOUTS, COL_ID + " = ?",
                    new String[]{String.valueOf(id)});
            return rows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting workout " + id, e);
            return false;
        }
    }

    /** Total calories burned across all workouts. */
    public int getTotalCalories() {
        try {
            SQLiteDatabase db = getReadableDatabase();
            try (Cursor c = db.rawQuery(
                    "SELECT COALESCE(SUM(" + COL_CALORIES + "), 0) FROM " + TABLE_WORKOUTS, null)) {
                return c.moveToFirst() ? c.getInt(0) : 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error computing total calories", e);
            return 0;
        }
    }

    /** Total number of logged workouts. */
    public int getWorkoutCount() {
        try {
            SQLiteDatabase db = getReadableDatabase();
            try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORKOUTS, null)) {
                return c.moveToFirst() ? c.getInt(0) : 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error counting workouts", e);
            return 0;
        }
    }
}
