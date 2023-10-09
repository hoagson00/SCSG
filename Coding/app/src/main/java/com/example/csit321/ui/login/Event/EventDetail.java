package com.example.csit321.ui.login.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.ui.login.AdminManagement.Statistics;
import com.example.csit321.ui.login.AdminManagement.UserSaved;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class EventDetail extends AppCompatActivity {

    private static final String TAG = "EventDetail";

    private static final String MyPreferences = "MyPrefs";

    private FirebaseFirestore db;

    private String userId, accountType;

    private Button whiteRegisBut, blueRegisBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = FirebaseFirestore.getInstance();

        readSharedPref(); //get the user information
        checkRegisterStatus(getEventId()); //check if the user register the event
        bottomNavFunction(); //bottom navigation bar
        backButton(checkSendStatus()); //back button
        getEventDate(getEventId()); //show the event's details
        registerBut(getEventId(), checkSendStatus()); //user register/unregister the event
        registeredUserListButton(); //list of users who register this event (for admin only)
        updateButton(); //update event's detail button
    }

    private void registeredUserListButton()
    {
        Button button = findViewById(R.id.event_detail_button4);
        button.setVisibility(View.INVISIBLE);
        if (accountType != null && accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null)
                    {
                        String eventId = extras.getString("event_id");
                        Intent intent = new Intent(EventDetail.this, EventRegistrations.class);
                        intent.putExtra("event_id",eventId);
                        finish();
                        startActivity(intent);
                    }
                }
            });
        }
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

    private void checkRegisterStatus(String eventId)
    {
        whiteRegisBut = (Button) findViewById(R.id.event_detail_button2);
        blueRegisBut = (Button) findViewById(R.id.event_detail_button1);
        if (userId.equals(""))
        {
            blueRegisBut.setVisibility(View.INVISIBLE);
            whiteRegisBut.setVisibility(View.VISIBLE);
        }
        else
        {
            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists())
                        {
                            List<String> savedEvents = new ArrayList<>();
                            savedEvents = (List<String>) document.get("events_saved");
                            if (savedEvents.contains(eventId) && savedEvents!= null)
                            {
                                blueRegisBut.setVisibility(View.VISIBLE);
                                whiteRegisBut.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                blueRegisBut.setVisibility(View.INVISIBLE);
                                whiteRegisBut.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else
                    {
                        Log.d(TAG, "Get failed with"+ task.getException());
                        Toast.makeText(EventDetail.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void registerBut(String eventId, String status)
    {
        regisWhiteBut(eventId, status);
        regisBlueBut(eventId, status);
    }

    private void regisWhiteBut(String eventId, String status)
    {
        whiteRegisBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.equals(""))
                {
                    Toast.makeText(EventDetail.this, "Please log in first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.collection("Users").document(userId).update("events_saved", FieldValue.arrayUnion(eventId))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG,"Press white");
                                    db.collection("Events").document(eventId).update("total_subscribes", FieldValue.increment(1));
                                    db.collection("Events").document(eventId).update("user_register", FieldValue.arrayUnion(userId))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                whiteRegisBut.setVisibility(View.INVISIBLE);
                                                                blueRegisBut.setVisibility(View.VISIBLE);
                                                                Intent intent = new Intent(EventDetail.this, EventDetail.class);
                                                                intent.putExtra("event_id", eventId);
                                                                intent.putExtra("from", status);
                                                                startActivity(intent);
                                                                finish();
                                                                Toast.makeText(EventDetail.this, "Registered.", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(EventDetail.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EventDetail.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EventDetail.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }

    private void regisBlueBut(String eventId, String status)
    {
        blueRegisBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(userId).update("events_saved", FieldValue.arrayRemove(eventId))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG,"Press blue");
                                    db.collection("Events").document(eventId).update("total_subscribes", FieldValue.increment(-1));
                                    db.collection("Events").document(eventId).update("user_register", FieldValue.arrayRemove(userId))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                whiteRegisBut.setVisibility(View.VISIBLE);
                                                                blueRegisBut.setVisibility(View.INVISIBLE);
                                                                Intent intent = new Intent(EventDetail.this, EventDetail.class);
                                                                intent.putExtra("event_id", eventId);
                                                                intent.putExtra("from", status);
                                                                startActivity(intent);
                                                                finish();
                                                                Toast.makeText(EventDetail.this, "Unregistered.", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else
                                                                Toast.makeText(EventDetail.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EventDetail.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EventDetail.this, "Unliked post failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void updateButton()
    {
        Button updateBut = findViewById(R.id.event_detail_button3);
        updateBut.setVisibility(View.INVISIBLE);
        if (accountType.equals("admin"))
        {
            updateBut.setVisibility(View.VISIBLE);
            updateBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null)
                    {
                        String eventId = extras.getString("event_id");
                        Intent intent = new Intent(EventDetail.this, UpdateEvent.class);
                        intent.putExtra("event_id",eventId);
                        finish();
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private String getEventId()
    {
        String eventId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("event_id");
        }
        return eventId;
    }

    private void getEventDate(String eventId)
    {
        DateFormat dateFormat = new DateFormat();
        DocumentReference docRef = db.collection("Events").document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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

                        TextView title = (TextView) findViewById(R.id.event_detail_textview1);
                        title.setText(event.getName());

                        TextView description = (TextView) findViewById(R.id.event_detail_textview2);
                        description.setText(event.getDescription());

                        TextView locationAndTime = (TextView) findViewById(R.id.event_detail_textview3);
                        try {
                            locationAndTime.setText("Location: " + event.getLocation() + "\n" +
                                    "Time Start: " + dateFormat.dateToString1(event.getStartDate()) + "\n" +
                                    "Time End: " + dateFormat.dateToString1(event.getEndDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        TextView viewAndSub = (TextView) findViewById(R.id.event_detail_textview4);
                        viewAndSub.setText("Subscribes: " + event.getSubCount() + "\n" + "Views: " + event.getViewCount());

                        loadImage(event.getImageId());
                        updateView(eventId, event.getViewCount());
                    } else {
                        Log.d(TAG, "Cannot find event");
                    }
                } else {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(EventDetail.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadImage(String imageId)
    {
        ImageView imageView = (ImageView) findViewById(R.id.event_detail_image);
        if (imageId == null)
        {
            imageView.setVisibility(View.GONE);
        }
        else
        {
            DocumentReference docRef = db.collection("Images").document(imageId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            String url =document.getString("url");
//                            Picasso.with(getApplicationContext()).load(path).into(imageView);
                            Glide.with(getApplicationContext()).load(url).into(imageView);
                        }
                    }
                    else
                    {
                        Log.d(TAG, "Get failed with"+ task.getException());
                        Toast.makeText(EventDetail.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateView(String eventId, int viewCount)
    {
        db.collection("Events").document(eventId).update("total_views", (viewCount+1));
    }

    private void backButton(String status)
    {
        Button backBut = (Button) findViewById(R.id.event_detail_button);
        if (status != null && status.equals("SavedPage"))
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( EventDetail.this, SavedPage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(intent);
                }
            });
        }
        else if (status != null && status.equals("Statistics"))
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( EventDetail.this, Statistics.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(intent);
                }
            });
        }
        else if (status != null && status.equals("UserSaved"))
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( EventDetail.this, UserSaved.class);
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        intent.putExtra("userId", extras.getString("user_id"));
                        intent.putExtra("fullName", extras.getString("fullName"));
                    }
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(intent);
                }
            });
        }
        else
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( EventDetail.this, EventPage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(intent);
                }
            });
        }
    }

    private void readSharedPref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        userId = sh.getString("userId","");
        accountType = sh.getString("accountType", "");
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.event_detail_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(EventDetail.this, HomePage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(EventDetail.this, VideoPage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(EventDetail.this, EventPage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(EventDetail.this, SavedPage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(EventDetail.this, ToolbarPage.class);
                    EventDetail.this.finish();
                    EventDetail.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}