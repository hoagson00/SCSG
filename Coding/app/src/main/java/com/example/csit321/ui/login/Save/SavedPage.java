package com.example.csit321.ui.login.Save;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.csit321.Adapter.EventAdapto;
import com.example.csit321.Adapter.PostAdapter;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.EntityClass.Post;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Event.EventDetail;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.PostDetail;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SavedPage extends AppCompatActivity {

    private static final String TAG = "SavedPage";
    private static final String MyPreferences = "MyPrefs";

    private RecyclerView recyclerView;

    private EventAdapto eventAdapto;
    private PostAdapter postAdapter;

    private List<Post> posts;
    private List<Event> events;

    private FirebaseFirestore db;
    ProgressDialog pd;

    private String accountType, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Show a list of saved posts and events of current user
        setContentView(R.layout.activity_saved_page);

        getSharePref(); //get user's information
        loadPostAdapter(); //setup a view of list
        loadLikedPostsList(); //load the list of posts
        combinationButton();//button to switch between list of saved posts and list of saved events
        bottomNavFunction(); //bottom navigator bar
    }

    private void combinationButton()
    {
        Button postBut = findViewById(R.id.saved_page_button);
        Button eventBut = findViewById(R.id.saved_page_button1);

        postBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBut.setBackgroundColor(getResources().getColor(R.color.white));
                eventBut.setTextColor(getResources().getColor(R.color.black));

                postBut.setBackgroundColor(getResources().getColor(R.color.blue));
                postBut.setTextColor(getResources().getColor(R.color.white));

                loadPostAdapter();
                loadLikedPostsList();
            }
        });

        eventBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postBut.setBackgroundColor(getResources().getColor(R.color.white));
                postBut.setTextColor(getResources().getColor(R.color.black));

                eventBut.setBackgroundColor(getResources().getColor(R.color.blue));
                eventBut.setTextColor(getResources().getColor(R.color.white));

                loadEventAdapter();
                loadSavedEventList();
            }
        });
    }

    private void getSharePref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        userId = sh.getString("userId","");
        accountType = sh.getString("accountType", "");
    }

    private void loadEventAdapter()
    {
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.saved_page_list);
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

    private void loadPostAdapter()
    {
        recyclerView = findViewById(R.id.saved_page_list);
        db = FirebaseFirestore.getInstance();
        posts = new ArrayList<>();

        postAdapter = new PostAdapter(posts, this, new PostAdapter.IClickItemPost() {
            @Override
            public void showDetail (String postId) {showPostDetail (postId);}
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void loadLikedPostsList(){
        posts = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");
        pd.show();

        if (userId.equals(""))
        {
            Toast.makeText(SavedPage.this, "Please log in first!!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            if(document.exists())
                            {
                                List<String> likedPosts = new ArrayList<>();
                                likedPosts = (List<String>) document.get("posts_liked");
                                if (likedPosts == null || likedPosts.isEmpty())
                                {
                                    postAdapter.setPostList(posts);
                                }
                                else
                                {
                                    db.collection("Posts").whereIn(FieldPath.documentId(), likedPosts).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        for (QueryDocumentSnapshot document : querySnapshot)
                                                        {
                                                            Post post = new Post();
                                                            post.setPostID(document.getId());
                                                            post.setTitle(document.getString("title"));
                                                            post.setDescription(document.getString("description"));
                                                            post.setHashtag(document.getString("hashtag"));
                                                            post.setLikedCount(Integer.parseInt(document.get("total_likes").toString()));
                                                            post.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                                                            post.setImageId(document.getString("image_id"));

                                                            post.setDate(document.getDate("date_posted"));
                                                            posts.add(post);
                                                        }
                                                        postAdapter.setPostList(posts);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SavedPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                            else
                            {
                                Toast.makeText(SavedPage.this, "Cannot find User", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SavedPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        pd.dismiss();
    }

    private void loadSavedEventList(){
        events = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");
        pd.show();

        if (userId.equals(""))
        {
            Toast.makeText(SavedPage.this, "Please log in first!!!", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(SavedPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                            else
                            {
                                Toast.makeText(SavedPage.this, "Cannot find User", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SavedPage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        pd.dismiss();
    }

    private void showEventDetail (final String eventId)
    {
        Intent myIntent = new Intent(SavedPage.this, EventDetail.class);
        myIntent.putExtra("event_id", eventId);
        myIntent.putExtra("from", "SavedPage");
        SavedPage.this.finish();
        SavedPage.this.startActivity(myIntent);
    }

    private void showPostDetail (final String postId)
    {
        Intent myIntent = new Intent(SavedPage.this, PostDetail.class);
        myIntent.putExtra("postId", postId);
        myIntent.putExtra("from", "SavedPage");
        SavedPage.this.finish();
        SavedPage.this.startActivity(myIntent);
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.saved_page_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(SavedPage.this, HomePage.class);
                    SavedPage.this.finish();
                    SavedPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(SavedPage.this, VideoPage.class);
                    SavedPage.this.finish();
                    SavedPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(SavedPage.this, EventPage.class);
                    SavedPage.this.finish();
                    SavedPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(SavedPage.this, SavedPage.class);
                    SavedPage.this.finish();
                    SavedPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(SavedPage.this, ToolbarPage.class);
                    SavedPage.this.finish();
                    SavedPage.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}