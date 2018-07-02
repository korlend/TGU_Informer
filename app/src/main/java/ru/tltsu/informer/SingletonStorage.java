package ru.tltsu.informer;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Артем on 17.07.2016.
 */

public class SingletonStorage {

    private static SingletonStorage instance = null;

    //public Map<String, Map<Integer, Map<String, String>>> schedule;
    public RequestQueue queue;
    public SimpleDateFormat formatToRequest;
    public ScheduleContract database;
    public String currentDateView;
    public String currentGroupView;
    public String currentLecturerView;
    public String currentFacultyView;
    public int currentFacultyOID;
    public List<String> faculties;
    public List<String> lecturers;
    public List<String> groups;
    public ServerCommunicator serverCommunicator;
    public Boolean lectorMode;

    private SingletonStorage(Context context) {
        //schedule = new HashMap<>();
        formatToRequest = new SimpleDateFormat(context.getResources().getString(R.string.date_format_to_server_request), Locale.ENGLISH);
        queue = Volley.newRequestQueue(context);
        database = new ScheduleContract(context);
        currentDateView = "";
        currentGroupView = "";
        currentLecturerView = "";
        currentFacultyView = "";
        currentFacultyOID = 1;
        groups = new ArrayList<>();
        lecturers = new ArrayList<>();
        faculties = new ArrayList<>();
        serverCommunicator = new ServerCommunicator(context);
        lectorMode = false;
    }

    public static SingletonStorage getInstance(Context context) {
        if (instance == null) {
            instance = getSync(context);
        }
        return instance;
    }

    private static synchronized SingletonStorage getSync(Context context) {
        if(instance == null) instance = new SingletonStorage(context);
        return instance;
    }

}
