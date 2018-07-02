package ru.tltsu.informer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Артем on 12.08.2016.
 */

public class LecturerAdapter {

    private final Context context;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    public LecturerAdapter(Context context, SQLiteDatabase dbWrite, SQLiteDatabase dbRead) {
        this.context = context;
        this.dbWrite = dbWrite;
        this.dbRead = dbRead;
    }

    public long insert(String name, boolean notify) {
        ContentValues values = new ContentValues();
        values.put(Lecturer.COLUMN_NAME_FIO, name);
        values.put(Lecturer.COLUMN_NAME_NOTIFY, notify);

        int id;
        if ((id = this.recordIdExists(name)) != 0) {
            //return update(id, name, notify);
            return 0;
        }
        return dbWrite.insert(Lecturer.TABLE_NAME, null, values);
    }

    public long update(int id, String name, boolean notify) {
        ContentValues values = new ContentValues();
        values.put(Lecturer.COLUMN_NAME_FIO, name);
        values.put(Lecturer.COLUMN_NAME_NOTIFY, notify);

        return dbWrite.update(Lecturer.TABLE_NAME, values, LecturerSQLHelper.SQL_UPDATE_BY_ID, new String[]{String.valueOf(id)});
    }

    public long delete(int id) {
        return dbWrite.delete(Lecturer.TABLE_NAME, String.format(Locale.ENGLISH, LecturerSQLHelper.SQL_DELETE_BY_ID, id), null);
    }

    public long deleteAllRecords() {
        return dbWrite.delete(Lecturer.TABLE_NAME, LecturerSQLHelper.SQL_DELETE_ALL_RECORDS, null);
    }

    public List<String> selectAllLecturers() {
        Cursor cursor = dbRead.query(Lecturer.TABLE_NAME, null, null, null, null, null, null);
        List<String> Lecturers = new ArrayList<>();
        int FIO = cursor.getColumnIndex(Lecturer.COLUMN_NAME_FIO);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Lecturers.add(cursor.getString(FIO));
        }

        cursor.close();
        return Lecturers;
    }

    public int recordIdExists(String name) {
        Cursor cursor = dbRead.query(Lecturer.TABLE_NAME, null, LecturerSQLHelper.SQL_SELECT_BY_ABBR, new String[]{name}, null, null, null);
        int name_id = 0;
        int ID = cursor.getColumnIndex(Lecturer.COLUMN_NAME_LECTURER_ID);
        if (cursor.moveToFirst()) {
            name_id = cursor.getInt(ID);
        }

        cursor.close();
        return name_id;
    }

    public class LecturerSQLHelper {
        static final String TEXT_TYPE = " TEXT";
        static final String INT_TYPE = " INTEGER";
        static final String DATE_TYPE = " DATE";
        static final String BOOL_TYPE = " BOOLEAN";
        static final String COMMA_SEP = ",";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Lecturer.TABLE_NAME + " (" +
                        Lecturer.COLUMN_NAME_LECTURER_ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                        Lecturer.COLUMN_NAME_FIO + TEXT_TYPE + COMMA_SEP +
                        Lecturer.COLUMN_NAME_NOTIFY + BOOL_TYPE +
                        " )";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Lecturer.TABLE_NAME;

        static final String SQL_DELETE_BY_ID =
                Lecturer.COLUMN_NAME_LECTURER_ID + " = %1$d";
        static final String SQL_DELETE_ALL_RECORDS =
                Lecturer.COLUMN_NAME_LECTURER_ID + " > 0";
        static final String SQL_SELECT_BY_ID =
                Lecturer.COLUMN_NAME_LECTURER_ID + " = ?";
        static final String SQL_SELECT_BY_ABBR =
                Lecturer.COLUMN_NAME_FIO + " = ?";
        static final String SQL_UPDATE_BY_ID =
                Lecturer.COLUMN_NAME_LECTURER_ID + " = ?";
    }

    static abstract class Lecturer implements BaseColumns {
        static final String TABLE_NAME = "lecturers";
        static final String COLUMN_NAME_LECTURER_ID = "lecturerid";
        static final String COLUMN_NAME_FIO = "fio";
        static final String COLUMN_NAME_NOTIFY = "notify";
    }
}
