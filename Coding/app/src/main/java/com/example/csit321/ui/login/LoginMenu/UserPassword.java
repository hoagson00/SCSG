package com.example.csit321.ui.login.LoginMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserPassword extends AppCompatActivity {

    private static final String MyPreferences = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password);
        passwordButton(); //show/hide password
        doneButton(); //confirm to change password
        bottomNavFunction(); //bottom navigation bar
    }

    private void doneButton()
    {
        Button button = (Button) findViewById(R.id.user_password_button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText oldPassEt = (EditText) findViewById(R.id.user_password_textinput);
                EditText newPassEt = (EditText) findViewById(R.id.user_password_textinput1);
                EditText repeatPassEt = (EditText) findViewById(R.id.user_password_textinput2);

                String oldPass = oldPassEt.getText().toString();
                String newPass = newPassEt.getText().toString();
                String repeatPass = repeatPassEt.getText().toString();

                User user = readSharedPref();
                if (!user.getPassword().equals(oldPass))
                {
                    Toast.makeText(UserPassword.this, "Old password is not match. Please try again", Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent(UserPassword.this, UserPassword.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(refresh);
                }
                else if (!newPass.equals(repeatPass))
                {
                    Toast.makeText(UserPassword.this, "New password is not match. Please try again", Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent(UserPassword.this, UserPassword.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(refresh);
                }
                else if (newPass.equals(""))
                {
                    Toast.makeText(UserPassword.this, "New password cant be blank", Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent(UserPassword.this, UserPassword.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(refresh);
                }
                else
                {
//                    AppDatabase.getDbInstance(getApplicationContext()).userDao().updatePassword(newPass, user.getUserId());
                    Toast.makeText(UserPassword.this, "Update successful", Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent(UserPassword.this, ToolbarPage.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(refresh);
                }
            }
        });
    }

    private User readSharedPref()
    {
//        SharedPreferences sh = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
//        String userId = sh.getString("userId","");
//        int accountStt = sh.getInt("accountStatus",-1);
//        User findUser = AppDatabase.getDbInstance(this.getApplicationContext()).userDao().findUserById(userId, accountStt);
//        if (findUser == null)
//            return null;
        return  null;
    }

    private void passwordButton()
    {
        EditText oldPass = (EditText) findViewById(R.id.user_password_textinput);
        EditText newPass = (EditText) findViewById(R.id.user_password_textinput1);
        EditText repeatPass = (EditText) findViewById(R.id.user_password_textinput2);
        Button hideButton = (Button) findViewById(R.id.user_password_button2);
        Button showButton = (Button) findViewById(R.id.user_password_button1);

        showButton.setVisibility(View.INVISIBLE);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass.setTransformationMethod(null);
                newPass.setTransformationMethod(null);
                repeatPass.setTransformationMethod(null);
                showButton.setVisibility(View.VISIBLE);
                hideButton.setVisibility(View.INVISIBLE);
            }
        });
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass.setTransformationMethod(new PasswordTransformationMethod());
                newPass.setTransformationMethod(new PasswordTransformationMethod());
                repeatPass.setTransformationMethod(new PasswordTransformationMethod());
                showButton.setVisibility(View.INVISIBLE);
                hideButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.user_information_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(UserPassword.this, HomePage.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(UserPassword.this, VideoPage.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(UserPassword.this, EventPage.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(UserPassword.this, SavedPage.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(UserPassword.this, ToolbarPage.class);
                    UserPassword.this.finish();
                    UserPassword.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}