package com.eatfit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealHistory;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.imanoweb.calendarview.DayDecorator;
import com.imanoweb.calendarview.DayView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class MyRegimen extends AppCompatActivity implements NetworkCallback {

    CustomCalendarView calendarView;
    SharedPreference spMain;
    String formattedDate;
    Calendar calendar;
    SimpleDateFormat df;
    ArrayList<MealHistory> mealHistoryArrayList;
    MealHistory mealHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_regimen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setTitle(null);
        // TODO get back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO set custom navigation icon on toolbar
        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        spMain = spMain.getInstance(this);
        df = new SimpleDateFormat("yyyy-MM-dd");
        mealHistoryArrayList = new ArrayList<>();
        //Initialize CustomCalendarView from layout
        calendarView = (CustomCalendarView) findViewById(R.id.calendar_view);
        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show Monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);
        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);
        // TO diable the Navigation Arrow from Calendar
        ((ImageView) calendarView.findViewById(R.id.leftButton)).setVisibility(View.GONE);
        ((ImageView) calendarView.findViewById(R.id.rightButton)).setVisibility(View.GONE);


        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {

                if (Utils.getDifference(date)) {
                    Intent in = new Intent(MyRegimen.this, MyRegimenNutrition.class);
                    in.putExtra("list", (Serializable) mealHistoryArrayList);
                    in.putExtra("Date", df.format(date));
                    startActivity(in);
                } else {
                    Toast.makeText(MyRegimen.this, "This is out range of your 15 Days history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMonthChanged(Date date) {
//                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
//                Toast.makeText(MyRegimen.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        calendar = Calendar.getInstance();
        System.out.println("Current time => " + calendar.getTime());
        formattedDate = df.format(calendar.getTime());

        Log.e("Date", formattedDate);


    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {

    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {

    }
}
