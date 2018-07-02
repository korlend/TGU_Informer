package ru.tltsu.informer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Артем on 12.07.2016.
 */

public class ServerCommunicator {
    Context context;

    public ServerCommunicator(Context context) {
        this.context = context;
    }

    public void getScheduleFromServer(boolean lectorMode, String groupLector, String dateBegin, String dateEnd, final VolleyCallback callback) {
        String url;
        if (lectorMode) {
            groupLector = groupLector.replaceAll(" ", "%20");
            url = String.format(
                    this.context.getResources().getString(R.string.url_server_data_get_lecturer_schedule_by_date_range),
                    groupLector,
                    dateBegin,
                    dateEnd
            );
        }
        else
            url = String.format(
                    this.context.getResources().getString(R.string.url_server_data_get_by_date_range),
                    groupLector,
                    dateBegin,
                    dateEnd
            );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        SingletonStorage.getInstance(context).queue.add(stringRequest);
    }

    public void getFacultiesFromServer(final VolleyCallback callback) {
        String url;
        url = this.context.getResources().getString(R.string.url_server_data_get_all_faculties);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        SingletonStorage.getInstance(context).queue.add(stringRequest);
    }

    public void getGroupsFromServer(final VolleyCallback callback) {
        String url;
        url = this.context.getResources().getString(R.string.url_server_data_get_all_groups);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        SingletonStorage.getInstance(context).queue.add(stringRequest);
    }

    public void getLecturersFromServer(final VolleyCallback callback) {
        String url;
        url = this.context.getResources().getString(R.string.url_server_data_get_all_lecturers);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        SingletonStorage.getInstance(context).queue.add(stringRequest);
    }
}
