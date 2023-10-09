package com.example.csit321.ui.login.Video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.example.csit321.Adapter.VideoAdapter;
import com.example.csit321.EntityClass.Video;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
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
import java.util.Locale;

public class VideoPage extends AppCompatActivity {

    private static final String TAG = "VideoPage";

    private static final String MyPreferences = "MyPrefs";

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private ArrayList<Video> videos;
    private FirebaseFirestore db;

    private ProgressDialog pd;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);

        readSharedPref(); //read the user's information
        loadAdapter(); //setup a view of list
        loadVideoListByNewest(); //load video by recently posted
        bottomNavFunction();// bottom navigation bar
        addNewVideo(); //add new video button
        sortButton(); //sort video by different types
        searchVideoByTitle(); //search video button
    }


    public void addNewVideo()
    {
        Button button = (Button) findViewById(R.id.video_page_button2);
        button.setVisibility(View.INVISIBLE);
        if (accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(VideoPage.this, NewVideo.class);
                    VideoPage.this.finish();
                    VideoPage.this.startActivity(myIntent);
                }
            });
        }
    }

    private void loadVideoListByNewest(){
        videos = new ArrayList<>();
        pd.show();
        db.collection("Videos").orderBy("date_posted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Video video = new Video();
                        video.setVideoId(document.getId());
                        video.setTitle(document.getString("title"));
                        video.setDescription(document.getString("description"));
                        video.setView(Integer.parseInt(document.get("total_views").toString()));
                        video.setDatePosted(document.getDate("date_posted"));
                        video.setUrl(document.getString("link"));
                        video.setVideoId(document.getId());

                        //                            post.setDate(dateFormat.StringToDate1(document.getString("date_posted")));
                        videos.add(video);
                    }
                    videoAdapter.setVideoList(videos);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((VideoPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadVideoListByViews(){
        videos = new ArrayList<>();
        pd.show();
        db.collection("Videos").orderBy("total_views", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Video video = new Video();
                        video.setTitle(document.getString("title"));
                        video.setDescription(document.getString("description"));
                        video.setView(Integer.parseInt(document.get("total_views").toString()));
                        video.setDatePosted(document.getDate("date_posted"));
                        video.setVideoId(document.getId());

                        //                            post.setDate(dateFormat.StringToDate1(document.getString("date_posted")));
                        videos.add(video);
                    }
                    videoAdapter.setVideoList(videos);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((VideoPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadVideoListByRelevant(){
        videos = new ArrayList<>();
        pd.show();
        db.collection("Videos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Video video = new Video();
                        video.setTitle(document.getString("title"));
                        video.setDescription(document.getString("description"));
                        video.setView(Integer.parseInt(document.get("total_views").toString()));
                        video.setDatePosted(document.getDate("date_posted"));
                        video.setVideoId(document.getId());

                        //                            post.setDate(dateFormat.StringToDate1(document.getString("date_posted")));
                        videos.add(video);
                        Collections.shuffle(videos);
                    }
                    videoAdapter.setVideoList(videos);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((VideoPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void sortButton()
    {
        Button sortBut = (Button) findViewById(R.id.video_page_button);
        sortBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    private void loadAdapter()
    {
        recyclerView = findViewById(R.id.video_page_list);
        db = FirebaseFirestore.getInstance();
        videos = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");
        videoAdapter = new VideoAdapter(videos, this) {};
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(videoAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(VideoPage.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.video_page_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.home_page_popup_menu1)
                {
                    loadVideoListByNewest();
                }
                if (item.getItemId() == R.id.home_page_popup_menu2)
                {
                    loadVideoListByViews();
                }
                if (item.getItemId() == R.id.home_page_popup_menu3)
                {
                    loadVideoListByRelevant();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void searchVideoByTitle ()
    {
        TextInputEditText inputEditText = (TextInputEditText) findViewById(R.id.video_page_edittext);
        Button searchBut = (Button) findViewById(R.id.video_page_button1);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String title = inputEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
                if (title.isEmpty()) {
                    loadVideoListByNewest();
                    pd.dismiss();
                }
                else
                {
                    inputEditText.setText("");
                    CollectionReference ref = db.collection("Videos");
                    ref.whereArrayContainsAny("title_array", Arrays.asList(title.split(" "))).orderBy("date_posted", Query.Direction.DESCENDING)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    videos = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Video video = new Video();
                                        video.setTitle(document.getString("title"));
                                        video.setDescription(document.getString("description"));
                                        video.setView(Integer.parseInt(document.get("total_views").toString()));
                                        video.setDatePosted(document.getDate("date_posted"));
                                        video.setUrl(document.getString("link"));
                                        video.setVideoId(document.getId());
                                        videos.add(video);
                                    }
                                    videoAdapter.setVideoList(videos);
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText((VideoPage.this), "No data found in Database", Toast.LENGTH_SHORT);
                                    pd.dismiss();
                                }
                            });
                }
            }
        });
    }

    private void readSharedPref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        accountType = sh.getString("accountType", "");
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.video_page_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(VideoPage.this, HomePage.class);
                    VideoPage.this.finish();
                    VideoPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(VideoPage.this, VideoPage.class);
                    VideoPage.this.finish();
                    VideoPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(VideoPage.this, EventPage.class);
                    VideoPage.this.finish();
                    VideoPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(VideoPage.this, SavedPage.class);
                    VideoPage.this.finish();
                    VideoPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(VideoPage.this, ToolbarPage.class);
                    VideoPage.this.finish();
                    VideoPage.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}