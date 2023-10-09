package com.example.csit321.ui.login.LoginMenu;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class UserLoginFragment extends Fragment {

    private static final String MyPreferences = "MyPrefs";

    private static final String TAG = "UserLoginFragment";

    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_login_fragment, container, false);

        loginButton(view); //login button
        newAccount(view); //create new account button
        passwordButton(view); //show/ hide password button
        forgotPassBut(view);// new password if the user forgot it
        registerAccBut(view);// create new account button
        backHomeBut(view); // back button

        return view;
    }

    private void registerAccBut(View view)
    {
        Button registerAccBut = (Button) view.findViewById(R.id.user_login_fragement_button2);
        registerAccBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refresh = new Intent (getActivity(), UserRegister.class);
                getActivity().finish();
                getActivity().startActivity(refresh);
            }
        });
    }

    private void forgotPassBut(View view)
    {
        Button button = view.findViewById(R.id.user_login_fragement_button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), HomePage.class));
//                getActivity().finish();
            }
        });
    }

    private void backHomeBut(View view)
    {
        Button button = view.findViewById(R.id.user_login_fragement_button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomePage.class));
                getActivity().finish();
            }
        });
    }

    private void passwordButton(View view)
    {
        EditText password = (EditText) view.findViewById(R.id.user_login_fragment_inputtext1);
        Button hideButton = (Button) view.findViewById(R.id.user_login_fragment_button);
        Button showButton = (Button) view.findViewById(R.id.user_login_fragment_button4);

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

    private void loginButton(View view)
    {
        TextInputEditText inputUsername = view.findViewById(R.id.user_login_fragment_inputtext);
        TextInputEditText inputPassword = view.findViewById(R.id.user_login_fragment_inputtext1);

        Button signInBut = (Button) view.findViewById(R.id.user_login_fragement_button1);
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsernameTxt = inputUsername.getText().toString().trim();
                String inputPasswordTxt = inputPassword.getText().toString().trim();
//                sharePrefs(inputUsernameTxt);
//                Intent myIntent = new Intent (getActivity(), ToolbarPage.class);
//                getActivity().finish();
//                getActivity().startActivity(myIntent);
                if (inputUsernameTxt.isEmpty() || inputPasswordTxt.isEmpty())
                {
                    Toast.makeText(getActivity(), "Username or Password is incorrect. Please try again", Toast.LENGTH_SHORT).show();
                    Intent refresh = new Intent (getActivity(), MainLogin.class);
                    getActivity().finish();
                    getActivity().startActivity(refresh);
                }
                else if (inputPasswordTxt.length() < 6)
                {
                    Toast.makeText(getActivity(), "Password is too short", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    pd = new ProgressDialog(getContext());

                    pd.setMessage("Please Wait!");
                    pd.show();

                    mAuth.signInWithEmailAndPassword(inputUsernameTxt, inputPasswordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                getUserInformation(inputUsernameTxt);
                                Toast.makeText(getActivity(), "Login successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            else
                            {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(getContext(),MainLogin.class));
                                pd.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void newAccount (View view)
    {
        Button button = (Button) view.findViewById(R.id.user_login_fragement_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserRegister.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP  );
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }

    private void getUserInformation(String username)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("username", username).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                {
                    String userId = document.getId();
                    String type = document.getString("type");
                    sharePrefs(userId, type);
                    startActivity(new Intent(getContext(), HomePage.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText((getContext()), "No data found in Database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sharePrefs (String userId, String userType)
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyPreferences, MODE_PRIVATE);
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

}
