package com.example.csit321.ui.login.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Notification extends AppCompatActivity {

    private static final String TAG = "Notification";
    private FirebaseFirestore db;
    private FirebaseMessaging fcm;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Users can adjust to receive notifications or not.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        db = FirebaseFirestore.getInstance();
        fcm = FirebaseMessaging.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        backButton(); //back button
        switchButton(); //yes to receive notification - no is not
        bottomNavFunction();//bottom navigation bar
    }

    private void switchButton()
    {
        pd.show();
        Switch sw = findViewById(R.id.notfication_switch);
        fcm.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful())
                {
                    String tokenId = task.getResult();
                    db.collection("Tokens")
                            .document(tokenId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            sw.setChecked(true);
                                            pd.dismiss();
                                        }
                                        else
                                        {
                                            sw.setChecked(false);
                                            pd.dismiss();
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Token Failed: "+task.getException().toString());
                                        pd.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Token Failed: "+e.getMessage());
                                    pd.dismiss();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Token Failed: "+e.getMessage());
                pd.dismiss();
            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    addToken();
                    Toast.makeText(Notification.this, "Activate app notification", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    removeToken();
                    Toast.makeText(Notification.this, "Disable app notification", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToken()
    {
        pd.show();
        fcm.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                Map<String, Object> tokenMap = new HashMap<>();
                tokenMap.put("token",token);
                FirebaseFirestore.getInstance().collection("Tokens")
                        .document(token).set(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "Add token successfully");
                            pd.dismiss();
                        }
                        else
                        {
                            Log.d(TAG, "Add token failed");
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Token Failed: "+e.getMessage());
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Token Failed: "+e.getMessage());
                pd.dismiss();
            }
        });
    }

    private void removeToken()
    {
        pd.show();
        fcm.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful())
                {
                    String tokenId = task.getResult();
                    db.collection("Tokens").document(tokenId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Delete successfully ");
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Delete Failed: "+e.getMessage());
                            pd.dismiss();
                        }
                    });
                }
                else
                {
                    Log.d(TAG, "Token Failed: "+task.getException().toString());
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Token Failed: "+e.getMessage());
                pd.dismiss();
            }
        });
    }

    private void backButton()
    {
        Button button = findViewById(R.id.notification_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notification.this, ToolbarPage.class));
                finish();
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.notification_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(Notification.this, HomePage.class);
                    Notification.this.finish();
                    Notification.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(Notification.this, VideoPage.class);
                    Notification.this.finish();
                    Notification.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(Notification.this, EventPage.class);
                    Notification.this.finish();
                    Notification.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(Notification.this, SavedPage.class);
                    Notification.this.finish();
                    Notification.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(Notification.this, ToolbarPage.class);
                    Notification.this.finish();
                    Notification.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}