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
 * Created by Артем on 06.09.2016.
 */



public class FacultyAdapter {

    private final Context context;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    public FacultyAdapter(Context context, SQLiteDatabase dbWrite, SQLiteDatabase dbRead) {
        this.context = context;
        this.dbWrite = dbWrite;
        this.dbRead = dbRead;
    }

    public long insert(int OID, String name, String abbr) {
        ContentValues values = new ContentValues();
        values.put(Faculty.COLUMN_NAME_NAME, name);
        values.put(Faculty.COLUMN_NAME_ABBR, abbr);

        if (this.recordIdExists(OID) != 0) {
            //return update(OID, name, abbr);
            return 0;
        }
        return dbWrite.insert(Faculty.TABLE_NAME, null, values);
    }

    public long update(int id, String name, String abbr) {
        ContentValues values = new ContentValues();
        values.put(Faculty.COLUMN_NAME_NAME, name);
        values.put(Faculty.COLUMN_NAME_ABBR, abbr);

        return dbWrite.update(Faculty.TABLE_NAME, values, FacultySQLHelper.SQL_UPDATE_BY_ID, new String[]{String.valueOf(id)});
    }

    public long delete(int id) {
        return dbWrite.delete(Faculty.TABLE_NAME, String.format(Locale.ENGLISH, FacultySQLHelper.SQL_DELETE_BY_ID, id), null);
    }

    public long deleteAllRecords() {
        return dbWrite.delete(Faculty.TABLE_NAME, FacultySQLHelper.SQL_DELETE_ALL_RECORDS, null);
    }

    public List<String> selectAllFaculties() {
        Cursor cursor = dbRead.query(Faculty.TABLE_NAME, null, null, null, null, null, null);
        List<String> Faculties = new ArrayList<>();
        int ABBR = cursor.getColumnIndex(Faculty.COLUMN_NAME_ABBR);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Faculties.add(cursor.getString(ABBR));
        }

        cursor.close();
        return Faculties;
    }

    public int selectFacultyOIDByAbbr(String abbr) {
        Cursor cursor = dbRead.query(Faculty.TABLE_NAME, null, FacultySQLHelper.SQL_SELECT_BY_ABBR, new String[]{abbr}, null, null, null);
        int faculty_id = 0;
        int OID = cursor.getColumnIndex(Faculty.COLUMN_NAME_FACULTY_ID);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            faculty_id = cursor.getInt(OID);
        }

        cursor.close();
        return faculty_id;
    }

    public int recordIdExists(int OID) {
        Cursor cursor = dbRead.query(Faculty.TABLE_NAME, null, FacultySQLHelper.SQL_SELECT_BY_ID, new String[]{Integer.toString(OID)}, null, null, null);
        int name_id = 0;
        int ID = cursor.getColumnIndex(Faculty.COLUMN_NAME_FACULTY_ID);
        if (cursor.moveToFirst()) {
            name_id = cursor.getInt(ID);
        }

        cursor.close();
        return name_id;
    }

    public class FacultySQLHelper {
        static final String TEXT_TYPE = " TEXT";
        static final String INT_TYPE = " INTEGER";
        static final String DATE_TYPE = " DATE";
        static final String BOOL_TYPE = " BOOLEAN";
        static final String COMMA_SEP = ",";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Faculty.TABLE_NAME + " (" +
                        Faculty.COLUMN_NAME_FACULTY_ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                        Faculty.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        Faculty.COLUMN_NAME_ABBR + TEXT_TYPE +
                        " )";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Faculty.TABLE_NAME;

        static final String SQL_DELETE_BY_ID =
                Faculty.COLUMN_NAME_FACULTY_ID + " = %1$d";
        static final String SQL_SELECT_BY_ID =
                Faculty.COLUMN_NAME_FACULTY_ID + " = ?";
        static final String SQL_SELECT_BY_ABBR =
                Faculty.COLUMN_NAME_ABBR + " = ?";
        static final String SQL_DELETE_ALL_RECORDS =
                Faculty.COLUMN_NAME_FACULTY_ID + " > 0";
        static final String SQL_UPDATE_BY_ID =
                Faculty.COLUMN_NAME_FACULTY_ID + " = ?";
    }

    /**
        {
            "name":"ÐÐ½ÑÑÐ¸ÑÑÑ Ð²Ð¾ÐµÐ½Ð½Ð¾Ð³Ð¾ Ð¾Ð±ÑÑÐµÐ½Ð¸Ñ",
            "oid":1,
            "uniname":"1",
            "abbr":"ÐÐ"
        }
    */

    static abstract class Faculty implements BaseColumns {
        static final String TABLE_NAME = "faculties";
        static final String COLUMN_NAME_FACULTY_ID = "oid";
        static final String COLUMN_NAME_ABBR = "abbr";
        static final String COLUMN_NAME_NAME = "name";
    }
}
