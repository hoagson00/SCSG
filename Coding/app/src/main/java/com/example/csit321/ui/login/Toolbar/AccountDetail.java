package com.example.csit321.ui.login.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.AdminManagement.AccountManagement;
import com.example.csit321.ui.login.AdminManagement.NewNotification;
import com.example.csit321.ui.login.AdminManagement.Statistics;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountDetail extends AppCompatActivity {

    private static final String TAG = "AccountDetail";
    private static final String MyPreferences = "MyPrefs";

    private FirebaseFirestore db;
    private String userId, accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Show the user's information
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        db = FirebaseFirestore.getInstance();
        getSharePref(); //get the user's information
        setData(); //Show the user's details
        passwordButton(); //Show/hide password button
        updateButton(); //Update user's details
        backButton(); //back button
        statisticButton(); //show statistic button (for admin only)
        userManagementButton(); //check list of users in system (for admin only)
        newNotificationButton(); //create new notification (for admin only)
        bottomNavFunction(); //bottom navigation bar
    }

    private void newNotificationButton()
    {
        Button button = findViewById(R.id.account_detail_button6);
        button.setVisibility(View.INVISIBLE);
        if (accountType != null && accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AccountDetail.this, NewNotification.class));
                    finish();
                }
            });
        }

    }

    private void userManagementButton()
    {
        Button button = findViewById(R.id.account_detail_button5);
        button.setVisibility(View.INVISIBLE);
        if (accountType != null && accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AccountDetail.this, AccountManagement.class));
                    finish();
                }
            });
        }
    }

    private void statisticButton()
    {
        Button button = findViewById(R.id.account_detail_button4);
        button.setVisibility(View.INVISIBLE);
        if (accountType != null && accountType.equals("admin"))
        {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AccountDetail.this, Statistics.class));
                    finish();
                }
            });
        }

    }

    private void backButton()
    {
        Button button = findViewById(R.id.account_detail_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountDetail.this, ToolbarPage.class));
                finish();
            }
        });
    }

    private void updateButton()
    {
        Button button = findViewById(R.id.account_detail_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountDetail.this, UpdateAccount.class));
                finish();
            }
        });
    }

    private void passwordButton()
    {
        TextView password = (TextView) findViewById(R.id.account_detail_textView2);
        Button hideButton = (Button) findViewById(R.id.account_detail_button3);
        Button showButton = (Button) findViewById(R.id.account_detail_button2);

        showButton.setVisibility(View.INVISIBLE);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod(null);
                showButton.setVisibility(View.VISIBLE);
                hideButton.setVisibility(View.INVISIBLE);
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setTransformationMethod(new PasswordTransformationMethod());
                showButton.setVisibility(View.INVISIBLE);
                hideButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setData()
    {
        if (userId != null || userId.equals(""))
        {
            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            User user = new User();
                            user.setUsername(document.getString("username"));
                            user.setPassword(document.getString("password"));
                            user.setFullName(document.getString("full_name"));
                            user.setAccountType(document.getString("purpose"));

                            TextView fullName = findViewById(R.id.account_detail_textView);
                            TextView username = findViewById(R.id.account_detail_textView1);
                            TextView password = findViewById(R.id.account_detail_textView2);
                            TextView purpose = findViewById(R.id.account_detail_textView3);

                            fullName.setText("Full name: "+ user.getFullName());
                            username.setText("Username: "+user.getUsername());
                            password.setText("Password: "+user.getPassword());
                            purpose.setText("Purpose: "+user.getAccountType());
                        }
                        else
                            Toast.makeText(AccountDetail.this, "Cannot find User", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AccountDetail.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountDetail.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getSharePref()
    {
        SharedPreferences sh = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        userId = sh.getString("userId","");
        accountType = sh.getString("accountType", "");
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.account_detail_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(AccountDetail.this, HomePage.class);
                    AccountDetail.this.finish();
                    AccountDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(AccountDetail.this, VideoPage.class);
                    AccountDetail.this.finish();
                    AccountDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(AccountDetail.this, EventPage.class);
                    AccountDetail.this.finish();
                    AccountDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(AccountDetail.this, SavedPage.class);
                    AccountDetail.this.finish();
                    AccountDetail.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(AccountDetail.this, ToolbarPage.class);
                    AccountDetail.this.finish();
                    AccountDetail.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}