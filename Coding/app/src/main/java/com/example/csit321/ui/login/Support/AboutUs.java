package com.example.csit321.ui.login.Support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //About Us Page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        backButton();
        ourHistoryButton();
        whoWeAreButton();
        ourTeamButton();
        ourFounderButton();
        essenceModelButton();
        facebookButton();
        youtubeButton();
        instagramButton();
        donateNowButton();
        bottomNavFunction();
    }

    private void donateNowButton()
    {
        Button button = findViewById(R.id.about_us_button9);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/donate/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void instagramButton()
    {
        ImageButton button = findViewById(R.id.about_us_button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.instagram.com/sisterscancersupportgroup/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void youtubeButton()
    {
        ImageButton button = findViewById(R.id.about_us_button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/channel/UClV_44f49LeyaxlKVd7aiuQ/channels";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void facebookButton()
    {
        ImageButton button = findViewById(R.id.about_us_button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.facebook.com/scsgillawarra/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void essenceModelButton()
    {
        Button button = findViewById(R.id.about_us_button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/essence-model/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void ourFounderButton()
    {
        Button button = findViewById(R.id.about_us_button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/our-founder/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void ourTeamButton()
    {
        Button button = findViewById(R.id.about_us_button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/our-team/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void whoWeAreButton()
    {
        Button button = findViewById(R.id.about_us_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/about-us/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void ourHistoryButton()
    {
        Button button = findViewById(R.id.about_us_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://scsg.org.au/our-history/";
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    private void backButton()
    {
        Button button = findViewById(R.id.about_us_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUs.this, ToolbarPage.class));
                finish();
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.about_us_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(AboutUs.this, HomePage.class);
                    AboutUs.this.finish();
                    AboutUs.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(AboutUs.this, VideoPage.class);
                    AboutUs.this.finish();
                    AboutUs.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(AboutUs.this, EventPage.class);
                    AboutUs.this.finish();
                    AboutUs.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(AboutUs.this, SavedPage.class);
                    AboutUs.this.finish();
                    AboutUs.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(AboutUs.this, ToolbarPage.class);
                    AboutUs.this.finish();
                    AboutUs.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}