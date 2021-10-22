package it.bonny.app.wisetimer2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import it.bonny.app.wisetimer2.bean.TimerBean;

public class ManagerDB {
    public static final String KEY_ID = "id";
    public static final String KEY_NOME = "nome";
    public static final String KEY_NUM_SERIES = "numSeries";
    public static final String KEY_WORK_SEC = "workSec";
    public static final String KEY_WORK_MIN = "workMin";
    public static final String KEY_REST_SEC = "restSec";
    public static final String KEY_REST_MIN = "restMin";
    public static final String KEY_CHECKED = "checked";
    public static final String KEY_NUM_ROUNDS = "numRounds";
    public static final String KEY_REST_ROUNDS_SEC = "restRoundsSec";
    public static final String KEY_REST_ROUNDS_MIN = "restRoundsMin";
    private static final String DATABASE_NOME = "WiseTimerDB";
    private static final String DATABASE_TABLE = "TimerBean";
    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_CREATION = "CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
            KEY_NOME + " text not null, " + KEY_NUM_SERIES + " text not null, " + KEY_WORK_SEC + " text not null, " + KEY_WORK_MIN + " text not null, " +
            KEY_REST_SEC + " text not null, " + KEY_REST_MIN + " text not null, " + KEY_CHECKED + " integer, " + KEY_NUM_ROUNDS + " text not null, " +
            KEY_REST_ROUNDS_SEC + " text not null, " + KEY_REST_ROUNDS_MIN + " text not null)";
    private final DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public ManagerDB(Context context) {databaseHelper = new DatabaseHelper(context); }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NOME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            try {
                db.execSQL(DATABASE_CREATION);
            }
            catch (SQLException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS TimerBean");
            onCreate(db);
        }
    }

    public void openWriteDB() throws SQLException{ db = databaseHelper.getWritableDatabase(); }
    public void openReadDB() throws  SQLException{ db = databaseHelper.getReadableDatabase(); }
    public void close(){
        databaseHelper.close();
    }

    public long insert(TimerBean timerBean) {
        long _id;
        try{
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NOME, timerBean.getName().trim());
            initialValues.put(KEY_NUM_SERIES, timerBean.getNumSeries().trim());
            initialValues.put(KEY_WORK_MIN, timerBean.getWorkMin().trim());
            initialValues.put(KEY_WORK_SEC, timerBean.getWorkSec().trim());
            initialValues.put(KEY_REST_MIN, timerBean.getRestMin().trim());
            initialValues.put(KEY_REST_SEC, timerBean.getRestSec().trim());
            initialValues.put(KEY_NUM_ROUNDS, timerBean.getNumRounds().trim());
            initialValues.put(KEY_REST_ROUNDS_MIN, timerBean.getRestRoundsMin().trim());
            initialValues.put(KEY_REST_ROUNDS_SEC, timerBean.getRestRoundsSec().trim());
            long checked;
            if(timerBean.isChecked())
                checked = 1;
            else
                checked = 0;

            initialValues.put(KEY_CHECKED, checked);
            _id = db.insert(DATABASE_TABLE, null, initialValues);
        }catch (Exception e){
            _id = -1;
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return _id;
    }

    public boolean deleteById(long id){
        boolean result;
        try{
            result = db.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
        }catch (Exception e){
            result = false;
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return result;
    }

    public Cursor getAllTimerBean(){
        Cursor cursor;
        try{
            cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NOME, KEY_NUM_SERIES, KEY_WORK_MIN, KEY_WORK_SEC, KEY_REST_MIN, KEY_REST_SEC,
                            KEY_CHECKED, KEY_NUM_ROUNDS, KEY_REST_ROUNDS_MIN, KEY_REST_ROUNDS_SEC},
                    null, null, null, null, KEY_CHECKED + " DESC, UPPER(" + KEY_NOME + ") ASC");
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        return cursor;
    }

    /*public Cursor getTimerBean(long id){
        Cursor cursor;
        try{
            cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_NOME,KEY_NUM_SERIES, KEY_WORK_MIN, KEY_WORK_SEC, KEY_REST_MIN, KEY_REST_SEC,
                            KEY_CHECKED, KEY_NUM_ROUNDS, KEY_REST_ROUNDS_MIN, KEY_REST_ROUNDS_SEC},
                    KEY_ID + "=" + id, null, null, null, null, null);
            if (cursor != null) cursor.moveToFirst();
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        return cursor;
    }*/

    public Cursor findTimerByName(String name) {
        Cursor cursor;
        try {
            cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NOME,KEY_NUM_SERIES, KEY_WORK_MIN, KEY_WORK_SEC, KEY_REST_MIN, KEY_REST_SEC,
                            KEY_CHECKED, KEY_NUM_ROUNDS, KEY_REST_ROUNDS_MIN, KEY_REST_ROUNDS_SEC},
                    KEY_NOME + " =? ", new String[]{name}, null, null, null, null);
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        return cursor;
    }

    public Cursor findTimerByChecked(long checkedValue){
        Cursor cursor;
        try {
            cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NOME,KEY_NUM_SERIES, KEY_WORK_MIN, KEY_WORK_SEC, KEY_REST_MIN, KEY_REST_SEC,
                            KEY_CHECKED, KEY_NUM_ROUNDS, KEY_REST_ROUNDS_MIN, KEY_REST_ROUNDS_SEC},
                    KEY_CHECKED + " =? ", new String[]{Long.toString(checkedValue)}, null, null, null, null);
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
        return cursor;
    }

    public void update(TimerBean timerBean) {
        try{
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NOME, timerBean.getName().trim());
            initialValues.put(KEY_NUM_SERIES, timerBean.getNumSeries().trim());
            initialValues.put(KEY_WORK_MIN, timerBean.getWorkMin().trim());
            initialValues.put(KEY_WORK_SEC, timerBean.getWorkSec().trim());
            initialValues.put(KEY_REST_MIN, timerBean.getRestMin().trim());
            initialValues.put(KEY_REST_SEC, timerBean.getRestSec().trim());
            initialValues.put(KEY_NUM_ROUNDS, timerBean.getNumRounds().trim());
            initialValues.put(KEY_REST_ROUNDS_MIN, timerBean.getRestRoundsMin().trim());
            initialValues.put(KEY_REST_ROUNDS_SEC, timerBean.getRestRoundsSec().trim());
            long checked;
            if(timerBean.isChecked())
                checked = 1;
            else
                checked = 0;

            initialValues.put(KEY_CHECKED, checked);
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

}
