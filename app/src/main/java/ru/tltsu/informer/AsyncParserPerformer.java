package ru.tltsu.informer;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Артем on 28.07.2016.
 */

public class AsyncParserPerformer extends AsyncTask<Object, Void, AsyncMainThreadTask>{

    public final int PARSE_LECTURERS = 1;
    public final int PARSE_GROUPS = 2;
    public final int PARSE_SCHEDULE = 3;
    public final int PARSE_FACULTIES = 4;

    @Override
    protected void onPostExecute(AsyncMainThreadTask s) {
        s.executeTask();
    }

    @Override
    protected AsyncMainThreadTask doInBackground(Object... params) {
        Context context = (Context)params[1];
        String response = (String)params[2];
        SingletonStorage storage = SingletonStorage.getInstance(context);
        switch ((Integer) params[0]) {
            case PARSE_FACULTIES:
                List<String> faculties = new ArrayList<>();
                for(Map<String, String> record : ServerResponseParser.parseServerFacultiesResponse(context, response)) {
                    storage.database.facultyAdapter.insert(
                            Integer.parseInt(record.get(context.getString(R.string.map_name_faculty_oid))),
                            record.get(context.getString(R.string.map_name_faculty_name)),
                            record.get(context.getString(R.string.map_name_faculty_abbr))
                    );
                    faculties.add(record.get(context.getString(R.string.map_name_faculty_abbr)));
                }
                storage.faculties = faculties;
                break;
            case PARSE_GROUPS:
                for(Map<String, String> record : ServerResponseParser.parseServerGroupsResponse(context, response)) {
                    storage.database.groupAdapter.insert(
                            record.get(context.getString(R.string.map_name_group_name)),
                            Integer.parseInt(record.get(context.getString(R.string.map_name_group_course))),
                            Integer.parseInt(record.get(context.getString(R.string.map_name_group_faculty))),
                            false
                    );
                }
                storage.groups = storage.database.groupAdapter.selectAllGroupsByFaculty(storage.currentFacultyOID);
                break;
            case PARSE_LECTURERS:
                List<String> groupsLecturersResponse = ServerResponseParser.parseServerLecturersResponse(context, response);
                for (String group : groupsLecturersResponse) {
                    storage.database.lecturerAdapter.insert(group, false);
                }
                storage.lecturers = groupsLecturersResponse;
                break;
            case PARSE_SCHEDULE:
                for(Map<String, String> record : ServerResponseParser.parseServerScheduleResponse(context, response)) {
                    storage.database.lessonAdapter.insert(
                            Integer.parseInt(record.get(context.getString(R.string.map_name_lesson_id))),
                            record.get(context.getString(R.string.map_name_lesson_date)),
                            record.get(context.getString(R.string.map_name_lesson_lecturer)),
                            record.get(context.getString(R.string.map_name_lesson_discipline)),
                            Integer.parseInt(record.get(context.getString(R.string.map_name_lesson_pair_number))),
                            record.get(context.getString(R.string.map_name_lesson_auditory)),
                            record.get(context.getString(R.string.map_name_lesson_kind_of_work)),
                            record.get(context.getString(R.string.map_name_lesson_group_abbr))
                    );
                }
                break;
            default:
                break;
        }
        return (AsyncMainThreadTask)params[3];
    }
}
