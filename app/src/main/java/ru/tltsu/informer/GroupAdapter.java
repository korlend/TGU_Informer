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



public class GroupAdapter {

    private final Context context;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    public GroupAdapter(Context context, SQLiteDatabase dbWrite, SQLiteDatabase dbRead) {
        this.context = context;
        this.dbWrite = dbWrite;
        this.dbRead = dbRead;
    }

    public long insert(String name, int course, int facultyoid, boolean notify) {
        ContentValues values = new ContentValues();
        values.put(Group.COLUMN_NAME_ABBR, name);
        values.put(Group.COLUMN_NAME_NOTIFY, notify);
        values.put(Group.COLUMN_NAME_COURSE, course);
        values.put(Group.COLUMN_NAME_FACULTY, facultyoid);

        int id;
        if ((id = this.recordIdExists(name)) != 0) {
            //return update(id, name, notify);
            return 0;
        }
        return dbWrite.insert(Group.TABLE_NAME, null, values);
    }

    public long update(int id, String name, int course, int facultyoid, boolean notify) {
        ContentValues values = new ContentValues();
        values.put(Group.COLUMN_NAME_ABBR, name);
        values.put(Group.COLUMN_NAME_NOTIFY, notify);
        values.put(Group.COLUMN_NAME_COURSE, course);
        values.put(Group.COLUMN_NAME_FACULTY, facultyoid);

        return dbWrite.update(Group.TABLE_NAME, values, GroupSQLHelper.SQL_UPDATE_BY_ID, new String[]{String.valueOf(id)});
    }

    public long delete(int id) {
        return dbWrite.delete(Group.TABLE_NAME, String.format(Locale.ENGLISH, GroupSQLHelper.SQL_DELETE_BY_ID, id), null);
    }

    public long deleteAllRecords() {
        return dbWrite.delete(Group.TABLE_NAME, GroupSQLHelper.SQL_DELETE_ALL_RECORDS, null);
    }

    public Map<String, Boolean> selectAllGroups() {
        Cursor cursor = dbRead.query(Group.TABLE_NAME, null, null, null, null, null, null);
        Map<String, Boolean> groups = new HashMap<>();
        int ABBR = cursor.getColumnIndex(Group.COLUMN_NAME_ABBR);
        int NOTIFY = cursor.getColumnIndex(Group.COLUMN_NAME_NOTIFY);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groups.put(cursor.getString(ABBR), cursor.getInt(NOTIFY) != 0);
        }

        cursor.close();
        return groups;
    }

    public List<String> selectAllGroupsByFacultyAndCourse(int facultyoid, int course) {
        Cursor cursor = dbRead.query(Group.TABLE_NAME, null, GroupSQLHelper.SQL_SELECT_BY_FACULTY_AND_COURSE,
                new String[]{Integer.toString(facultyoid), Integer.toString(course)}, null, null, null);
        List<String> groups = new ArrayList<>();
        int ABBR = cursor.getColumnIndex(Group.COLUMN_NAME_ABBR);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groups.add(cursor.getString(ABBR));
        }

        cursor.close();
        return groups;
    }

    public List<String> selectAllGroupsByFaculty(int facultyoid) {
        Cursor cursor = dbRead.query(Group.TABLE_NAME, null, GroupSQLHelper.SQL_SELECT_BY_FACULTY,
                new String[]{Integer.toString(facultyoid)}, null, null, "abbr");
        List<String> groups = new ArrayList<>();
        int ABBR = cursor.getColumnIndex(Group.COLUMN_NAME_ABBR);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groups.add(cursor.getString(ABBR));
        }

        cursor.close();
        return groups;
    }

    public int recordIdExists(String name) {
        Cursor cursor = dbRead.query(Group.TABLE_NAME, null, GroupSQLHelper.SQL_SELECT_BY_ABBR, new String[]{name}, null, null, null);
        int name_id = 0;
        int ID = cursor.getColumnIndex(Group.COLUMN_NAME_GROUP_ID);
        if (cursor.moveToFirst()) {
            name_id = cursor.getInt(ID);
        }

        cursor.close();
        return name_id;
    }

    public class GroupSQLHelper {
        static final String TEXT_TYPE = " TEXT";
        static final String INT_TYPE = " INTEGER";
        static final String DATE_TYPE = " DATE";
        static final String BOOL_TYPE = " BOOLEAN";
        static final String COMMA_SEP = ",";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Group.TABLE_NAME + " (" +
                        Group.COLUMN_NAME_GROUP_ID + INT_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                        Group.COLUMN_NAME_ABBR + TEXT_TYPE + COMMA_SEP +
                        Group.COLUMN_NAME_COURSE + INT_TYPE + COMMA_SEP +
                        Group.COLUMN_NAME_FACULTY + INT_TYPE + COMMA_SEP +
                        Group.COLUMN_NAME_NOTIFY + BOOL_TYPE +
                        " )";

        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Group.TABLE_NAME;

        static final String SQL_DELETE_BY_ID =
                Group.COLUMN_NAME_GROUP_ID + " = %1$d";
        static final String SQL_DELETE_ALL_RECORDS =
                Group.COLUMN_NAME_GROUP_ID + " > 0";
        static final String SQL_SELECT_BY_ID =
                Group.COLUMN_NAME_GROUP_ID + " = ?";
        static final String SQL_SELECT_BY_FACULTY =
                Group.COLUMN_NAME_FACULTY + " = ?";
        static final String SQL_SELECT_BY_FACULTY_AND_COURSE =
                Group.COLUMN_NAME_FACULTY + " = ? and " + Group.COLUMN_NAME_COURSE + " = ?";
        static final String SQL_SELECT_BY_ABBR =
                Group.COLUMN_NAME_ABBR + " = ?";
        static final String SQL_UPDATE_BY_ID =
                Group.COLUMN_NAME_GROUP_ID + " = ?";
    }

    /**
    {
        "name":"Ð­ÐÐ±Ð·-1101",
        "course":5,
        "oid":2,
        "uniname":"2",
        "faculty":2
    }
    */

    static abstract class Group implements BaseColumns {
        static final String TABLE_NAME = "groups";
        static final String COLUMN_NAME_GROUP_ID = "groupid";
        static final String COLUMN_NAME_ABBR = "abbr";
        static final String COLUMN_NAME_COURSE = "course";
        static final String COLUMN_NAME_FACULTY = "facultyoid";
        static final String COLUMN_NAME_NOTIFY = "notify";
    }
}
