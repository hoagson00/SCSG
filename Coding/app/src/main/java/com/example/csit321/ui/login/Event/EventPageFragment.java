package com.example.csit321.ui.login.Event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csit321.R;

import java.util.Calendar;

public class EventPageFragment extends Fragment {

    private static final String TAG = "EventPageFragment";

    private int sYear=0, sMonth=0, sDay=0;
    private int eYear=0, eMonth=0, eDay=0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Sort event by inputted date
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_event_page, container, false);
        pickStartTimeButton(view); //pick start date
        pickEndTimeButton(view); //pick end date
        cancelButton(view); //cancel button
        sortByDateType(view); //sort date by inputted date
        okButton(view,0); //confirm button
        return view;
    }


    private void sortByDateType (View v)
    {
        RadioGroup radioGroup = v.findViewById(R.id.event_page_frag_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton.getText().equals("Start Date"))
                {
                    okButton(v,1);
                }
                else if (radioButton.getText().equals("Expiry Date"))
                {
                    okButton(v,2);
                }
                else
                {
                    okButton(v,0);
                }
            }
        });


    }

    private void okButton(View v, int type)
    {
        Button button = (Button) v.findViewById(R.id.event_page_frag_button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDateEx = setDateToStringFormat(sYear, sMonth, sDay);
                String endDateEx = setDateToStringFormat(eYear, eMonth, eDay);
                if (type == 0)
                {
                    Toast.makeText(getActivity(), "Pick start date or expiry date.", Toast.LENGTH_SHORT).show();
                }
                else if (startDateEx.equals("0-00-00 00:00:00") && endDateEx.equals("0-00-00 00:00:00"))
                {
                    Toast.makeText(getActivity(), "Missing fields. Please try again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent myIntent = new Intent(getActivity(), EventPageSortByDate.class);
                    myIntent.putExtra("fromDate", startDateEx);
                    myIntent.putExtra("toDate", endDateEx);
                    myIntent.putExtra("type",type);
                    getActivity().finish();
                    getActivity().startActivity(myIntent);
                }
            }
        });
    }

    public String setDateToStringFormat (int year, int month, int day)
    {
        if (month <10 && day < 10)
            return year+ "-0"+month+"-0"+day + " 00:00:00";
        else if (day<10)
            return year+ "-"+month+"-0"+day + " 00:00:00";
        else if (month <10)
            return year+ "-0"+month+"-"+day + " 00:00:00";
        else
            return year+ "-"+month+"-"+day + " 00:00:00";
    }

    private void cancelButton(View v)
    {
        Button button = v.findViewById(R.id.event_page_frag_button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventPage.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
    }

    private void pickStartTimeButton (View v)
    {
        TextView textView = (TextView) v.findViewById(R.id.event_page_frag_textview1) ;
        Button button = (Button) v.findViewById(R.id.event_page_frag_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener fragment = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "-"+ (month+1)+ "-"+year;
                        textView.setText(date);
                        sYear = year;
                        sMonth = (month+1);
                        sDay = dayOfMonth;
                    }
                };
                Calendar cal = Calendar.getInstance();
                sYear = cal.get(Calendar.YEAR);
                sMonth = cal.get(Calendar.MONTH);
                sDay = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventPageFragment.this.getContext(), fragment, sYear, sMonth, sDay);
                datePickerDialog.show();
            }
        });
    }

    private void pickEndTimeButton (View v)
    {
        TextView textView = (TextView) v.findViewById(R.id.event_page_frag_textview2) ;
        Button button = (Button) v.findViewById(R.id.event_page_frag_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener fragment = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "-"+ (month+1)+ "-"+year;
                        textView.setText(date);
                        eYear = year;
                        eMonth = (month+1);
                        eDay = dayOfMonth;
                    }
                };
                Calendar cal = Calendar.getInstance();
                eYear = cal.get(Calendar.YEAR);
                eMonth = cal.get(Calendar.MONTH);
                eDay = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventPageFragment.this.getContext(), fragment, eYear, eMonth, eDay);
                datePickerDialog.show();
            }
        });
    }
}