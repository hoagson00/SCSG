package com.example.csit321.ui.login.Toolbar;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csit321.EntityClass.User;
import com.example.csit321.R;
import com.example.csit321.ui.login.LoginMenu.UserPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ToolbarFragment1 extends Fragment {

    private static final String MyPreferences = "MyPrefs";

    private String userId, accountType;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //This fragment shows the user's details when user have already logged in
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_toolbar_account1, container, false);

        readSharedPref(); //get the user's information
        showDialog̣̣(view); //show a wish to users
        logoutButton(view); //log out button
        changePasswordButton(view); //Button to account detail UI
        return view;
    }

    private void changePasswordButton(View view)
    {
        Button button = (Button) view.findViewById(R.id.fragment_toolbar_account_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), AccountDetail.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }

    private void logoutButton(View view)
    {
        Button button = (Button) view.findViewById(R.id.fragment_toolbar_account_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences(MyPreferences, MODE_PRIVATE);
                preferences.edit().clear().commit();
                Intent intent = new Intent(getActivity(), ToolbarPage.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }

    private void showDialog̣̣(View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.fragment_toolbar_account_text);
        TextView textView1 = (TextView) view.findViewById(R.id.fragment_toolbar_account_text2);
        if (userId != null || !userId.equals(""))
        {
            FirebaseFirestore.getInstance().collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            List<String> title = new ArrayList<>();
                            title.add("Wishing you a day filled with good health and happiness!");
                            title.add("May you feel energized and motivated to tackle whatever the day brings.");
                            title.add("Here's to a day full of joy, laughter, and positivity.");
                            title.add("May your body be strong and your mind be clear.");
                            title.add("Wishing you a day filled with love, kindness, and gratitude.");
                            title.add("May your day be productive and fulfilling, and leave you feeling accomplished.");
                            title.add("Wishing you moments of peace and relaxation throughout your day.");
                            title.add("May you be surrounded by supportive and caring people.");
                            title.add("Here's to a day filled with healthy choices and self-care.");
                            Collections.shuffle(title);
                            Collections.shuffle(title);
                            textView.setText("Hi, "+document.getString("full_name"));
                            textView1.setText(title.get(0));
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Cannot find User.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void readSharedPref()
    {
        SharedPreferences sh = getContext().getSharedPreferences(MyPreferences, MODE_PRIVATE);
        userId = sh.getString("userId","");
        accountType = sh.getString("accountType", "");
    }
}
