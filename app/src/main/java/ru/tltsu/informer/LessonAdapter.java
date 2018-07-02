package ru.tltsu.informer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Артем on 12.08.2016.
 */

public class LessonAdapter {

    private final Context context;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    public LessonAdapter(Context context, SQLiteDatabase dbWrite, SQLiteDatabase dbRead) {
        this.context = context;
        this.dbWrite = dbWrite;
        this.dbRead = dbRead;
    }

    public long insert(int id, String date, String lecturer, String discipline, int pair_number, String auditory, String kindofwork, String group) {
        ContentValues values = new ContentValues();
        values.put(Lesson.COLUMN_NAME_LESSON_ID, id);
        values.put(Lesson.COLUMN_NAME_DATE, date);
        values.put(Lesson.COLUMN_NAME_AUDITORY, auditory);
        values.put(Lesson.COLUMN_NAME_DISCIPLINE, discipline);
        values.put(Lesson.COLUMN_NAME_KIND_OF_WORK, kindofwork);
        values.put(Lesson.COLUMN_NAME_LECTURER, lecturer);
        values.put(Lesson.COLUMN_NAME_PAIR_NUMBER, pair_number);
        values.put(Lesson.COLUMN_NAME_GROUP_ABBR, group);

        if (this.recordIdExists(id)) {
            //System.out.println(id);
            return update(id, date, lecturer, discipline, pair_number, auditory, kindofwork, group);
        }
        return dbWrite.insert(Lesson.TABLE_NAME, null, values);
    }

    public long update(int id, String date, String lecturer, String discipline, int pair_number, String auditory, String kindofwork, String group) {
        ContentValues values = new ContentValues();
        //values.put(Lesson.COLUMN_NAME_LESSON_ID, id);
        values.put(Lesson.COLUMN_NAME_DATE, date);
        values.put(Lesson.COLUMN_NAME_AUDITORY, auditory);
        values.put(Lesson.COLUMN_NAME_DISCIPLINE, discipline);
        values.put(Lesson.COLUMN_NAME_KIND_OF_WORK, kindofwork);
        values.put(Lesson.COLUMN_NAME_LECTURER, lecturer);
        values.put(Lesson.COLUMN_NAME_PAIR_NUMBER, pair_number);
        values.put(Lesson.COLUMN_NAME_GROUP_ABBR, group);

        return dbWrite.update(Lesson.TABLE_NAME, values, LessonSQLHelper.SQL_UPDATE_BY_ID, new String[]{String.valueOf(id)});
    }

    public long delete(int id) {
        return dbWrite.delete(Lesson.TABLE_NAME, String.format(Locale.ENGLISH, LessonSQLHelper.SQL_DELETE_BY_ID, id), null);
    }

    public long deleteAllRecords() {
        return dbWrite.delete(Lesson.TABLE_NAME, LessonSQLHelper.SQL_DELETE_ALL_RECORDS, null);
    }

    public Map<Integer, Map<String, String>> selectLecturerLessonsByDate(String date, String lecturer) {
        try {
            Calendar cDate = Calendar.getInstance();
            SimpleDateFormat formatToRequest = new SimpleDateFormat(context.getResources().getString(R.string.date_format_to_server_request), Locale.ENGLISH);
            cDate.setTime(formatToRequest.parse(date));
            date = formatToRequest.format(cDate.getTime());
        } catch (ParseException e) {e.printStackTrace();}

        String[] columns = {
                Lesson.COLUMN_NAME_LESSON_ID,
                Lesson.COLUMN_NAME_DATE,
                Lesson.COLUMN_NAME_AUDITORY,
                Lesson.COLUMN_NAME_KIND_OF_WORK,
                Lesson.COLUMN_NAME_DISCIPLINE,
                Lesson.COLUMN_NAME_LECTURER,
                Lesson.COLUMN_NAME_PAIR_NUMBER,
                Lesson.COLUMN_NAME_GROUP_ABBR
        };
        String[] selectionArgs = {date, lecturer};
        Map<Integer, Map<String, String>> lessons = new HashMap<>();

        Cursor cursor = dbRead.query(Lesson.TABLE_NAME, null, LessonSQLHelper.SQL_SELECT_LECTURER_BY_DATE, selectionArgs, null, null, null);

        //int LESSON_ID = cursor.getColumnIndex(Lesson.COLUMN_NAME_LESSON_ID);
        //int DATE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DATE);
        int AUDITORY = cursor.getColumnIndex(Lesson.COLUMN_NAME_AUDITORY);
        int KIND_OF_WORK = cursor.getColumnIndex(Lesson.COLUMN_NAME_KIND_OF_WORK);
        int DISCIPLINE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DISCIPLINE);
        int LECTURER = cursor.getColumnIndex(Lesson.COLUMN_NAME_LECTURER);
        int PAIR_NUMBER = cursor.getColumnIndex(Lesson.COLUMN_NAME_PAIR_NUMBER);
        int GROUP = cursor.getColumnIndex(Lesson.COLUMN_NAME_GROUP_ABBR);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Map<String, String> pair = new HashMap<>();
            pair.put(context.getResources().getString(R.string.map_name_lesson_auditory), cursor.getString(AUDITORY));
            pair.put(context.getResources().getString(R.string.map_name_lesson_discipline), cursor.getString(DISCIPLINE));
            pair.put(context.getResources().getString(R.string.map_name_lesson_kind_of_work), cursor.getString(KIND_OF_WORK));
            pair.put(context.getResources().getString(R.string.map_name_lesson_lecturer), cursor.getString(LECTURER));
            pair.put(context.getResources().getString(R.string.map_name_lesson_group_abbr), cursor.getString(GROUP));

