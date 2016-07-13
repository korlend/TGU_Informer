package ru.tltsu.informer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

public class DayActivity extends AppCompatActivity {
    VolleyCallback volleyCallback;
    Map<String, Map<Integer, Map<String, String>>> schedule;
    RequestQueue queue;
    SimpleDateFormat formatToRequest;
    ScheduleContract database;
    String currentDateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        /* init start */
        volleyCallback = new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                ServerResponseParser.parseServerResponse(
                        getApplicationContext(),
                        database,
                        formatToRequest,
                        response
                );
                fillDay(schedule.get(currentDateView));
            }
        };
        schedule = new HashMap<>();
        formatToRequest = new SimpleDateFormat(getResources().getString(R.string.date_format_to_server_request), Locale.ENGLISH);
        queue = Volley.newRequestQueue(this);
        database = new ScheduleContract(getApplicationContext());
        /* init end */

        /* for tests */
        Calendar fromDate = Calendar.getInstance();
        Calendar toDate = Calendar.getInstance();

        fromDate.set(Calendar.YEAR, 2015);
        fromDate.set(Calendar.MONTH, 8);
        fromDate.set(Calendar.DATE, 15);
        toDate.set(Calendar.YEAR, 2015);
        toDate.set(Calendar.MONTH, 10);
        toDate.set(Calendar.DATE, 15);

        /*
        test
        */

        currentDateView = "МОб-1201";

        database.deleteAllRecords();

        this.getScheduleFromServer(
                "МОб-1201",
                "2015-08-15",
                "2015-10-15",
                volleyCallback
        );

        schedule = database.selectScheduleByDates("2015-08-15", "2015-10-15");
        fillDay(schedule.get(currentDateView));
        System.out.println(schedule.get(currentDateView));
        System.out.println(schedule);

        /*
        test
         */



        //getScheduleBetweenDates("МОб-1201", fromDate, toDate);
        //fillDay(schedule.get("2015-09-15"));

        for(Map.Entry<String, Map<Integer, Map<String, String>>> entry : schedule.entrySet()) {
            String key = entry.getKey();
            System.out.println(key);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillDay(Map<Integer, Map<String, String>> dayData) {
        if (dayData == null) {
            dayData = new HashMap<>();
        }

        View first_pair = findViewById(R.id.first_pair);
        View second_pair = findViewById(R.id.second_pair);
        View third_pair = findViewById(R.id.third_pair);
        View fourth_pair = findViewById(R.id.fourth_pair);
        View fifth_pair = findViewById(R.id.fifth_pair);
        View sixth_pair = findViewById(R.id.sixth_pair);

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
    }

    private void fillPair(View pair, Map<String, String> data, String lesson_start, String lesson_end) {
        ((TextView) pair.findViewById(R.id.pair_start)).setText(lesson_start);
        ((TextView) pair.findViewById(R.id.pair_end)).setText(lesson_end);
        if (data != null) {
            ((TextView) pair.findViewById(R.id.lesson_type)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_kind_of_work)).substring(0, 4));
            ((TextView) pair.findViewById(R.id.lesson_name)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_discipline)));
            ((TextView) pair.findViewById(R.id.lesson_lector)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_lecturer)));
            ((TextView) pair.findViewById(R.id.auditory)).setText(data.get(getApplicationContext().getString(R.string.map_name_lesson_auditory)));
        }
    }

    private void getScheduleCurrentDay(String group) {
        Calendar today = Calendar.getInstance();

        Calendar afternoon = Calendar.getInstance();
        afternoon.add(Calendar.DATE, 1);
        getScheduleFromServer(group, formatToRequest.format(afternoon.getTime()), formatToRequest.format(today.getTime()), volleyCallback);

    }

    private void getScheduleCurrentWeek(String group) {
        Calendar firstDayOfWeek = Calendar.getInstance();
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, firstDayOfWeek.getFirstDayOfWeek());

        Calendar lastDayOfWeek = Calendar.getInstance();
        lastDayOfWeek.set(Calendar.DAY_OF_WEEK, lastDayOfWeek.getFirstDayOfWeek());
        lastDayOfWeek.add(Calendar.WEEK_OF_YEAR, 1);

        getScheduleFromServer(group, formatToRequest.format(firstDayOfWeek.getTime()), formatToRequest.format(lastDayOfWeek.getTime()), volleyCallback);

    }

    private void getScheduleBetweenDates(String group, Calendar dateBegin, Calendar dateEnd) {
        System.out.println("getSchedule");
        getScheduleFromServer(group, formatToRequest.format(dateBegin.getTime()), formatToRequest.format(dateEnd.getTime()), volleyCallback);
        System.out.println("parseserverresponse");
    }

    private void getScheduleFromServer(String group, String dateBegin, String dateEnd, final VolleyCallback callback) {
        String url = String.format(
                getResources().getString(R.string.url_server_data_get_by_date_range),
                group,
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
        queue.add(stringRequest);
    }
}
