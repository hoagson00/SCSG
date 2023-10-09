package com.example.csit321.ui.login.AdminManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csit321.Adapter.EventAdapto;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Event.EventDetail;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Event.EventRegistrations;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Toolbar.UpdateAccount;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserSaved extends AppCompatActivity {

    private static final String TAG = "UserSaved";
    private static final String MyPreferences = "MyPrefs";

    private RecyclerView recyclerView;

    private EventAdapto eventAdapto;

    private List<Event> events;

    private FirebaseFirestore db;
    ProgressDialog pd;

    private String userId, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Admin can check the user's saved list of events
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_saved);

        getValues(); //get the user's information
        loadEventAdapter(); //setup a view of list
        loadSavedEventList(); //load the list of user's registered events
        backButton(checkSendStatus()); //back button
        bottomNavFunction(); //bottom navigation bar
    }

    private String checkSendStatus()
    {
        String status = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("from");
        }
        return status;
    }

    private void backButton(String status)
    {
        Button button = findViewById(R.id.user_saved_button);
        if (status != null && status.equals("EventRegistrations"))
        {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = getIntent().getExtras();
                    String eventId ="";
                    if (extras != null)
                    {
                        eventId = extras.getString("eventId");
                    }
                    Intent intent = new Intent(UserSaved.this, EventRegistrations.class);
                    intent.putExtra("event_id",eventId);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else
        {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserSaved.this, AccountManagement.class));
                    finish();
                }
            });
        }
    }

    private void loadEventAdapter()
    {
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.user_saved_list);
        eventAdapto = new EventAdapto(events, this, new EventAdapto.IClickItemEvent(){
            @Override
            public void showEvent (String eventId) {showEventDetail (eventId);}
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(eventAdapto);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void loadSavedEventList(){
        events = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.show();

        if (userId.equals(""))
        {
            Toast.makeText(UserSaved.this, "Please log in first!!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            if(document.exists())
                            {
                                List<String> savedEvent = new ArrayList<>();
                                savedEvent = (List<String>) document.get("events_saved");
                                if (savedEvent == null || savedEvent.isEmpty())
                                {
                                    eventAdapto.setEventList(events);
                                }
                                else
                                {
                                    db.collection("Events").whereIn(FieldPath.documentId(), savedEvent).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        for (QueryDocumentSnapshot document : querySnapshot)
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
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UserSaved.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                            else
                            {
                                Toast.makeText(UserSaved.this, "Cannot find User", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserSaved.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        pd.dismiss();
    }

    private void showEventDetail (final String eventId)
    {
        Intent myIntent = new Intent(UserSaved.this, EventDetail.class);
        myIntent.putExtra("event_id", eventId);
        myIntent.putExtra("user_id", userId);
        myIntent.putExtra("fullName", fullName);
        myIntent.putExtra("from", "UserSaved");
        UserSaved.this.finish();
        UserSaved.this.startActivity(myIntent);
    }

    private void getValues()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            userId = extras.getString("userId");
            fullName = extras.getString("fullName");
        }
        TextView textView = findViewById(R.id.user_saved_textView);
        textView.setText(fullName+" registered the following events: " );
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.user_saved_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(UserSaved.this, HomePage.class);
                    UserSaved.this.finish();
                    UserSaved.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(UserSaved.this, VideoPage.class);
                    UserSaved.this.finish();
                    UserSaved.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(UserSaved.this, EventPage.class);
                    UserSaved.this.finish();
                    UserSaved.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(UserSaved.this, SavedPage.class);
                    UserSaved.this.finish();
                    UserSaved.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(UserSaved.this, ToolbarPage.class);
                    UserSaved.this.finish();
                    UserSaved.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}