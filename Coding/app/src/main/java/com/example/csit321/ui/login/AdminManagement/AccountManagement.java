package com.example.csit321.ui.login.AdminManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.csit321.Adapter.PostAdapter;
import com.example.csit321.EntityClass.Post;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.EntityClass.User;
import com.example.csit321.Adapter.UserAdapter;
import com.example.csit321.SupportClass.SpacingItemDecorator;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.PostDetail;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.AccountDetail;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class AccountManagement extends AppCompatActivity {

    private static final String TAG = "AccountManagement";
    private static final String MyPreferences = "MyPrefs";

    private FirebaseFirestore db;
    private DateFormat dateFormat;
    ProgressDialog pd;

    private String accountType;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Show the details of all users in the system
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        loadAdapter(); //setup a view of list
        loadUser(); //load a list of all users' details
        backButton(); //back button
        searchUserByTitle(); //search user function
        bottomNavFunction(); //bottom navigation bar
    }

    public void searchUserByTitle ()
    {
        pd.show();
        TextInputEditText inputEditText = (TextInputEditText) findViewById(R.id.account_management_edittext);
        Button searchBut = (Button) findViewById(R.id.account_management_button1);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
                if (title.isEmpty()) {
                    loadUser();
                    pd.dismiss();
                }
                else
                {
                    inputEditText.setText("");
                    CollectionReference ref = db.collection("Users");
                    ref.whereArrayContainsAny("array_name", Arrays.asList(title.split(" ")))
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    users = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        User user = new User();
                                        user.setUserId(document.getId());
                                        user.setFullName(document.getString("full_name"));
                                        user.setUsername(document.getString("username"));
                                        user.setAccountType(document.getString("purpose"));
                                        users.add(user);
                                    }
                                    userAdapter.setUserList(users);
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText((AccountManagement.this), e.getMessage().toLowerCase(Locale.ROOT), Toast.LENGTH_SHORT);
                                    pd.dismiss();
                                }
                            });
                }
            }
        });
    }

    private void loadAdapter()
    {
        recyclerView = findViewById(R.id.account_management_view);
        db = FirebaseFirestore.getInstance();
        users = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        userAdapter = new UserAdapter(users, this, new UserAdapter.IClickItemUser() {
            @Override
            public void showDetail(String userId, String fullName) {
                showPostDetail(userId, fullName);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showPostDetail (final String userId, final String fullName)
    {
        Intent myIntent = new Intent(AccountManagement.this, UserSaved.class);
        myIntent.putExtra("userId", userId);
        myIntent.putExtra("fullName", fullName);
        AccountManagement.this.finish();
        AccountManagement.this.startActivity(myIntent);
    }


    private void loadUser()
    {
        users = new ArrayList<>();
        pd.show();

        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document: task.getResult())
                    {
                        User user = new User();
                        user.setUserId(document.getId());
                        user.setFullName(document.getString("full_name"));
                        user.setUsername(document.getString("username"));
                        user.setAccountType(document.getString("purpose"));
                        users.add(user);
                    }
                    userAdapter.setUserList(users);
                    pd.dismiss();
                }
                else
                {
                    Toast.makeText(AccountManagement.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountManagement.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void backButton()
    {
        Button button = findViewById(R.id.account_management_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountManagement.this, AccountDetail.class));
                finish();
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.account_management_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(AccountManagement.this, HomePage.class);
                    AccountManagement.this.finish();
                    AccountManagement.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(AccountManagement.this, VideoPage.class);
                    AccountManagement.this.finish();
                    AccountManagement.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(AccountManagement.this, EventPage.class);
                    AccountManagement.this.finish();
                    AccountManagement.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(AccountManagement.this, SavedPage.class);
                    AccountManagement.this.finish();
                    AccountManagement.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(AccountManagement.this, ToolbarPage.class);
                    AccountManagement.this.finish();
                    AccountManagement.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }

}