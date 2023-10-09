package com.example.csit321.ui.login.Post;

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

import com.example.csit321.Adapter.PostAdapter;
import com.example.csit321.EntityClass.Post;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Event.EventPage;
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
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private static final String MyPreferences = "MyPrefs";

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList <Post> posts;
    private FirebaseFirestore db;
    private DateFormat dateFormat;
    ProgressDialog pd;

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getSharePref(); //get the information of user
        loadAdapter(); //Setup the details of items
        sortButton(); //Sort button
        loadPostListByNewest(); //Load the lists
        bottomNavFunction(); // the bottom navigation bar
        searchPostByTitle(); //search function
        addNewPost(); //add new post function
    }

    private void loadAdapter()
    {
        recyclerView = findViewById(R.id.home_page_list);
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

    private void sortButton()
    {
        Button sortBut = (Button) findViewById(R.id.home_page_button);
        sortBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    private void loadPostListByNewest(){
        posts = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!!");
        pd.show();

        dateFormat = new DateFormat();
        db.collection("Posts").orderBy("date_posted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Post post = new Post();
                        post.setPostID(document.getId());
                        post.setTitle(document.getString("title"));
                        post.setDescription(document.getString("description"));
                        post.setHashtag(document.getString("hashtag"));
                        post.setLikedCount(Integer.parseInt(document.get("total_likes").toString()));
                        post.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                        post.setImageId(document.getString("image_id"));

                        //                            post.setDate(dateFormat.StringToDate1(document.getString("date_posted")));
                        post.setDate(document.getDate("date_posted"));
                        posts.add(post);
                    }
                    postAdapter.setPostList(posts);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((HomePage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadPostListByLike(){
        posts = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.show();

        dateFormat = new DateFormat();
        db.collection("Posts").orderBy("total_likes", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                     for (QueryDocumentSnapshot document : task.getResult())
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((HomePage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadPostListByView(){
        posts = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.show();

        dateFormat = new DateFormat();
        db.collection("Posts").orderBy("total_views", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
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
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((HomePage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void loadPostListByRelevant(){
        posts = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.show();

        dateFormat = new DateFormat();
        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
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
                    Collections.shuffle(posts);
                    postAdapter.setPostList(posts);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText((HomePage.this), "No data found in Database", Toast.LENGTH_SHORT);
                    pd.dismiss();
                }
            }
        });
    }

    private void showPostDetail (final String postId)
    {
        Intent myIntent = new Intent(HomePage.this, PostDetail.class);
        myIntent.putExtra("postId", postId);
        HomePage.this.finish();
        HomePage.this.startActivity(myIntent);
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(HomePage.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.home_page_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.home_page_popup_menu1)
                {
                    loadPostListByNewest();
                }
                if (item.getItemId() == R.id.home_page_popup_menu2)
                {
                    loadPostListByLike();
                }
                if (item.getItemId() == R.id.home_page_popup_menu3)
                {
                    loadPostListByView();
                }
                if (item.getItemId() == R.id.home_page_popup_menu4)
                {
                    loadPostListByRelevant();
                }
//                popupMenu.getMenu().findItem(R.id.home_page_view).setVisible(false);
                return false;
            }
        });
        popupMenu.show();
    }

    private void addNewPost()
    {
        Button button = (Button) findViewById(R.id.home_page_button2);
        button.setVisibility(View.INVISIBLE);
        if (accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(HomePage.this, NewPost.class);
                    HomePage.this.finish();
                    HomePage.this.startActivity(myIntent);
                }
            });
        }
    }

    public void searchPostByTitle ()
    {
        TextInputEditText inputEditText = (TextInputEditText) findViewById(R.id.home_page_edittext);
        Button searchBut = (Button) findViewById(R.id.home_page_button1);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String title = inputEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
                if (title.isEmpty()) {
                    loadPostListByNewest();
                    pd.dismiss();
                }
                else
                {
                    inputEditText.setText("");
                    CollectionReference ref = db.collection("Posts");
                    ref.whereArrayContainsAny("title_array", Arrays.asList(title.split(" "))).orderBy("date_posted", Query.Direction.DESCENDING)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    posts = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
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
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText((HomePage.this), "No data found in Database", Toast.LENGTH_SHORT);
                                    pd.dismiss();
                                }
                            });
                }
            }
        });
    }

    private void getSharePref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        accountType = sh.getString("accountType","");
        Log.d(TAG, "accountType: "+ accountType);
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.home_page_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(HomePage.this, HomePage.class);
                    HomePage.this.finish();
                    HomePage.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(HomePage.this, VideoPage.class);
                    HomePage.this.finish();
                    HomePage.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(HomePage.this, EventPage.class);
                    HomePage.this.finish();
                    HomePage.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(HomePage.this, SavedPage.class);
                    HomePage.this.finish();
                    HomePage.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(HomePage.this, ToolbarPage.class);
                    HomePage.this.finish();
                    HomePage.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}