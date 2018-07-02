package ru.tltsu.informer;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Артем on 21.07.2016.
 */

public class ServerResponseParser {

    public static List<Map<String, String>> parseServerScheduleResponse(Context context, String jsonString) {

        try {
            SingletonStorage storage = SingletonStorage.getInstance(context);
            JSONObject jsonobject;
            JSONArray jsonarray = new JSONArray(jsonString);
            List<Map<String, String>> scheduleToReturn = new ArrayList<>();
            String[] mapNameLessonKeys = context.getResources().getStringArray(R.array.map_name_lesson_array);
            String[] serverResponseParams = context.getResources().getStringArray(R.array.parse_server_response_lesson_array);
            for (int i = 0; i < jsonarray.length(); i++) {
                Map<String, String> singleRecord = new HashMap<>();
                jsonobject = jsonarray.getJSONObject(i);
                Calendar date = Calendar.getInstance();
                date.setTime(storage.formatToRequest.parse(jsonobject.getString(context.getResources().getString(R.string.parse_server_response_lesson_start_time))));

                for (int j = 0; j < mapNameLessonKeys.length; j++) {
                    singleRecord.put(mapNameLessonKeys[j], jsonobject.getString(serverResponseParams[j]));
                }

                singleRecord.put(context.getString(R.string.map_name_lesson_date), storage.formatToRequest.format(date.getTime()));
                /*
                singleRecord.put(context.getString(R.string.map_name_lesson_id), context.getString(R.string.parse_server_response_id));
                singleRecord.put(context.getString(R.string.map_name_lesson_auditory), context.getString(R.string.parse_server_response_auditory));
                singleRecord.put(context.getString(R.string.map_name_lesson_lecturer), context.getString(R.string.parse_server_response_lecturer));
                singleRecord.put(context.getString(R.string.map_name_lesson_discipline), context.getString(R.string.parse_server_response_discipline));
                singleRecord.put(context.getString(R.string.map_name_lesson_pair_number), context.getString(R.string.parse_server_response_pair_number));
                singleRecord.put(context.getString(R.string.map_name_lesson_kind_of_work), context.getString(R.string.parse_server_response_kind_of_work));
                singleRecord.put(context.getString(R.string.map_name_lesson_group_abbr), context.getString(R.string.parse_server_response_group_abbr));
                singleRecord.put(context.getString(R.string.map_name_lesson_id), context.getString(R.string.parse_server_response_id));
                */
                scheduleToReturn.add(singleRecord);
            }
            return scheduleToReturn;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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
    public static  List<Map<String, String>> parseServerGroupsResponse(Context context, String jsonString) {
        try {
            JSONObject jsonobject;
            JSONArray jsonarray = new JSONArray(jsonString);
            List<Map<String, String>> recordsToReturn = new ArrayList<>();
            String[] mapNameLessonKeys = context.getResources().getStringArray(R.array.map_name_group_array);
            String[] serverResponseParams = context.getResources().getStringArray(R.array.parse_server_response_group_array);
            for (int i = 0; i < jsonarray.length(); i++) {
                Map<String, String> singleRecord = new HashMap<>();
                jsonobject = jsonarray.getJSONObject(i);
                for (int j = 0; j < mapNameLessonKeys.length; j++) {
                    singleRecord.put(mapNameLessonKeys[j], jsonobject.getString(serverResponseParams[j]));
                }
                recordsToReturn.add(singleRecord);
            }
            return recordsToReturn;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<String> parseServerLecturersResponse(Context context, String jsonString) {
        try {
            JSONArray jsonarray = new JSONArray(jsonString);
            List<String> groupsListToReturn = new ArrayList<>();
            for (int i = 0; i < jsonarray.length(); i++) {
                groupsListToReturn.add(jsonarray.getString(i));
            }
            return groupsListToReturn;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static  List<Map<String, String>> parseServerFacultiesResponse(Context context, String jsonString) {
        try {
            JSONObject jsonobject;
            JSONArray jsonarray = new JSONArray(jsonString);
            List<Map<String, String>> recordsToReturn = new ArrayList<>();
            String[] mapNameLessonKeys = context.getResources().getStringArray(R.array.map_name_faculty_array);
            String[] serverResponseParams = context.getResources().getStringArray(R.array.parse_server_response_faculty_array);
            for (int i = 0; i < jsonarray.length(); i++) {
                Map<String, String> singleRecord = new HashMap<>();
                jsonobject = jsonarray.getJSONObject(i);
                for (int j = 0; j < mapNameLessonKeys.length; j++) {
                    singleRecord.put(mapNameLessonKeys[j], jsonobject.getString(serverResponseParams[j]));
                }
                recordsToReturn.add(singleRecord);
            }
            return recordsToReturn;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