            lessons.put(cursor.getInt(PAIR_NUMBER), pair);
        }

        cursor.close();
        return lessons;
        //return new HashMap<>();
    }

    public Map<Integer, Map<String, String>> selectStudentLessonsByDate(String date, String group) {
        try {
            Calendar cDate = Calendar.getInstance();
            SimpleDateFormat formatToRequest = new SimpleDateFormat(context.getResources().getString(R.string.date_format_to_server_request), Locale.ENGLISH);
            cDate.setTime(formatToRequest.parse(date));
            date = formatToRequest.format(cDate.getTime());
        } catch (ParseException e) {e.printStackTrace();}

        String[] columns = {
                Lesson.COLUMN_NAME_LESSON_ID,
                Lesson.COLUMN_NAME_DATE,
                Lesson.COLUMN_NAME_AUDITORY,
                Lesson.COLUMN_NAME_KIND_OF_WORK,
                Lesson.COLUMN_NAME_DISCIPLINE,
                Lesson.COLUMN_NAME_LECTURER,
                Lesson.COLUMN_NAME_PAIR_NUMBER,
                Lesson.COLUMN_NAME_GROUP_ABBR
        };
        String[] selectionArgs = {date, group};
        Map<Integer, Map<String, String>> lessons = new HashMap<>();

        Cursor cursor = dbRead.query(Lesson.TABLE_NAME, null, LessonSQLHelper.SQL_SELECT_GROUP_BY_DATE, selectionArgs, null, null, null);

        //int LESSON_ID = cursor.getColumnIndex(Lesson.COLUMN_NAME_LESSON_ID);
        //int DATE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DATE);
        int AUDITORY = cursor.getColumnIndex(Lesson.COLUMN_NAME_AUDITORY);
        int KIND_OF_WORK = cursor.getColumnIndex(Lesson.COLUMN_NAME_KIND_OF_WORK);
        int DISCIPLINE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DISCIPLINE);
        int LECTURER = cursor.getColumnIndex(Lesson.COLUMN_NAME_LECTURER);
        int PAIR_NUMBER = cursor.getColumnIndex(Lesson.COLUMN_NAME_PAIR_NUMBER);
        int GROUP = cursor.getColumnIndex(Lesson.COLUMN_NAME_GROUP_ABBR);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Map<String, String> pair = new HashMap<>();
            pair.put(context.getResources().getString(R.string.map_name_lesson_auditory), cursor.getString(AUDITORY));
            pair.put(context.getResources().getString(R.string.map_name_lesson_discipline), cursor.getString(DISCIPLINE));
            pair.put(context.getResources().getString(R.string.map_name_lesson_kind_of_work), cursor.getString(KIND_OF_WORK));
            pair.put(context.getResources().getString(R.string.map_name_lesson_lecturer), cursor.getString(LECTURER));
            pair.put(context.getResources().getString(R.string.map_name_lesson_group_abbr), cursor.getString(GROUP));

            lessons.put(cursor.getInt(PAIR_NUMBER), pair);
        }

        cursor.close();
        return lessons;
        //return new HashMap<>();
    }

    public Map<String, Map<Integer, Map<String, String>>> selectScheduleByDates(String dateBegin, String dateEnd, String group) {
        try {
            Calendar cDateBegin = Calendar.getInstance();
            Calendar cDateEnd = Calendar.getInstance();
            SimpleDateFormat formatToRequest = new SimpleDateFormat(context.getResources().getString(R.string.date_format_to_server_request), Locale.ENGLISH);
            cDateBegin.setTime(formatToRequest.parse(dateBegin));
            cDateEnd.setTime(formatToRequest.parse(dateEnd));
            dateBegin = formatToRequest.format(cDateBegin.getTime());
            dateEnd = formatToRequest.format(cDateEnd.getTime());
        } catch (ParseException e) {e.printStackTrace();}
        String[] columns = {
                Lesson.COLUMN_NAME_LESSON_ID,
                Lesson.COLUMN_NAME_DATE,
                Lesson.COLUMN_NAME_AUDITORY,
                Lesson.COLUMN_NAME_KIND_OF_WORK,
                Lesson.COLUMN_NAME_DISCIPLINE,
                Lesson.COLUMN_NAME_LECTURER,
                Lesson.COLUMN_NAME_PAIR_NUMBER,
                Lesson.COLUMN_NAME_GROUP_ABBR
        };
        String[] selectionArgs = {dateBegin, dateEnd, group};
        Map<String, Map<Integer, Map<String, String>>> schedule = new HashMap<>();
        Map<Integer, Map<String, String>> lessons = new HashMap<>();
        Cursor cursor = dbRead.query(Lesson.TABLE_NAME, columns, LessonSQLHelper.SQL_SELECT_GROUP_BETWEEN_DATES, selectionArgs, null, null, null);
        //Cursor cursor = dbRead.query(Lesson.TABLE_NAME, columns, null, null, null, null, null);

        //int LESSON_ID = cursor.getColumnIndex(Lesson.COLUMN_NAME_LESSON_ID);
        int DATE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DATE);
        int AUDITORY = cursor.getColumnIndex(Lesson.COLUMN_NAME_AUDITORY);
        int KIND_OF_WORK = cursor.getColumnIndex(Lesson.COLUMN_NAME_KIND_OF_WORK);
        int DISCIPLINE = cursor.getColumnIndex(Lesson.COLUMN_NAME_DISCIPLINE);
        int LECTURER = cursor.getColumnIndex(Lesson.COLUMN_NAME_LECTURER);
        int PAIR_NUMBER = cursor.getColumnIndex(Lesson.COLUMN_NAME_PAIR_NUMBER);
        int GROUP = cursor.getColumnIndex(Lesson.COLUMN_NAME_GROUP_ABBR);

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

    public boolean recordIdExists(int id) {
        return dbRead.query(Lesson.TABLE_NAME, null, LessonSQLHelper.SQL_SELECT_BY_ID, new String[]{String.valueOf(id)}, null, null, null).getCount() > 0;
    }

    public class LessonSQLHelper {
        static final String TEXT_TYPE = " TEXT";
        static final String INT_TYPE = " INTEGER";
        static final String DATE_TYPE = " DATE";
        static final String COMMA_SEP = ",";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Lesson.TABLE_NAME + " (" +
                        Lesson.COLUMN_NAME_LESSON_ID + " INTEGER PRIMARY KEY," +
                        Lesson.COLUMN_NAME_DATE + DATE_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_PAIR_NUMBER + INT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_DISCIPLINE + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_LECTURER + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_AUDITORY + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_KIND_OF_WORK + TEXT_TYPE + COMMA_SEP +
                        Lesson.COLUMN_NAME_GROUP_ABBR + TEXT_TYPE +
                        " )";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Lesson.TABLE_NAME;

        static final String SQL_DELETE_BY_ID =
                Lesson.COLUMN_NAME_LESSON_ID + " = %1$d";
        static final String SQL_DELETE_ALL_RECORDS =
                Lesson.COLUMN_NAME_LESSON_ID + " > 0";
        static final String SQL_SELECT_GROUP_BY_DATE =
                Lesson.COLUMN_NAME_DATE + " = ? AND " + Lesson.COLUMN_NAME_GROUP_ABBR + " = ?";
        static final String SQL_SELECT_LECTURER_BY_DATE =
                Lesson.COLUMN_NAME_DATE + " = ? AND " + Lesson.COLUMN_NAME_LECTURER + " = ?";
        static final String SQL_SELECT_BY_ID =
                Lesson.COLUMN_NAME_LESSON_ID + " = ?";
        static final String SQL_SELECT_GROUP_BETWEEN_DATES =
                Lesson.COLUMN_NAME_DATE + " BETWEEN ? AND ? AND" + Lesson.COLUMN_NAME_GROUP_ABBR + " = ?";
        static final String SQL_UPDATE_BY_ID =
                Lesson.COLUMN_NAME_LESSON_ID + " = ?";
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
        static final String COLUMN_NAME_GROUP_ABBR = "groupname";
    }
}
