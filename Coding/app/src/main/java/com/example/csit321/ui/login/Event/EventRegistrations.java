package com.example.csit321.ui.login.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.csit321.Adapter.EventAdapto;
import com.example.csit321.Adapter.UserAdapter;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.AdminManagement.AccountManagement;
import com.example.csit321.ui.login.AdminManagement.UserSaved;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventRegistrations extends AppCompatActivity {

    private static final String TAG = "EventRegistrations";
    private static final String MyPreferences = "MyPrefs";

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;

    private List<User> users;

    private FirebaseFirestore db;
    ProgressDialog pd;

    private String userId, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //show all the users' information who join this event (for admin only)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registrations);

        loadUserAdapter(); //set up a view of list
        loadUserList(getEventId()); //load a list of users' details
        backButton(); //back button
        bottomNavFunction(); //bottom navigation bar
    }

    private void loadUserList(String eventId)
    {
        pd.show();
        users = new ArrayList<>();
        db.collection("Events").document(eventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        List <String> userIdList = new ArrayList<>();
                        userIdList = (List<String>) document.get("user_register");
                        if (userIdList == null || userIdList.isEmpty())
                        {
                            userAdapter.setUserList(users);
                            pd.dismiss();
                        }
                        else
                        {
                            db.collection("Users").whereIn(FieldPath.documentId(), userIdList).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                for (QueryDocumentSnapshot document : querySnapshot)
                                                {
                                                    User user = new User();
                                                    user.setUserId(document.getId());
                                                    user.setFullName(document.getString("full_name"));
                                                    user.setUsername(document.getString("username"));
                                                    user.setAccountType(document.getString("purpose"));
                                                    users.add(user);
                                                }
                                                userAdapter.setUserList(users);
                                                pd.dismiss();
                                            }
                                            else
                                            {
                                                Toast.makeText(EventRegistrations.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EventRegistrations.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });
                        }
                    }
                    else
                    {
                        Toast.makeText(EventRegistrations.this, "Cannot find Event.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(EventRegistrations.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventRegistrations.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void loadUserAdapter()
    {
        recyclerView = findViewById(R.id.user_registered_event_list);
        db = FirebaseFirestore.getInstance();
        users = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        userAdapter = new UserAdapter(users, this, new UserAdapter.IClickItemUser() {
            @Override
            public void showDetail(String userId, String fullName) {
                showPostDetail(userId, fullName);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showPostDetail (final String userId, final String fullName)
    {
        Intent myIntent = new Intent(EventRegistrations.this, UserSaved.class);
        myIntent.putExtra("userId", userId);
        myIntent.putExtra("fullName", fullName);
        myIntent.putExtra("eventId", getEventId());
        myIntent.putExtra("from", "EventRegistrations");
        EventRegistrations.this.finish();
        EventRegistrations.this.startActivity(myIntent);
    }

    private String getEventId()
    {
        String eventId = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            eventId = extras.getString("event_id");
        }
        return eventId;
    }

    private void backButton()
    {
        Button button = findViewById(R.id.user_registered_event_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null)
                {
                    String eventId = extras.getString("event_id");
                    Intent intent = new Intent(EventRegistrations.this, EventDetail.class);
                    intent.putExtra("event_id",eventId);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.user_registered_event_lnav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(EventRegistrations.this, HomePage.class);
                    EventRegistrations.this.finish();
                    EventRegistrations.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(EventRegistrations.this, VideoPage.class);
                    EventRegistrations.this.finish();
                    EventRegistrations.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(EventRegistrations.this, EventPage.class);
                    EventRegistrations.this.finish();
                    EventRegistrations.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(EventRegistrations.this, SavedPage.class);
                    EventRegistrations.this.finish();
                    EventRegistrations.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(EventRegistrations.this, ToolbarPage.class);
                    EventRegistrations.this.finish();
                    EventRegistrations.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}