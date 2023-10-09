package com.example.csit321.ui.login.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csit321.R;
import com.example.csit321.ui.login.LoginMenu.MainLogin;
import com.example.csit321.ui.login.LoginMenu.UserRegister;

public class ToolbarFragment extends Fragment{

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //This fragment is showed when the user doesn't login
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_toolbar_account, container, false);
        loginButton(view); //login button
        registerButton(view); //create new account button
        return view;
    }

    public void loginButton(View view)
    {
        Button button = (Button) view.findViewById(R.id.fragment_toolbar_account_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainLogin.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }

    public void registerButton (View view)
    {
        Button button = (Button) view.findViewById(R.id.fragment_toolbar_account_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserRegister.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }
}
