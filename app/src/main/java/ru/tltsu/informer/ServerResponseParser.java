package ru.tltsu.informer;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Артем on 12.07.2016.
 */

public class ServerResponseParser {
    /**
     * Парсит json из response добавляет данные из него в базу данных
     * @param context контекст приложения
     * @param database база данных
     * @param formatToRequest формат для даты e.g.(yyyy-MM-dd)
     * @param jsonString json строка
     */
    public static void parseServerResponse(Context context, ScheduleContract database, SimpleDateFormat formatToRequest, String jsonString) {
        try {
            JSONObject jsonobject;
            JSONArray jsonarray = new JSONArray(jsonString);
            String keyDate;
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonobject = jsonarray.getJSONObject(i);
                Calendar date = Calendar.getInstance();
                date.setTime(formatToRequest.parse(jsonobject.getString(context.getResources().getString(R.string.parse_server_response_start_time))));
                database.insert(
                        jsonobject.getInt(context.getResources().getString(R.string.parse_server_response_id)),
                        formatToRequest.format(date.getTime()),
                        jsonobject.getString(context.getResources().getString(R.string.parse_server_response_lecturer)),
                        jsonobject.getString(context.getResources().getString(R.string.parse_server_response_discipline)),
                        Integer.parseInt(jsonobject.getString(context.getResources().getString(R.string.parse_server_response_pair_number))),
                        jsonobject.getString(context.getResources().getString(R.string.parse_server_response_auditory)),
                        jsonobject.getString(context.getResources().getString(R.string.parse_server_response_kind_of_work))
                );
            }
            System.out.println("parse ended");
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}
