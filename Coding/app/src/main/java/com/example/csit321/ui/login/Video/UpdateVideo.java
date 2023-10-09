package com.example.csit321.ui.login.Video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.csit321.EntityClass.Event;
import com.example.csit321.EntityClass.Video;
import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Event.UpdateEvent;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateVideo extends AppCompatActivity {

    private static final String TAG = "UpdateVideo";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LINK = "link";
    private static final String KEY_ARRAY_OF_TITLE = "title_array";

    private FirebaseFirestore db;
    private Video video;
    private TextInputEditText title, description, link;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_video);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        setData(getVideoId()); //show the current video's details for admin
        updateVideo(getVideoId()); //confirm to update the video's details
        activeBackBut(); //back button
        bottomNavFunction(); //bottom navigation bar
        deleteButton(getVideoId()); //delete video
    }

    private void deleteButton(String videoId)
    {
        Button button = findViewById(R.id.update_video_button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateVideo.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Do you want to delete this video?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteVideo(videoId);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
    }

    private void deleteVideo(String videoId)
    {
        pd.show();
        DocumentReference docRef = db.collection("Videos").document(videoId);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateVideo.this, "Delete video successfully.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateVideo.this, VideoPage.class));
                finish();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateVideo.this, "Error"+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void updateVideo (String videoId)
    {
        Button postBut = (Button) findViewById(R.id.update_video_button3);
        postBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String titleTxt = title.getText().toString();
                String descriptionTxt = description.getText().toString();
                String linkTxt = link.getText().toString();

                if (titleTxt.isEmpty() || descriptionTxt.isEmpty() || linkTxt.isEmpty()) {
                    Toast.makeText(UpdateVideo.this, "Missing fields. Please try again", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else
                {
                    Map<String, Object> updateVide = new HashMap<>();
                    updateVide.put(KEY_TITLE, titleTxt);
                    updateVide.put(KEY_DESCRIPTION, descriptionTxt);
                    updateVide.put(KEY_LINK, linkTxt);
                    updateVide.put(KEY_ARRAY_OF_TITLE, Arrays.asList(titleTxt.trim().toLowerCase(Locale.ROOT).split(" ")));
                    db.collection("Videos").document(videoId).update(updateVide).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UpdateVideo.this, "Update video successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UpdateVideo.this, VideoPage.class));
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateVideo.this, "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void activeBackBut()
    {
        Button backBut = (Button) findViewById(R.id.update_video_button);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateVideo.this, VideoPage.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void setData (String videoId)
    {
        DocumentReference docRef = db.collection("Videos").document(videoId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        video = new Video ();
                        video.setVideoId(document.getId());
                        video.setTitle(document.getString("title"));
                        video.setDescription(document.getString("description"));
                        video.setUrl(document.getString("link"));


                        title = findViewById(R.id.update_video_edittext);
                        title.setText(video.getTitle());

                        description = findViewById(R.id.update_video_edittext1);
                        description.setText(video.getDescription());

                        link = findViewById(R.id.update_video_edittext2);
                        link.setText((video.getUrl()));

                    }
                    else
                    {
                        Log.d(TAG, "Cannot find video");
                    }
                }
                else
                {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(UpdateVideo.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getVideoId()
    {
        String videoId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoId = extras.getString("videoId");
        }
        return videoId;
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.update_video_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(UpdateVideo.this, HomePage.class);
                    UpdateVideo.this.finish();
                    UpdateVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(UpdateVideo.this, VideoPage.class);
                    UpdateVideo.this.finish();
                    UpdateVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(UpdateVideo.this, EventPage.class);
                    UpdateVideo.this.finish();
                    UpdateVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(UpdateVideo.this, SavedPage.class);
                    UpdateVideo.this.finish();
                    UpdateVideo.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(UpdateVideo.this, ToolbarPage.class);
                    UpdateVideo.this.finish();
                    UpdateVideo.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}