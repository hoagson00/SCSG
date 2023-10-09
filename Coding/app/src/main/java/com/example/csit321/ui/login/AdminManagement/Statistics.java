package com.example.csit321.ui.login.AdminManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventDetail;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.PostDetail;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.AccountDetail;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Statistics extends AppCompatActivity {

    private static final String TAG = "Statistics";
    private static final String MyPreferences = "MyPrefs";

    private String userId, accountType;

    private TextView totalPosted, totalLikes, totalView, mostLiked, mostViewed, recentPost;
    private Button mostLikeBut, mostViewBut, recentBut;
    private Button postBut, videoBut, eventBut;

    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Admin can check the statistics of posts, events and video
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!!");

        getSharePref(); //read the user's information
        defineValues(); //define ids from front-end design to back-end coding
        setData("Posts"); //set up the statistic of posts
        threeTopButton(); //button to switch statistic of posts, statistic of events and statistic of videos
        backButton(); //back button
        bottomNavFunction(); //buttom navigation bar
    }

    private void setData(String collection)
    {
        pd.show();
        db.collection(collection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    int countPosts = task.getResult().size();
                    int totalLikes = 0;
                    int totalViews = 0;
                    for (QueryDocumentSnapshot document: task.getResult())
                    {
                        int views = Integer.parseInt(document.get("total_views").toString());
                        totalViews += views;

                        if (collection.equals("Posts"))
                        {
                            int likes = Integer.parseInt(document.get("total_likes").toString());
                            totalLikes += likes;

                            setUpButton(collection, "total_likes");
                            setUpButton(collection, "total_views");
                            setUpButton(collection, "date_posted");
                        }

                        if (collection.equals("Events"))
                        {
                            int likes = Integer.parseInt(document.get("total_subscribes").toString());
                            totalLikes += likes;

                            setUpButton(collection, "total_subscribes");
                            setUpButton(collection, "total_views");
                            setUpButton(collection, "date_posted");
                        }

                        if (collection.equals("Videos"))
                        {
                            setUpButton(collection, "total_views");
                            setUpButton(collection, "date_posted");
                        }
                    }
                    setTotalValues(collection, countPosts, totalViews, totalLikes);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText(Statistics.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Statistics.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void setUpButton(String collection, String field)
    {
        db.collection(collection).orderBy(field, Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty())
                    {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String title = document.getString("title");
                        String name = document.getString("name");
                        String id = document.getId();
                        if (collection.equals("Posts"))
                        {

                            if (field.equals("total_likes"))
                            {
                                mostLikeBut.setText(title);
                                mostLikeBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, PostDetail.class);
                                        intent.putExtra("postId", id);
                                        intent.putExtra("from", "Statistics");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            if (field.equals("total_views"))
                            {
                                mostViewBut.setText(title);
                                mostViewBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, PostDetail.class);
                                        intent.putExtra("postId", id);
                                        intent.putExtra("from", "Statistics");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            if (field.equals("date_posted"))
                            {
                                recentBut.setText(title);
                                recentBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, PostDetail.class);
                                        intent.putExtra("postId", id);
                                        intent.putExtra("from", "Statistics");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                        if (collection.equals("Videos"))
                        {
                            if (field.equals("total_views"))
                            {
                                mostLikeBut.setText(title);
                                mostLikeBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, VideoPage.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            if (field.equals("date_posted"))
                            {
                                mostViewBut.setText(title);
                                mostViewBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, VideoPage.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                        if(collection.equals("Events"))
                        {
                            if (field.equals("total_subscribes"))
                            {
                                mostLikeBut.setText(name);
                                mostLikeBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, EventDetail.class);
                                        intent.putExtra("event_id", id);
                                        intent.putExtra("from", "Statistics");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            if (field.equals("total_views"))
                            {
                                mostViewBut.setText(name);
                                mostViewBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, EventDetail.class);
                                        intent.putExtra("event_id", id);
                                        intent.putExtra("from", "Statistics");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            if (field.equals("date_posted"))
                            {
                                recentBut.setText(name);
                                recentBut.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Statistics.this, EventDetail.class);
                                        intent.putExtra("event_id", id);
                                        intent.putExtra("from", "Statistics");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(Statistics.this, "Cannot find "+collection, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Statistics.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Statistics.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void defineValues()
    {
        totalPosted = findViewById(R.id.statistics_view1);
        totalLikes = findViewById(R.id.statistics_view2);
        totalView = findViewById(R.id.statistics_view3);
        mostLiked = findViewById(R.id.statistics_view6);
        mostViewed = findViewById(R.id.statistics_view4);
        recentPost = findViewById(R.id.statistics_view5);


        mostLikeBut = findViewById(R.id.statistics_button4);
        mostViewBut = findViewById(R.id.statistics_button5);
        recentBut = findViewById(R.id.statistics_button6);

        postBut = findViewById(R.id.statistics_button1);
        videoBut = findViewById(R.id.statistics_button2);
        eventBut = findViewById(R.id.statistics_button3);
    }

    private void setTotalValues(String collection, int count, int views, int likes)
    {
        if (collection.equals("Posts"))
        {
            totalPosted.setText("Total Posts: "+count);
            totalLikes.setText("Total Likes: "+likes);
            totalView.setText("Total Views: "+views);
            mostLiked.setText("The post with the most likes: ");
            mostViewed.setText("The post with the most views: ");
            recentPost.setText("The most recent post: ");
        }
        if (collection.equals("Events"))
        {
            totalPosted.setText("Total Events: "+count);
            totalLikes.setText("Total Subscribe: "+likes);
            totalView.setText("Total Views: "+views);
            mostLiked.setText("The events with the most subscribe: ");
            mostViewed.setText("The events with the most views: ");
            recentPost.setText("The most recent events: ");
        }
        if (collection.equals("Videos"))
        {
            totalPosted.setText("Total Videos: "+count);
            totalLikes.setText("Total Views: "+views);
            mostLiked.setText("The videos with the most views: ");
            mostViewed.setText("The most recent videos: ");
        }
    }

    private void postButton()
    {
        postBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                videoBut.setBackgroundColor(getResources().getColor(R.color.white));
                videoBut.setTextColor(getResources().getColor(R.color.black));
                eventBut.setBackgroundColor(getResources().getColor(R.color.white));
                eventBut.setTextColor(getResources().getColor(R.color.black));
                postBut.setBackgroundColor(getResources().getColor(R.color.blue));
                postBut.setTextColor(getResources().getColor(R.color.white));

                recentPost.setVisibility(View.VISIBLE);
                recentBut.setVisibility(View.VISIBLE);
                totalView.setVisibility(View.VISIBLE);
                setData("Posts");
            }
        });
    }

    private void videoButton()
    {
        videoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postBut.setBackgroundColor(getResources().getColor(R.color.white));
                postBut.setTextColor(getResources().getColor(R.color.black));
                eventBut.setBackgroundColor(getResources().getColor(R.color.white));
                eventBut.setTextColor(getResources().getColor(R.color.black));
                videoBut.setBackgroundColor(getResources().getColor(R.color.blue));
                videoBut.setTextColor(getResources().getColor(R.color.white));

                recentPost.setVisibility(View.INVISIBLE);
                recentBut.setVisibility(View.INVISIBLE);
                totalView.setVisibility(View.INVISIBLE);
                setData("Videos");
            }
        });
    }

    private void eventButton()
    {
        eventBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postBut.setBackgroundColor(getResources().getColor(R.color.white));
                postBut.setTextColor(getResources().getColor(R.color.black));
                videoBut.setBackgroundColor(getResources().getColor(R.color.white));
                videoBut.setTextColor(getResources().getColor(R.color.black));
                eventBut.setBackgroundColor(getResources().getColor(R.color.blue));
                eventBut.setTextColor(getResources().getColor(R.color.white));

                recentPost.setVisibility(View.VISIBLE);
                recentBut.setVisibility(View.VISIBLE);
                totalView.setVisibility(View.VISIBLE);
                setData("Events");
            }
        });
    }

    private void threeTopButton()
    {
        postButton();
        videoButton();
        eventButton();
    }

    private void backButton()
    {
        Button button = findViewById(R.id.statistics_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Statistics.this, AccountDetail.class));
                finish();
            }
        });
    }

    private void getSharePref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        userId = sh.getString("userId","");
        accountType = sh.getString("accountType", "");
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.statistics_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(Statistics.this, HomePage.class);
                    Statistics.this.finish();
                    Statistics.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(Statistics.this, VideoPage.class);
                    Statistics.this.finish();
                    Statistics.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(Statistics.this, EventPage.class);
                    Statistics.this.finish();
                    Statistics.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(Statistics.this, SavedPage.class);
                    Statistics.this.finish();
                    Statistics.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(Statistics.this, ToolbarPage.class);
                    Statistics.this.finish();
                    Statistics.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }


}