package com.example.csit321.ui.login.AdminManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.csit321.EntityClass.Token;
import com.example.csit321.R;
import com.example.csit321.SupportClass.FCMSend;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.AccountDetail;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NewNotification extends AppCompatActivity {

    private static final String TAG = "NewNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Admin creates a new notification to all user
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification);
        backButton(); //back button

        sendButton(); //send notification button
        bottomNavFunction(); //bottom navigation bar
    }

    private void sendButton()
    {
        EditText mTitle = findViewById(R.id.new_notification_text);
        EditText mMessage = findViewById(R.id.new_notification_text1);

        Button button = findViewById(R.id.new_notification_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString().trim();
                String message = mMessage.getText().toString().trim();

                if (!title.equals("") && !message.equals(""))
                {
                    ProgressDialog pd = new ProgressDialog(NewNotification.this);
                    pd.setMessage("Please Wait!!");
                    pd.show();

                    FirebaseFirestore.getInstance().collection("Tokens").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                for (QueryDocumentSnapshot document: task.getResult())
                                {
                                    Token token = new Token();
                                    token.setToken(document.getString("token"));
                                    FCMSend.pushNotification(
                                            NewNotification.this,
                                            token.getToken(),
                                            title,
                                            message
                                    );
                                }
                                pd.dismiss();
                            }
                            else
                            {
                                Log.d(TAG, "Error: "+task.getException().toString());
                                pd.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error: "+e.getMessage());
                            pd.dismiss();
                        }
                    });
                    pd.dismiss();
                    startActivity(new Intent(NewNotification.this, AccountDetail.class));
                    finish();
                }
            }
        });
    }

    private void backButton()
    {
        Button button = findViewById(R.id.new_notification_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewNotification.this, AccountDetail.class));
                finish();
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.new_notification_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(NewNotification.this, HomePage.class);
                    NewNotification.this.finish();
                    NewNotification.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(NewNotification.this, VideoPage.class);
                    NewNotification.this.finish();
                    NewNotification.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(NewNotification.this, EventPage.class);
                    NewNotification.this.finish();
                    NewNotification.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(NewNotification.this, SavedPage.class);
                    NewNotification.this.finish();
                    NewNotification.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(NewNotification.this, ToolbarPage.class);
                    NewNotification.this.finish();
                    NewNotification.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}