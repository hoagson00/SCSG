package com.example.csit321.ui.login.Support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Support extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Support page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        howWeHelpBut();
        sahaButton();
        backButton();
        bottomNavFunction();
    }

    private void backButton()
    {
        Button button = findViewById(R.id.support_page_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Support.this, ToolbarPage.class));
                finish();
            }
        });
    }

    private void sahaButton()
    {
        Button button = findViewById(R.id.support_page_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/saha/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void howWeHelpBut()
    {
        Button button = findViewById(R.id.support_page_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/how-we-help/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void bottomNavFunction() {
        BottomNavigationView nav = findViewById(R.id.support_page_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homelogo: {
                    Intent myIntent = new Intent(Support.this, HomePage.class);
                    Support.this.finish();
                    Support.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo: {
                    Intent myIntent = new Intent(Support.this, VideoPage.class);
                    Support.this.finish();
                    Support.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo: {
                    Intent myIntent = new Intent(Support.this, EventPage.class);
                    Support.this.finish();
                    Support.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo: {
                    Intent myIntent = new Intent(Support.this, SavedPage.class);
                    Support.this.finish();
                    Support.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo: {
                    Intent myIntent = new Intent(Support.this, ToolbarPage.class);
                    Support.this.finish();
                    Support.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}