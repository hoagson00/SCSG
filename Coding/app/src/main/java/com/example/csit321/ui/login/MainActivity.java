package com.example.csit321.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.AdminManagement.AccountManagement;
import com.example.csit321.ui.login.AdminManagement.Statistics;
import com.example.csit321.ui.login.Event.EventDetail;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Event.NewEvent;
import com.example.csit321.ui.login.LoginMenu.MainLogin;
import com.example.csit321.ui.login.LoginMenu.UserRegister;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.NewPost;
import com.example.csit321.ui.login.Toolbar.AccountDetail;
import com.example.csit321.ui.login.Toolbar.Notification;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.NewVideo;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String MyPreferences = "MyPrefs";
    private String userId, accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSharedPref();
        homePageBut();
        mainLoginBut();
    }

    private void readSharedPref()
    {
        //Check the user's information from the last login
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        userId = sh.getString("userId","");
        sharePrefs(userId);
        sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        accountType = sh.getString("accountType", "");
    }

    public void sharePrefs (String id)
    {
        // if userid is not null -> share the userid to other functions
        if (!userId.equals(""))
        {
            FirebaseFirestore.getInstance().collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            String userType = document.getString("type");

                            SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId",id);
                            editor.putString("accountType", userType);
                            editor.commit();
                        }
                    }
                    else
                    {
                        Log.d(TAG, "Error: "+task.getException().toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error: "+e.getMessage());
                }
            });
        }
    }

    private void homePageBut()
    {
        Button button = findViewById(R.id.main_activity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HomePage.class));
                finish();
            }
        });
    }
    private void mainLoginBut()
    {
        Button button = findViewById(R.id.main_activity_button1);
        button.setVisibility(View.INVISIBLE);
        if (userId == "")
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, MainLogin.class));
                    finish();
                }
            });
        }
    }
}