package com.example.csit321.ui.login.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.csit321.Adapter.EventAdapto;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventPageSortByDate extends AppCompatActivity {

    private List<Event> events;
    private EventAdapto eventAdapto;
    private FirebaseFirestore db;

    private static final String TAG = "EventPageSortByDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Generate a new list of events after sort by date function
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page_sort_by_date);
        bottomNavFunction(); //bottom navigation bar
        backButton(); //back button
        showRecycleView(); //setup a view for the new list of event
        sortEventByTime(); //sort event by time function
    }

    private void showRecycleView()
    {
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.event_page_sortbydate_list);
        eventAdapto = new EventAdapto(events, this, new EventAdapto.IClickItemEvent(){
            @Override
            public void showEvent (String eventId) {sendEventToEventDetail (eventId);}
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
//        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(eventAdapto);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void sortEventByTime()
    {
        DateFormat dateFormat =  new DateFormat();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String dateFrom = extras.getString("fromDate");
            String dateTo = extras.getString("toDate");
            int type = extras.getInt("type");

            Date dateFromInput = null;
            Date dateToInput = null;
            try {
                dateFromInput = dateFormat.StringToDate1(dateFrom);
                dateToInput = dateFormat.StringToDate1(dateTo);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (type == 1)
            {
                if (dateFrom.equals("0-00-00 00:00:00"))
                {
                    loadEventBeforeEndDate(1,dateToInput);
                }
                else if (dateTo.equals("0-00-00 00:00:00"))
                {
                    loadEventAfterStartDate(1,dateFromInput);
                }
                else
                {
                    loadEventSortByDate(1,dateFromInput,dateToInput);
                }
            }
            else if (type == 2)
            {
                if (dateFrom.equals("0-00-00 00:00:00"))
                {
                    loadEventBeforeEndDate(2,dateToInput);
                }
                else if (dateTo.equals("0-00-00 00:00:00"))
                {
                    loadEventAfterStartDate(2,dateFromInput);
                }
                else
                {
                    loadEventSortByDate(2,dateFromInput,dateToInput);
                }
            }
            else
            {
                Toast.makeText(EventPageSortByDate.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadEventBeforeEndDate(int dateType, Date toDate)
    {
        String type = null;
        if (dateType == 1)
            type = "date_from";
        if (dateType == 2)
            type = "date_to";
        events = new ArrayList<>();
        db.collection("Events").whereLessThanOrEqualTo(type,toDate).orderBy(type, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Event event = new Event();
                        event.setEventId(document.getId());
                        event.setName(document.getString("name"));
                        event.setDescription(document.getString("description"));
                        event.setLocation(document.getString("location"));
                        event.setStartDate(document.getDate("date_from"));
                        event.setEndDate(document.getDate("date_to"));
                        event.setStatus(Integer.parseInt(document.get("status").toString()));
                        event.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                        event.setSubCount(Integer.parseInt(document.get("total_subscribes").toString()));
                        event.setImageId(document.getString("image_id"));

                        events.add(event);
                        Log.d(TAG, event.toString());
                    }
                    eventAdapto.setEventList(events);
                }
                else
                {
                    Toast.makeText((EventPageSortByDate.this), "No data found in Database", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void loadEventAfterStartDate(int dateType, Date fromDate)
    {
        String type = null;
        if (dateType == 1)
            type = "date_from";
        if (dateType == 2)
            type = "date_to";
        events = new ArrayList<>();
        db.collection("Events").whereGreaterThanOrEqualTo(type,fromDate).orderBy(type, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Event event = new Event();
                        event.setEventId(document.getId());
                        event.setName(document.getString("name"));
                        event.setDescription(document.getString("description"));
                        event.setLocation(document.getString("location"));
                        event.setStartDate(document.getDate("date_from"));
                        event.setEndDate(document.getDate("date_to"));
                        event.setStatus(Integer.parseInt(document.get("status").toString()));
                        event.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                        event.setSubCount(Integer.parseInt(document.get("total_subscribes").toString()));
                        event.setImageId(document.getString("image_id"));

                        events.add(event);
                    }
                    eventAdapto.setEventList(events);
                }
                else
                {
                    Toast.makeText((EventPageSortByDate.this), "No data found in Database", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void loadEventSortByDate(int dateType, Date fromDate, Date toDate)
    {
        String type = null;
        if (dateType == 1)
            type = "date_from";
        if (dateType == 2)
            type = "date_to";
        events = new ArrayList<>();
        db.collection("Events").whereGreaterThanOrEqualTo(type,fromDate).whereLessThanOrEqualTo(type,toDate)
                .orderBy(type, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Event event = new Event();
                        event.setEventId(document.getId());
                        event.setName(document.getString("name"));
                        event.setDescription(document.getString("description"));
                        event.setLocation(document.getString("location"));
                        event.setStartDate(document.getDate("date_from"));
                        event.setEndDate(document.getDate("date_to"));
                        event.setStatus(Integer.parseInt(document.get("status").toString()));
                        event.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                        event.setSubCount(Integer.parseInt(document.get("total_subscribes").toString()));
                        event.setImageId(document.getString("image_id"));

                        events.add(event);
                    }
                    eventAdapto.setEventList(events);
                }
                else
                {
                    Toast.makeText((EventPageSortByDate.this), "No data found in Database", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void sendEventToEventDetail (final String eventId)
    {
        Intent myIntent = new Intent(EventPageSortByDate.this, EventDetail.class);
        myIntent.putExtra("event_id", eventId);
        EventPageSortByDate.this.finish();
        EventPageSortByDate.this.startActivity(myIntent);
    }


    private void backButton()
    {
        Button button = (Button) findViewById(R.id.event_page_sortbydate_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( EventPageSortByDate.this, EventPage.class);
                EventPageSortByDate.this.finish();
                EventPageSortByDate.this.startActivity(intent);
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.event_page_sortbydate_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(EventPageSortByDate.this, HomePage.class);
                    EventPageSortByDate.this.finish();
                    EventPageSortByDate.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(EventPageSortByDate.this, VideoPage.class);
                    EventPageSortByDate.this.finish();
                    EventPageSortByDate.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(EventPageSortByDate.this, EventPage.class);
                    EventPageSortByDate.this.finish();
                    EventPageSortByDate.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(EventPageSortByDate.this, SavedPage.class);
                    EventPageSortByDate.this.finish();
                    EventPageSortByDate.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(EventPageSortByDate.this, ToolbarPage.class);
                    EventPageSortByDate.this.finish();
                    EventPageSortByDate.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}