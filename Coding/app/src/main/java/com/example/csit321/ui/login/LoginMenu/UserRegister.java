package com.example.csit321.ui.login.LoginMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Toolbar.ToolbarFragment1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserRegister extends AppCompatActivity {

    private static final String MyPreferences = "MyPrefs";
    private int userTypeByInt = -1;

    private static final String TAG = "UserRegister";

    private static final String KEY_NAME = "full_name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PURPOSE = "purpose";
    private static final String KEY_ACCOUNT_TYPE = "type";
    private static final String KEY_LIKED_POSTS = "posts_liked";
    private static final String KEY_SAVED_EVENTS = "events_saved";
    private static final String KEY_NAME_ARRAY = "array_name";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        passwordButton(); //show/hide password button
        addUserType(); // choosing the user type
        backButton(); // back button
        finishButton(); //confirm create new account
//        createAuthWithEmail("admin@gmail.com","admin123", "Admin Manager", "Admin", "admin" );
    }

    private void backButton()
    {
        Button button = (Button) findViewById(R.id.user_register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( UserRegister.this, MainLogin.class);
                UserRegister.this.finish();
                UserRegister.this.startActivity(intent);
            }
        });
    }

    private void finishButton()
    {
        Button button = (Button) findViewById(R.id.user_register_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void sharePrefs (String userId, String userType)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId",userId);
        editor.putString("accountType", userType);
        //User
//        editor.putString("userId","Yi4jGdP4A3rIWWvBBVnX");
//        editor.putString("accountType", "user");

        //Admin
//        editor.putString("userId","XdPlEFotCA23tFIJjIIM");
//        editor.putString("accountType", "admin");
        editor.commit();
        Log.d(TAG, "share prefs: "+userId+" "+userType);
    }

    private void registerUser()
    {

        EditText fullNameEt = (EditText) findViewById(R.id.user_register_textinput);
        EditText usernameEt = (EditText) findViewById(R.id.user_register_textinput1);
        EditText passwordEt = (EditText) findViewById(R.id.user_register_textinput2);

        String fullName = fullNameEt.getText().toString();
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();


        if(TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
        {
            Toast.makeText(UserRegister.this, "Empty credentials", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6)
        {
            Toast.makeText(UserRegister.this, "Password is too short", Toast.LENGTH_SHORT).show();
        }
        else if (userTypeByInt == -1)
        {
            Toast.makeText(UserRegister.this, "Please select purpose for your account", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String purpose = userTypeByString();
            pd = new ProgressDialog(this);

            pd.setMessage("Please Wait!");
            pd.show();
//            createAuthWithPhone(username, password, fullName, userType);
            if (username.matches("\\d{10}"))
            {
                createAuthWithPhone(username, password, fullName, purpose, "user");
            }
            else
            {
                createAuthWithEmail(username, password, fullName, purpose, "user");
            }

        }

    }

    private void createAuthWithEmail(String username, String password, String fullName, String purpose, String accountType)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                List<String> likedPosts = new ArrayList<>();
                List<String> savedEvents = new ArrayList<>();

                Map <String, Object> users = new HashMap<>();
                users.put(KEY_NAME, fullName);
                users.put(KEY_USERNAME, username);
                users.put(KEY_PASSWORD, password);
                users.put(KEY_PURPOSE, purpose);
                users.put(KEY_ACCOUNT_TYPE, accountType);
                users.put(KEY_LIKED_POSTS, likedPosts);
                users.put(KEY_SAVED_EVENTS, savedEvents);
                users.put(KEY_NAME_ARRAY, Arrays.asList(fullName.trim().toLowerCase(Locale.ROOT).split(" ")));

                String id = db.collection("Users").document().getId();
                db.collection("Users").document(id).set(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sharePrefs(id, accountType);
//                                addTokenForUser(id);
                                Toast.makeText(UserRegister.this, "User saved", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserRegister.this, HomePage.class);
                                UserRegister.this.finish();
                                UserRegister.this.startActivity(intent);
                                pd.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserRegister.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                                Intent intent = new Intent(UserRegister.this, UserRegister.class);
                                UserRegister.this.finish();
                                UserRegister.this.startActivity(intent);
                                pd.dismiss();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(UserRegister.this, "Email or Phone number is not valid.", Toast.LENGTH_SHORT).show();
                Toast.makeText(UserRegister.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String username, String password, String fullName, String userType) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(UserRegister.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> users = new HashMap<>();
                            users.put(KEY_NAME, fullName);
                            users.put(KEY_USERNAME, username);
                            users.put(KEY_PASSWORD, password);
                            users.put(KEY_PURPOSE, userType);
                            users.put(KEY_ACCOUNT_TYPE, "user");

                            String id = db.collection("Users").document().getId();
                            db.collection("Users").document(id).set(users)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(UserRegister.this, "User saved", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserRegister.this, HomePage.class);
                                            UserRegister.this.finish();
                                            UserRegister.this.startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserRegister.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserRegister.this, UserRegister.class);
                                            UserRegister.this.finish();
                                            UserRegister.this.startActivity(intent);
                                        }
                                    });
                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
//
                            Toast.makeText(UserRegister.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void createAuthWithPhone(String username, String password, String fullName, String purpose, String accountType)
    {
        Intent intent = new Intent( UserRegister.this, VerifyPhone.class);
        intent.putExtra(KEY_NAME, fullName);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_PASSWORD, password);
        intent.putExtra(KEY_PURPOSE, purpose);
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType);
        UserRegister.this.finish();
        UserRegister.this.startActivity(intent);
    }

    private void passwordButton()
    {
        EditText password = (EditText) findViewById(R.id.user_register_textinput2);
        Button hideButton = (Button) findViewById(R.id.user_register_button1);
        Button showButton = (Button) findViewById(R.id.user_register_button3);

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
        Spinner spinner = (Spinner) findViewById(R.id.user_register_spinner);
//        TextView textView = (TextView) findViewById(R.id.user_register_logo5);

        ArrayAdapter <String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, userTypes);
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
}