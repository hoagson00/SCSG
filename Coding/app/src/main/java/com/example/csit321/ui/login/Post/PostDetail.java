package com.example.csit321.ui.login.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.example.csit321.EntityClass.Post;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.ui.login.AdminManagement.Statistics;
import com.example.csit321.ui.login.Event.EventPage;
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

public class PostDetail extends AppCompatActivity {

    private static final String TAG = "PostDetail";

    private Button backBut, likeBlackBut, likeRedBut;
    private TextView title, description, hashtag, date;
    private ImageView imageView;

    private FirebaseFirestore db;
    private DateFormat dateFormat;
    ProgressDialog pd;

    private String userId, accountType;


    private static final String MyPreferences = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        db = FirebaseFirestore.getInstance();
        dateFormat = new DateFormat();

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!");
        pd.show();

        readSharedPref(); //read the user's information
        checkLikeStatus(getPostId()); //Check the like button if user liked the post
        bottomNavFunction(); //bottom navigation bar
        loadPost(getPostId()); //load the post
        backBut(checkSendStatus());// back button
        likeBut(getPostId(), checkSendStatus()); //like button
        updateBut(getPostId()); //update button
        pd.dismiss();
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

    private void checkLikeStatus(String postId)
    {
        likeRedBut = (Button) findViewById(R.id.post_detail_button3);
        likeBlackBut = (Button) findViewById(R.id.post_detail_button1);
        if (userId.equals(""))
        {
            likeRedBut.setVisibility(View.INVISIBLE);
            likeBlackBut.setVisibility(View.VISIBLE);
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
                            List<String> likedPosts = new ArrayList<>();
                            likedPosts = (List<String>) document.get("posts_liked");
                            if (likedPosts.contains(postId) && likedPosts!= null)
                            {
                                likeRedBut.setVisibility(View.VISIBLE);
                                likeBlackBut.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                likeRedBut.setVisibility(View.INVISIBLE);
                                likeBlackBut.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else
                    {
                        Log.d(TAG, "Get failed with"+ task.getException());
                        Toast.makeText(PostDetail.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateBut(String postId)
    {
        Button button = findViewById(R.id.post_detail_button5);
        button.setVisibility(View.INVISIBLE);
        if (accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostDetail.this, UpdatePost.class);
                    intent.putExtra("postId", postId);
                    finish();
                    startActivity(intent);
                }
            });
        }
    }

    private void redLikeBut(String postId, String status)
    {
        likeRedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(userId).update("posts_liked", FieldValue.arrayRemove(postId))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    db.collection("Posts").document(postId).update("total_likes", FieldValue.increment(-1));
                                    likeBlackBut.setVisibility(View.VISIBLE);
                                    likeRedBut.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(PostDetail.this, PostDetail.class);
                                    intent.putExtra("postId", postId);
                                    intent.putExtra("from", status);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PostDetail.this, "Unliked post failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void blackLikeBut (String postId, String status)
    {
        likeBlackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.equals(""))
                {
                    Toast.makeText(PostDetail.this, "Please log in first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.collection("Users").document(userId).update("posts_liked", FieldValue.arrayUnion(postId))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        db.collection("Posts").document(postId).update("total_likes", FieldValue.increment(1));
                                        likeBlackBut.setVisibility(View.INVISIBLE);
                                        likeRedBut.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(PostDetail.this, PostDetail.class);
                                        intent.putExtra("postId", postId);
                                        intent.putExtra("from", status);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostDetail.this, "Liked post failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void likeBut(String postId, String status)
    {
        redLikeBut(postId, status);
        blackLikeBut(postId, status);

    }

    private void loadImage(String imageId)
    {
        imageView = (ImageView) findViewById(R.id.post_detail_imageView);
        if (imageId == null || imageId.equals(""))
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
                        Toast.makeText(PostDetail.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private String getPostId()
    {
        String postId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            postId = extras.getString("postId");
        }
        return postId;
    }

    private void loadPost(String postId)
    {
        DocumentReference docRef = db.collection("Posts").document(postId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Post post = new Post();
                        post.setPostID(document.getId());
                        post.setTitle(document.getString("title"));
                        post.setDescription(document.getString("description"));
                        post.setHashtag(document.getString("hashtag"));
                        post.setLikedCount(Integer.parseInt(document.get("total_likes").toString()));
                        post.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                        post.setImageId(document.getString("image_id"));
                        post.setDate(document.getDate("date_posted"));
//                        try {
//                            post.setDate(dateFormat.StringToDate1(document.getString("date_posted")));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }

                        title = (TextView) findViewById(R.id.post_detail_textView);
                        title.setText(post.getTitle());

                        description = (TextView) findViewById(R.id.post_detail_textView1);
                        description.setText(post.getDescription());

                        hashtag = (TextView) findViewById(R.id.post_detail_textView2);
                        hashtag.setText(post.getHashtag());

                        date = (TextView) findViewById(R.id.post_detail_textView3);
                        try {
                            date.setText("Posted in: " + dateFormat.dateToString1(post.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        TextView viewAndLike = (TextView) findViewById(R.id.post_detail_textView4);
                        viewAndLike.setText("Likes: "+ post.getLikedCount()+"\n"+ "Views: "+(post.getViewCount()+1));

                        loadImage(post.getImageId());
                        updateView(postId, post.getViewCount());
                    }
                    else
                    {
                        Log.d(TAG, "Cannot find post");
                    }
                }
                else
                {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(PostDetail.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateView(String postId, int viewCount)
    {
        db.collection("Posts").document(postId).update("total_views", (viewCount+1));
    }

    private void backBut(String status)
    {
        backBut = (Button) findViewById(R.id.post_detail_button);
        if (status != null && status.equals("SavedPage"))
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(PostDetail.this, SavedPage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                }
            });
        }
        else if (status != null && status.equals("Statistics"))
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(PostDetail.this, Statistics.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                }
            });
        }
        else
        {
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(PostDetail.this, HomePage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
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
        BottomNavigationView nav = findViewById(R.id.post_detail_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(PostDetail.this, HomePage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(PostDetail.this, VideoPage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(PostDetail.this, EventPage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(PostDetail.this, SavedPage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(PostDetail.this, ToolbarPage.class);
                    PostDetail.this.finish();
                    PostDetail.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}