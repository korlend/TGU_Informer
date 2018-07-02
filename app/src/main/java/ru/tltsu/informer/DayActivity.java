package ru.tltsu.informer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/*

использовать только группы и преподов где больше одной записи в расписаниях

 */

public class DayActivity extends AppCompatActivity implements View.OnClickListener {
    SingletonStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storage = SingletonStorage.getInstance(this);
        this.createSpinners();


        /* init start */

        storage.currentFacultyOID = 12;
        storage.currentFacultyView = "ИМФИТ";
        storage.currentGroupView = "МОб-1201";
        storage.currentLecturerView = "Вракова Мария Германовна";
        setDate(2015, 9, 15);//"2015-09-15";

        //storage.database.deleteAllRecords();


        getScheduleBetweenDates("МОб-1201", "2015-09-13", "2015-09-19");
        //fillDay(schedule.get("2015-09-15"));


        /* init end */
        sync();
/*
        storage.serverCommunicator.getScheduleFromServer(
                storage.currentGroupView,
                "2015-08-00",
                "2015-10-00",
                getCurrentDayCallback()
        );
*/

    }

    @Override
    public void onClick(View v) {
        System.out.println("onClick signal");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_preferences:


                break;

            case R.id.menu_sync:
                sync();
                break;

            case R.id.menu_lecturer_mode:
                if (!storage.lectorMode) {
                    Log.i("test", "teacher now");
                    storage.lectorMode = true;
                    storage.currentLecturerView = "Вракова Мария Германовна";

                    sync();
                }
                break;

            case R.id.menu_student_mode:
                if (storage.lectorMode) {
                    Log.i("test", "student now");
                    storage.lectorMode = false;
                    storage.currentGroupView = "МОб-1201";
                    sync();
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void sync() {
        showSchedule();
        //updateGroupLecturersListSpinnerItems();
        storage.serverCommunicator.getFacultiesFromServer(getFacultiesListCallback());
        storage.serverCommunicator.getLecturersFromServer(getLecturersListCallback());
        storage.serverCommunicator.getGroupsFromServer(getGroupsListCallback());

        storage.groups = storage.database.groupAdapter.selectAllGroupsByFaculty(storage.currentFacultyOID);
        storage.lecturers = storage.database.lecturerAdapter.selectAllLecturers();
        storage.faculties = storage.database.facultyAdapter.selectAllFaculties();

        Log.i("sync", storage.faculties.toString());

        updateGroupLecturerListSpinnerItems();
        updateFacultyListSpinnerItems();
    }

    private void ModeSwitch() {
        if (storage.lectorMode) {
            Log.i("test", "student now");
            storage.lectorMode = false;
            sync();
            storage.currentGroupView = "МОб-1201";
        } else {
            Log.i("test", "student now");
            storage.lectorMode = true;
            sync();
            storage.currentLecturerView = "Вракова Мария Германовна";
        }
    }

    private void showSchedule() {
        if (!storage.lectorMode)
            fillDay(storage.database.lessonAdapter.selectStudentLessonsByDate(storage.currentDateView, storage.currentGroupView));
        else
            fillDay(storage.database.lessonAdapter.selectLecturerLessonsByDate(storage.currentDateView, storage.currentLecturerView));
    }

    private void createSpinners() {
        Spinner s = (Spinner) findViewById(R.id.spinner_group_selection);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                storage.lectorMode ? storage.lecturers : storage.groups
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        if (storage.groups.size() != 0 && storage.lecturers.size() != 0 ) {
            if (!storage.lectorMode)
                s.setSelection(storage.groups.indexOf(storage.currentGroupView));
            else
                s.setSelection(storage.lecturers.indexOf(storage.currentLecturerView));
        }
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (storage.groups.size() != 0 && storage.lecturers.size() != 0 ) {
                    if (!storage.lectorMode)
                        storage.currentGroupView = storage.groups.get(position);
                    else
                        storage.currentLecturerView = storage.lecturers.get(position);
                }
                showSchedule();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        /**
         * faculty spinner
         */
        s = (Spinner) findViewById(R.id.spinner_faculty_selection);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                storage.faculties
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        if (storage.faculties.size() != 0) {
            s.setSelection(storage.faculties.indexOf(storage.currentFacultyView));
        }
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (storage.faculties.size() != 0) {
                    if (!storage.lectorMode) {
                        storage.currentFacultyView = storage.faculties.get(position);
                        storage.currentFacultyOID = storage.database.facultyAdapter.selectFacultyOIDByAbbr(storage.currentFacultyView);
                        storage.groups = storage.database.groupAdapter.selectAllGroupsByFaculty(storage.currentFacultyOID);
                        updateGroupLecturerListSpinnerItems();
                    }
                    else
                        storage.currentLecturerView = storage.lecturers.get(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void updateGroupLecturerListSpinnerItems() {
        Spinner s = (Spinner) findViewById(R.id.spinner_group_selection);
        ArrayAdapter<String> adapter;
        if (!storage.lectorMode) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storage.groups);
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storage.lecturers);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        if (storage.groups.size() != 0 && storage.lecturers.size() != 0 ) {
            if (!storage.lectorMode) {
                s.setSelection(0);
            } else {
                s.setSelection(storage.lecturers.indexOf(storage.currentLecturerView));
            }
        }
    }

    private void updateFacultyListSpinnerItems() {
        Spinner s = (Spinner) findViewById(R.id.spinner_faculty_selection);
        ArrayAdapter<String> adapter;
        if (!storage.lectorMode) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storage.faculties);
        } else {
            return;
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        if (storage.faculties.size() != 0) {
            if (!storage.lectorMode) {
                s.setSelection(storage.faculties.indexOf(storage.currentFacultyView));
            } else {
                s.setSelection(storage.lecturers.indexOf(storage.currentLecturerView));
            }
        }
    }

    private void setCurrentDateView() {

    }

    public void fillDay(Map<Integer, Map<String, String>> dayData) {

        //storage.database.selectStudentLessonsByDate(date, group);



        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            TextView day_of_week = (TextView) findViewById(R.id.day_of_week);
            day_of_week.setText(dateFormat.format(storage.formatToRequest.parse(storage.currentDateView)));
        } catch (ParseException e) {e.printStackTrace();}

        View first_pair = findViewById(R.id.first_pair);
        View second_pair = findViewById(R.id.second_pair);
        View third_pair = findViewById(R.id.third_pair);
        View fourth_pair = findViewById(R.id.fourth_pair);
        View fifth_pair = findViewById(R.id.fifth_pair);
        View sixth_pair = findViewById(R.id.sixth_pair);
        View seventh_pair = findViewById(R.id.seventh_pair);

        fillPair(first_pair,
                dayData.get(1),
                getResources().getString(R.string.first_pair_start),
                getResources().getString(R.string.first_pair_end)
        );
        fillPair(second_pair,
                dayData.get(2),
                getResources().getString(R.string.second_pair_start),
                getResources().getString(R.string.second_pair_end)
        );
        fillPair(third_pair,
                dayData.get(3),
                getResources().getString(R.string.third_pair_start),
                getResources().getString(R.string.third_pair_end)
        );
        fillPair(fourth_pair,
                dayData.get(4),
                getResources().getString(R.string.fourth_pair_start),
                getResources().getString(R.string.fourth_pair_end)
        );
        fillPair(fifth_pair,
                dayData.get(5),
                getResources().getString(R.string.fifth_pair_start),
                getResources().getString(R.string.fifth_pair_end)
        );
        fillPair(sixth_pair,
                dayData.get(6),
                getResources().getString(R.string.sixth_pair_start),
                getResources().getString(R.string.sixth_pair_end)
        );
        fillPair(seventh_pair,
                dayData.get(6),
                getResources().getString(R.string.seventh_pair_start),
                getResources().getString(R.string.seventh_pair_end)
        );
    }

    private void fillPair(View pair, Map<String, String> data, String lesson_start, String lesson_end) {
        ((TextView) pair.findViewById(R.id.pair_start)).setText(lesson_start);
        ((TextView) pair.findViewById(R.id.pair_end)).setText(lesson_end);
        if (data != null) {
            ((TextView) pair.findViewById(R.id.lesson_type)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_kind_of_work)).substring(0, 4));
            ((TextView) pair.findViewById(R.id.lesson_name)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_discipline)));
            if (storage.lectorMode)
                ((TextView) pair.findViewById(R.id.lesson_lector)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_group_abbr)));
            else
                ((TextView) pair.findViewById(R.id.lesson_lector)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_lecturer)));
            String auditory[] = data.get(getApplicationContext().getString(R.string.map_name_lesson_auditory)).split("-");
            ((TextView) pair.findViewById(R.id.auditory)).setText(auditory[0] + "\n" + auditory[1]);
        } else {
            ((TextView) pair.findViewById(R.id.lesson_type)).setText("");
            ((TextView) pair.findViewById(R.id.lesson_name)).setText("");
            ((TextView) pair.findViewById(R.id.lesson_lector)).setText("");
            ((TextView) pair.findViewById(R.id.auditory)).setText("");
        }
    }

    private void getTestScheduleCurrentDay() {
        storage.serverCommunicator.getScheduleFromServer(
                storage.lectorMode,
                storage.lectorMode ? storage.currentLecturerView : storage.currentGroupView,
                "2015-09-14",
                "2015-09-18",
                getCurrentDayCallback()
        );
    }

    private void getScheduleCurrentDay(String group) {
        Calendar today = Calendar.getInstance();

        Calendar afternoon = Calendar.getInstance();
        afternoon.add(Calendar.DATE, 1);

        storage.serverCommunicator.getScheduleFromServer(
                storage.lectorMode,
                group,
                storage.formatToRequest.format(afternoon.getTime()),
                storage.formatToRequest.format(today.getTime()),
                getCurrentDayCallback()
        );
    }

    private void getScheduleCurrentWeek(String group) {
        Calendar firstDayOfWeek = Calendar.getInstance();
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, firstDayOfWeek.getFirstDayOfWeek());

        Calendar lastDayOfWeek = Calendar.getInstance();
        lastDayOfWeek.set(Calendar.DAY_OF_WEEK, lastDayOfWeek.getFirstDayOfWeek());
        lastDayOfWeek.add(Calendar.WEEK_OF_YEAR, 1);

        storage.serverCommunicator.getScheduleFromServer(
                storage.lectorMode,
                group,
                storage.formatToRequest.format(firstDayOfWeek.getTime()),
                storage.formatToRequest.format(lastDayOfWeek.getTime()),
                getCurrentDayCallback()
        );
    }

    private void getScheduleBetweenDates(String group, String dateBegin, String dateEnd) {
        storage.serverCommunicator.getScheduleFromServer(
                storage.lectorMode,
                group,
                dateBegin,
                dateEnd,
                getCurrentDayCallback()
        );
    }

    private void getScheduleInDay(String group, String dayDate) {
        Calendar afterDayDate = Calendar.getInstance();
        Log.d("test", dayDate);
        try {
            afterDayDate.setTime(storage.formatToRequest.parse(dayDate));
            afterDayDate.add(Calendar.DAY_OF_YEAR, 1);
            Log.d("test", storage.formatToRequest.format(afterDayDate.getTime()));
            storage.serverCommunicator.getScheduleFromServer(
                    storage.lectorMode,
                    group,
                    dayDate,
                    storage.formatToRequest.format(afterDayDate.getTime()),
                    getCurrentDayCallback()
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void pickDateDialog(View view) {
        Calendar currentDate = Calendar.getInstance();
        try {
            currentDate.setTime(storage.formatToRequest.parse(storage.currentDateView));
        } catch (ParseException e) {e.printStackTrace();}
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setDate(year, monthOfYear, dayOfMonth);
                    }
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void setDate(int year, int month, int day) {
        month++;
        String dateString = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
        TextView dateView = (TextView) findViewById(R.id.day_date_text_view);
        dateView.setText(dateString);
        storage.currentDateView = dateString;
        showSchedule();
        if (!storage.lectorMode)
            getScheduleInDay(storage.currentGroupView, dateString);
        else
            getScheduleInDay(storage.currentLecturerView, dateString);
    }

    private VolleyCallback getFacultiesListCallback() {
        return new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                AsyncParserPerformer performer = new AsyncParserPerformer();
                performer.execute(performer.PARSE_FACULTIES, getApplicationContext(), response, new AsyncMainThreadTask() {
                    @Override
                    public void executeTask() {
                        updateFacultyListSpinnerItems();
                    }
                });
            }
        };
    }

    private VolleyCallback getGroupsListCallback() {
        return new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                AsyncParserPerformer performer = new AsyncParserPerformer();
                performer.execute(performer.PARSE_GROUPS, getApplicationContext(), response, new AsyncMainThreadTask() {
                    @Override
                    public void executeTask() {
                        updateGroupLecturerListSpinnerItems();
                    }
                });
            }
        };
    }

    private VolleyCallback getLecturersListCallback() {
        return new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                AsyncParserPerformer performer = new AsyncParserPerformer();
                performer.execute(performer.PARSE_LECTURERS, getApplicationContext(), response, new AsyncMainThreadTask() {
                    @Override
                    public void executeTask() {
                        updateGroupLecturerListSpinnerItems();
                    }
                });
            }
        };
    }

    private VolleyCallback getCurrentDayCallback() {
        return new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
            AsyncParserPerformer performer = new AsyncParserPerformer();
            performer.execute(performer.PARSE_SCHEDULE, getApplicationContext(), response, new AsyncMainThreadTask() {
                @Override
                public void executeTask() {
                    showSchedule();
                }
            });
            }
        };
    }
}
