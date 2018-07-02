package ru.tltsu.informer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private MainDBHelper mDbHelper;
    private final Context context;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;

    public GroupAdapter groupAdapter;
    public LecturerAdapter lecturerAdapter;
    public LessonAdapter lessonAdapter;
    public FacultyAdapter facultyAdapter;

    public ScheduleContract(Context context) {
        this.context = context;
        mDbHelper = new MainDBHelper(context);
        dbWrite = mDbHelper.getWritableDatabase();
        dbRead = mDbHelper.getReadableDatabase();

        lessonAdapter = new LessonAdapter(context, dbWrite, dbRead);
        groupAdapter = new GroupAdapter(context, dbWrite, dbRead);
        lecturerAdapter = new LecturerAdapter(context, dbWrite, dbRead);
        facultyAdapter = new FacultyAdapter(context, dbWrite, dbRead);
    }

    public class MainDBHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 2;
        private static final String DATABASE_NAME = "Schedule.db";

        public MainDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(LessonAdapter.LessonSQLHelper.SQL_CREATE_ENTRIES);
            db.execSQL(GroupAdapter.GroupSQLHelper.SQL_CREATE_ENTRIES);
            db.execSQL(LecturerAdapter.LecturerSQLHelper.SQL_CREATE_ENTRIES);
            db.execSQL(FacultyAdapter.FacultySQLHelper.SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            System.out.println("from onUpgrade database");
            switch(oldVersion) {
                case 1:
                case 2:
                    db.execSQL(LessonAdapter.LessonSQLHelper.SQL_DELETE_ENTRIES);
                    db.execSQL(GroupAdapter.GroupSQLHelper.SQL_DELETE_ENTRIES);
                    db.execSQL(LecturerAdapter.LecturerSQLHelper.SQL_DELETE_ENTRIES);
                    db.execSQL(FacultyAdapter.FacultySQLHelper.SQL_DELETE_ENTRIES);

                    onCreate(db);
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}