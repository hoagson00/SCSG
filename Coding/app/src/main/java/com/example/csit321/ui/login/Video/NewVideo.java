package com.example.csit321.ui.login.Video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.csit321.EntityClass.Token;
import com.example.csit321.R;
import com.example.csit321.SupportClass.FCMSend;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Event.NewEvent;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewVideo extends AppCompatActivity {

    private static final String TAG = "NewVideo";

    private FirebaseFirestore db;

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LINK = "link";
    private static final String KEY_DATE = "date_posted";
    private static final String KEY_VIEW = "total_views";
    private static final String KEY_ARRAY_OF_TITLE = "title_array";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create a new video (for admin only)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_video);
        bottomNavFunction(); //bottom navigator button
        activeBackBut(); //back button
        addVideo(); //confirm to create new video
    }

    private void sendNotification() {
        String title = "New Video";
        String message = "Please check out the new video";

        if (!title.equals("") && !message.equals("")) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Please Wait!!");
            pd.show();
            FirebaseFirestore.getInstance().collection("Tokens").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Token token = new Token();
                            token.setToken(document.getString("token"));
                            FCMSend.pushNotification(
                                    NewVideo.this,
                                    token.getToken(),
                                    title,
                                    message
                            );
                        }
                        startActivity(new Intent(NewVideo.this, VideoPage.class));
                        pd.dismiss();
                    } else {
                        Log.d(TAG, "Error: " + task.getException().toString());
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    pd.dismiss();
                }
            });
        }
    }

    private void addVideo()
    {
        db = FirebaseFirestore.getInstance();

        TextInputEditText title = (TextInputEditText) findViewById(R.id.new_video_edittext);
        TextInputEditText description = (TextInputEditText) findViewById(R.id.new_video_edittext1);
        TextInputEditText link = (TextInputEditText) findViewById(R.id.new_video_edittext2);

        Button postBut = (Button) findViewById(R.id.new_video_button3);
        postBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleTxt = title.getText().toString();
                String descriptionTxt = description.getText().toString();
                String linkTxt = link.getText().toString();

                if (titleTxt.isEmpty() || descriptionTxt.isEmpty() || linkTxt.isEmpty()) {
                    Toast.makeText(NewVideo.this, "Missing fields. Please try again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String videoId = db.collection("Videos").document().getId();
                    Map<String, Object> newVideo = new HashMap<>();
                    newVideo.put(KEY_TITLE, titleTxt);
                    newVideo.put(KEY_DESCRIPTION, descriptionTxt);
                    newVideo.put(KEY_LINK, linkTxt);
                    newVideo.put(KEY_DATE, new Date());
                    newVideo.put(KEY_VIEW, 0);
                    newVideo.put(KEY_ARRAY_OF_TITLE, Arrays.asList(titleTxt.trim().toLowerCase(Locale.ROOT).split(" ")));
                    db.collection("Videos").document(videoId).set(newVideo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            sendNotification();
                            Toast.makeText(NewVideo.this, "New video added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewVideo.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void activeBackBut()
    {
        Button backBut = (Button) findViewById(R.id.new_video_button);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(NewVideo.this,VideoPage.class);
                NewVideo.this.finish();
                NewVideo.this.startActivity(myIntent);
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.new_video_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(NewVideo.this, HomePage.class);
                    NewVideo.this.finish();
                    NewVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(NewVideo.this, VideoPage.class);
                    NewVideo.this.finish();
                    NewVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(NewVideo.this, EventPage.class);
                    NewVideo.this.finish();
                    NewVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(NewVideo.this, SavedPage.class);
                    NewVideo.this.finish();
                    NewVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(NewVideo.this, ToolbarPage.class);
                    NewVideo.this.finish();
                    NewVideo.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}