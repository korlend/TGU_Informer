package ru.tltsu.informer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SyncAdapterType;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* server json example
                {
                    "oid": 1477
                    "startOn": "2015-09-15 08:30:00.0"
                    "endOn": "2015-09-15 10:00:00.0"
                    "modifiedTime": "2015-07-09 11:50:19.0"
                    "lecturerFIO": "Парфенова Ольга Александровна"
                    "numberOfPair": "1"
                    "auditoriumAbbr": "УЛК-610"
                    "disciplineName": "Английский язык 3 /Английский язык/"
                    "kindOfWorkName": "Практика"
                }
        */

public class ScheduleContract {

    private ScheduleDBHelper mDbHelper;
    private final Context context;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    public ScheduleContract(Context context) {
        this.context = context;
        mDbHelper = new ScheduleDBHelper(context);
        dbWrite = mDbHelper.getWritableDatabase();
        dbRead = mDbHelper.getReadableDatabase();
    }

    public long insert(int id, String date, String lecturer, String discipline, int pair_number, String auditory, String kindofwork) {
        ContentValues values = new ContentValues();
        values.put(Lesson.COLUMN_NAME_LESSON_ID, id);
        values.put(Lesson.COLUMN_NAME_DATE, date);
        values.put(Lesson.COLUMN_NAME_AUDITORY, auditory);
        values.put(Lesson.COLUMN_NAME_DISCIPLINE, discipline);
        values.put(Lesson.COLUMN_NAME_KIND_OF_WORK, kindofwork);
        values.put(Lesson.COLUMN_NAME_LECTURER, lecturer);
        values.put(Lesson.COLUMN_NAME_PAIR_NUMBER, pair_number);

        return dbWrite.insert(Lesson.TABLE_NAME, null, values);
    }

    public long update(int id, String date, String lecturer, String discipline, int pair_number, String auditory, String kindofwork) {
        ContentValues values = new ContentValues();
        values.put(Lesson.COLUMN_NAME_LESSON_ID, id);
        values.put(Lesson.COLUMN_NAME_DATE, date);
        values.put(Lesson.COLUMN_NAME_AUDITORY, auditory);
        values.put(Lesson.COLUMN_NAME_DISCIPLINE, discipline);
        values.put(Lesson.COLUMN_NAME_KIND_OF_WORK, kindofwork);
        values.put(Lesson.COLUMN_NAME_LECTURER, lecturer);
        values.put(Lesson.COLUMN_NAME_PAIR_NUMBER, pair_number);

        return dbWrite.update(Lesson.TABLE_NAME, values, null, null);
    }

    public long delete(int oid) {
        return dbWrite.delete(Lesson.TABLE_NAME, String.format(Locale.ENGLISH, ScheduleDBHelper.SQL_DELETE_BY_ID, oid), null);
    }

    public long deleteAllRecords() {
        return dbWrite.delete(Lesson.TABLE_NAME, ScheduleDBHelper.SQL_DELETE_ALL_RECORDS, null);
    }

    public Map<Integer, Map<String, String>> selectLessonsByDate(String date) {
        String[] columns = {
                Lesson.COLUMN_NAME_LESSON_ID,
                Lesson.COLUMN_NAME_DATE,
                Lesson.COLUMN_NAME_AUDITORY,
                Lesson.COLUMN_NAME_KIND_OF_WORK,
                Lesson.COLUMN_NAME_DISCIPLINE,
                Lesson.COLUMN_NAME_LECTURER,
                Lesson.COLUMN_NAME_PAIR_NUMBER
        };
        String[] selectionArgs = {date};
        Map<Integer, Map<String, String>> lessons = new HashMap<>();

        Cursor cursor = dbRead.query(Lesson.TABLE_NAME, columns, ScheduleDBHelper.SQL_SELECT_BY_DATE, selectionArgs, null, null, null);

        //int LESSON_ID = cursor.getColumnIndex(Lesson.COLUMN_NAME_LESSON_ID);
        //int DATE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DATE);
        int AUDITORY = cursor.getColumnIndex(Lesson.COLUMN_NAME_AUDITORY);
        int KIND_OF_WORK = cursor.getColumnIndex(Lesson.COLUMN_NAME_KIND_OF_WORK);
        int DISCIPLINE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DISCIPLINE);
        int LECTURER = cursor.getColumnIndex(Lesson.COLUMN_NAME_LECTURER);
        int PAIR_NUMBER = cursor.getColumnIndex(Lesson.COLUMN_NAME_PAIR_NUMBER);
        cursor.moveToFirst();
        System.out.println(cursor.getString(DISCIPLINE));

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Map<String, String> pair = new HashMap<>();
            pair.put(context.getResources().getString(R.string.map_name_lesson_auditory), cursor.getString(AUDITORY));
            pair.put(context.getResources().getString(R.string.map_name_lesson_discipline), cursor.getString(DISCIPLINE));
            pair.put(context.getResources().getString(R.string.map_name_lesson_kind_of_work), cursor.getString(KIND_OF_WORK));
            pair.put(context.getResources().getString(R.string.map_name_lesson_lecturer), cursor.getString(LECTURER));

