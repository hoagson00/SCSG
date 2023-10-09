package com.example.csit321.ui.login.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.csit321.Adapter.EventAdapto;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class EventPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapto eventAdapto;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Event> events;

    private static final String TAG = "EventPage";
    private static final String MyPreferences = "MyPrefs";

    private ProgressDialog pd;

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        readSharedPref(); //read the user's information
        bottomNavFunction(); //bottom navigation bar
        showRecycleView(); //setup a list of events
        loadEventListByNewest(); //load the list of event which sorted by recent date
        addNewEventButton(); //add new event button (for admin only)
        searchEventByTitle(); //search event by title
        filterButton(); //filter the list of events by different types
        sortByDateButton(); //sort event by inputted date
    }

    private void sortByDateButton()
    {
        Button button = (Button) findViewById(R.id.event_page_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerView = (RecyclerView) findViewById(R.id.event_page_list);
                recyclerView.setAlpha(0);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.event_page_frag_container, EventPageFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
            }
        });
    }

    private void filterButton()
    {
        Button button = (Button) findViewById(R.id.event_page_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    private void loadEventListByNewest(){
        pd.show();
        events = new ArrayList<>();
        db.collection("Events").orderBy("date_posted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadEventListBySub(){
        pd.show();
        events = new ArrayList<>();
        db.collection("Events").orderBy("total_subscribes", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadEventListByView(){
        pd.show();
        events = new ArrayList<>();
        db.collection("Events").orderBy("total_views", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadEventListByRelevant(){
        pd.show();
        events = new ArrayList<>();
        db.collection("Events").orderBy("date_posted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    Collections.shuffle(events);
                    eventAdapto.setEventList(events);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadEventListByStartDate(){
        pd.show();
        events = new ArrayList<>();
        db.collection("Events").orderBy("date_from", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadEventListByExpiryDate(){
        pd.show();
        events = new ArrayList<>();
        db.collection("Events").orderBy("date_to", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(EventPage.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.event_page_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.event_page_popup_menu1)
                {
                    loadEventListByNewest();
                }
                if (item.getItemId() == R.id.event_page_popup_menu2)
                {
                    loadEventListBySub();
                }
                if (item.getItemId() == R.id.event_page_popup_menu3)
                {
                    loadEventListByView();
                }
                if (item.getItemId() == R.id.event_page_popup_menu4)
                {
                    loadEventListByRelevant();
                }
                if (item.getItemId() == R.id.event_page_popup_menu5)
                {
                    loadEventListByStartDate();
                }
                if (item.getItemId() == R.id.event_page_popup_menu6)
                {
                    loadEventListByExpiryDate();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void searchEventByTitle()
    {
        TextInputEditText inputEditText = (TextInputEditText) findViewById(R.id.event_page_edittext);
        Button searchBut = (Button) findViewById(R.id.event_page_button1);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String title = inputEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
                if (title.isEmpty()) {
                    loadEventListByNewest();
                    pd.dismiss();
                }
                else
                {
                    inputEditText.setText("");
                    CollectionReference ref = db.collection("Events");
                    ref.whereArrayContainsAny("title_array", Arrays.asList(title.split(" "))).orderBy("date_posted", Query.Direction.DESCENDING)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    events = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
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
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText((EventPage.this), "No data found in Database", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                }
            }
        });
    }

    private void addNewEventButton()
    {
        Button button = (Button) findViewById(R.id.event_page_button3);
        button.setVisibility(View.INVISIBLE);
        if (accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventPage.this, NewEvent.class);
                    EventPage.this.finish();
                    EventPage.this.startActivity(intent);
                }
            });
        }
    }

    private void showRecycleView()
    {
        events = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.event_page_list);
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");
        eventAdapto = new EventAdapto(events, this, new EventAdapto.IClickItemEvent(){
            @Override
            public void showEvent (String eventId) {sendEventToEventDetail (eventId);}
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(eventAdapto);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void sendEventToEventDetail (final String eventId)
    {
        Intent myIntent = new Intent(EventPage.this, EventDetail.class);
        myIntent.putExtra("event_id", eventId);
        EventPage.this.finish();
        EventPage.this.startActivity(myIntent);
    }

    private void readSharedPref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        accountType = sh.getString("accountType", "");
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.event_page_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(EventPage.this, HomePage.class);
                    EventPage.this.finish();
                    EventPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(EventPage.this, VideoPage.class);
                    EventPage.this.finish();
                    EventPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(EventPage.this, EventPage.class);
                    EventPage.this.finish();
                    EventPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(EventPage.this, SavedPage.class);
                    EventPage.this.finish();
                    EventPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(EventPage.this, ToolbarPage.class);
                    EventPage.this.finish();
                    EventPage.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}