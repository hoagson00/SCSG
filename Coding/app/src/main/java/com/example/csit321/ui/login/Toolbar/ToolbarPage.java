package com.example.csit321.ui.login.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Support.AboutUs;
import com.example.csit321.ui.login.Support.Support;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ToolbarPage extends AppCompatActivity {

    private static final String MyPreferences = "MyPrefs";

    private static final String TAG = "ToolbarPage";

    private String userId, accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Show all the function of this app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_page);

        readSharedPref(); //get the user's details
        showUserInfo(); //show the user's details
        homeButton(); //home button
        videoButton(); //video button
        eventButton(); //event button
        savedButton(); //saved button
        bottomNavFunction(); //bottom navigation bar
        supportButton(); //support page
        notificationButton(); //notification page
        getInvolved(); //get involved page
        aboutUsButton(); //about us page
    }

    private void aboutUsButton()
    {
        ImageButton button = findViewById(R.id.toolbar_page_button8);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ToolbarPage.this, AboutUs.class));
                finish();
            }
        });
    }

    private void notificationButton()
    {
        ImageButton button = findViewById(R.id.toolbar_page_button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ToolbarPage.this, Notification.class));
                finish();
            }
        });
    }

    private void getInvolved()
    {
        ImageButton button = findViewById(R.id.toolbar_page_button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/volunteer-2/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void supportButton()
    {
        ImageButton button = findViewById(R.id.toolbar_page_button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ToolbarPage.this, Support.class));
                finish();
            }
        });
    }

    private void homeButton()
    {
        ImageButton button = (ImageButton) findViewById(R.id.toolbar_page_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ToolbarPage.this, HomePage.class);
                ToolbarPage.this.finish();
                ToolbarPage.this.startActivity(myIntent);
            }
        });
    }

    private void videoButton()
    {
        ImageButton button = (ImageButton) findViewById(R.id.toolbar_page_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ToolbarPage.this, VideoPage.class);
                ToolbarPage.this.finish();
                ToolbarPage.this.startActivity(myIntent);
            }
        });
    }

    private void eventButton()
    {
        ImageButton button = (ImageButton) findViewById(R.id.toolbar_page_button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ToolbarPage.this, EventPage.class);
                ToolbarPage.this.finish();
                ToolbarPage.this.startActivity(myIntent);
            }
        });
    }

    private void savedButton()
    {
        ImageButton button = (ImageButton) findViewById(R.id.toolbar_page_button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ToolbarPage.this, SavedPage.class);
                ToolbarPage.this.finish();
                ToolbarPage.this.startActivity(myIntent);
            }
        });
    }

    private void showUserInfo()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (userId == null || userId.equals(""))
        {
            fragmentManager.beginTransaction()
                    .replace(R.id.toolbar_page_frag_container, ToolbarFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name")
                    .commit();
        }
        else
        {
            fragmentManager.beginTransaction()
                    .replace(R.id.toolbar_page_frag_container, ToolbarFragment1.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name")
                    .commit();
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
        BottomNavigationView nav = findViewById(R.id.toolbar_page_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(ToolbarPage.this, HomePage.class);
                    ToolbarPage.this.finish();
                    ToolbarPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(ToolbarPage.this, VideoPage.class);
                    ToolbarPage.this.finish();
                    ToolbarPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(ToolbarPage.this, EventPage.class);
                    ToolbarPage.this.finish();
                    ToolbarPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(ToolbarPage.this, SavedPage.class);
                    ToolbarPage.this.finish();
                    ToolbarPage.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(ToolbarPage.this, ToolbarPage.class);
                    ToolbarPage.this.finish();
                    ToolbarPage.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}