            lessons.put(cursor.getInt(PAIR_NUMBER), pair);
        }

        cursor.close();
        return lessons;
    }

    public Map<String, Map<Integer, Map<String, String>>> selectScheduleByDates(String dateBegin, String dateEnd) {
        String[] columns = {
                Lesson.COLUMN_NAME_LESSON_ID,
                Lesson.COLUMN_NAME_DATE,
                Lesson.COLUMN_NAME_AUDITORY,
                Lesson.COLUMN_NAME_KIND_OF_WORK,
                Lesson.COLUMN_NAME_DISCIPLINE,
                Lesson.COLUMN_NAME_LECTURER,
                Lesson.COLUMN_NAME_PAIR_NUMBER
        };
        String[] selectionArgs = {dateBegin, dateEnd};
        Map<String, Map<Integer, Map<String, String>>> schedule = new HashMap<>();
        Map<Integer, Map<String, String>> lessons = new HashMap<>();
        //Cursor cursor = dbRead.query(Lesson.TABLE_NAME, columns, ScheduleDBHelper.SQL_SELECT_BETWEEN_DATES, selectionArgs, null, null, null);
        Cursor cursor = dbRead.query(Lesson.TABLE_NAME, columns, null, null, null, null, null);

        System.out.println(cursor.getCount());

        //int LESSON_ID = cursor.getColumnIndex(Lesson.COLUMN_NAME_LESSON_ID);
        int DATE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DATE);
        int AUDITORY = cursor.getColumnIndex(Lesson.COLUMN_NAME_AUDITORY);
        int KIND_OF_WORK = cursor.getColumnIndex(Lesson.COLUMN_NAME_KIND_OF_WORK);
        int DISCIPLINE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DISCIPLINE);
        int LECTURER = cursor.getColumnIndex(Lesson.COLUMN_NAME_LECTURER);
        int PAIR_NUMBER = cursor.getColumnIndex(Lesson.COLUMN_NAME_PAIR_NUMBER);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Map<String, String> pair = new HashMap<>();
            pair.put(context.getResources().getString(R.string.map_name_lesson_auditory), cursor.getString(AUDITORY));
            pair.put(context.getResources().getString(R.string.map_name_lesson_discipline), cursor.getString(DISCIPLINE));
            pair.put(context.getResources().getString(R.string.map_name_lesson_kind_of_work), cursor.getString(KIND_OF_WORK));
            pair.put(context.getResources().getString(R.string.map_name_lesson_lecturer), cursor.getString(LECTURER));

            if (schedule.containsKey(cursor.getString(DATE))) {
                lessons = schedule.get(cursor.getString(DATE));
            } else {
                lessons = new HashMap<>();
            }
            lessons.put(cursor.getInt(PAIR_NUMBER), pair);
            schedule.put(cursor.getString(DATE), lessons);
        }

        cursor.close();
        return schedule;
    }

    public class ScheduleDBHelper extends SQLiteOpenHelper {
        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INTEGER";
        private static final String DATE_TYPE = " DATE";
        private static final String COMMA_SEP = ",";

        private static final String SQL_DELETE_BY_ID =
                Lesson.COLUMN_NAME_LESSON_ID + " = %1$d";
        private static final String SQL_DELETE_ALL_RECORDS =
                Lesson.COLUMN_NAME_LESSON_ID + " > 0";
        private static final String SQL_SELECT_BY_DATE =
                Lesson.COLUMN_NAME_DATE + " = ?";
        private static final String SQL_SELECT_BETWEEN_DATES =
                Lesson.COLUMN_NAME_DATE + " BETWEEN ? AND ?";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Lesson.TABLE_NAME + " (" +
                        Lesson.COLUMN_NAME_LESSON_ID + " INTEGER PRIMARY KEY," +
                        Lesson.COLUMN_NAME_DATE + DATE_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_PAIR_NUMBER + INT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_DISCIPLINE + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_LECTURER + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_AUDITORY + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_KIND_OF_WORK + TEXT_TYPE +
                " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Lesson.TABLE_NAME;

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "Schedule.db";

        public ScheduleDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    static abstract class Lesson implements BaseColumns {
        static final String TABLE_NAME = "lesson";
        static final String COLUMN_NAME_LESSON_ID = "lessonid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_PAIR_NUMBER = "pairnumber";
        static final String COLUMN_NAME_DISCIPLINE = "discipline";
        static final String COLUMN_NAME_LECTURER = "lecturer";
        static final String COLUMN_NAME_AUDITORY = "auditory";
        static final String COLUMN_NAME_KIND_OF_WORK = "kindofwork";
    }
}