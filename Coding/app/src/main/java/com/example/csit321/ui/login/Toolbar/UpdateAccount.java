package com.example.csit321.ui.login.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.UpdatePost;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateAccount extends AppCompatActivity {

    private static final String MyPreferences = "MyPrefs";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PURPOSE = "purpose";

    private FirebaseFirestore db;
    ProgressDialog pd;
    private String userId, accountType;
    private int userTypeByInt = -1;

    private static final String TAG = "UpdateAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Update user's details
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        getSharePref(); //get the user's details

        setData(); //show the user's details
        addUserType(); //change user type
        passwordButton(); //show/hide password button
        backButton(); //back button
        updateButton(); //confirm to update details
        deleteButton(); //delete user
        bottomNavFunction(); //bottom navigation bar
    }

    private void deleteButton()
    {
        Button button = findViewById(R.id.update_detail_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAccount.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Do you want to delete this user?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
    }

    private void deleteUser()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    db.collection("Users").document(userId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SharedPreferences preferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
                                preferences.edit().clear().commit();
                                Intent intent = new Intent(UpdateAccount.this, ToolbarPage.class);
                                finish();
                                startActivity(intent);
                                Toast.makeText(UpdateAccount.this, "Delete account successfully!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(UpdateAccount.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateAccount.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateAccount.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(UpdateAccount.this, "No user found!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteToken()
    {

    }

    private void updateButton()
    {
        Button button = findViewById(R.id.update_detail_button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Please Wait!");
                pd.show();

                EditText fullName = (EditText) findViewById(R.id.update_detail_textView);
                EditText password = (EditText) findViewById(R.id.update_detail_textView2);

                String newFullName = fullName.getText().toString();
                String newPass = password.getText().toString();
                String purpose = userTypeByString();
                if (newFullName.isEmpty() || newPass.isEmpty())
                {
                    Toast.makeText(UpdateAccount.this, "Full name or password cannot be empty.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Map<String, Object> updateUser = new HashMap<>();
                                updateUser.put(KEY_NAME, newFullName);
                                updateUser.put(KEY_PASSWORD, newPass);
                                updateUser.put(KEY_PURPOSE, purpose);

                                if (userId != null || userId.equals(""))
                                {
                                    db.collection("Users").document(userId).update(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                startActivity(new Intent(UpdateAccount.this, AccountDetail.class));
                                                finish();
                                                Toast.makeText(UpdateAccount.this, "Update user successfully", Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                            else
                                            {
                                                Toast.makeText(UpdateAccount.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateAccount.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(UpdateAccount.this, "Cannot find User", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }
                            else
                            {
                                Toast.makeText(UpdateAccount.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateAccount.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void backButton()
    {
        Button button = findViewById(R.id.update_detail_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateAccount.this, AccountDetail.class));
                finish();
            }
        });
    }

    private void setData() {

        if (userId != null || userId.equals("")) {
            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = new User();
                            user.setUsername(document.getString("username"));
                            user.setPassword(document.getString("password"));
                            user.setFullName(document.getString("full_name"));
                            user.setAccountType(document.getString("purpose"));

                            EditText fullName = findViewById(R.id.update_detail_textView);
                            TextView username = findViewById(R.id.update_detail_textView1);
                            EditText password = findViewById(R.id.update_detail_textView2);

                            fullName.setText(user.getFullName());
                            username.setText(user.getUsername());
                            password.setText(user.getPassword());
                        } else
                            Toast.makeText(UpdateAccount.this, "Cannot find User", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateAccount.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateAccount.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    private void passwordButton()
    {
        EditText password = (EditText) findViewById(R.id.update_detail_textView2);
        Button hideButton = (Button) findViewById(R.id.update_detail_button3);
        Button showButton = (Button) findViewById(R.id.update_detail_button2);

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

    private void addUserType()
    {
        String[] userTypes = {"Care Taker","Cancer Survivor", "Other"};
        Spinner spinner = (Spinner) findViewById(R.id.update_detail_spinner);
//        TextView textView = (TextView) findViewById(R.id.user_register_logo5);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userTypeByInt = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userTypeByInt = spinner.getSelectedItemPosition();
            }
        });
//        Log.d("userTypeByInt", String.valueOf(userTypeByInt));
    }

    private String userTypeByString()
    {
        String type = "";
        if (userTypeByInt == 0)
        {
            type = "Care Taker";
        }
        else if (userTypeByInt == 1)
        {
            type = "Cancer Survivor";
        }
        else if (userTypeByInt == 2)
        {
            type = "Other";
        }
        else
        {
            return null;
        }
        return type;
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.update_detail_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(UpdateAccount.this, HomePage.class);
                    UpdateAccount.this.finish();
                    UpdateAccount.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(UpdateAccount.this, VideoPage.class);
                    UpdateAccount.this.finish();
                    UpdateAccount.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(UpdateAccount.this, EventPage.class);
                    UpdateAccount.this.finish();
                    UpdateAccount.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(UpdateAccount.this, SavedPage.class);
                    UpdateAccount.this.finish();
                    UpdateAccount.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(UpdateAccount.this, ToolbarPage.class);
                    UpdateAccount.this.finish();
                    UpdateAccount.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}