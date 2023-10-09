package com.example.csit321.ui.login.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.csit321.EntityClass.Event;
import com.example.csit321.EntityClass.Token;
import com.example.csit321.R;
import com.example.csit321.SupportClass.DateConverter;
import com.example.csit321.SupportClass.FCMSend;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.NewPost;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewEvent extends AppCompatActivity {

    private static final String TAG = "NewEvent";

    private int sYear=0, sMonth=0, sDay=0, sHour=0, sMin=0;
    private int eYear=0, eMonth=0, eDay=0, eHour=0, eMin=0;

    private ImageView imageView;

    private Uri imageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference;

    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_FROM_DATE = "date_from";
    private static final String KEY_TO_DATE = "date_to";
    private static final String KEY_POSTED_DATE = "date_posted";
    private static final String KEY_STATUS = "status"; //1: preparing, 2: ongoing, 3: end
    private static final String KEY_VIEW = "total_views";
    private static final String KEY_SUB = "total_subscribes";
    private static final String KEY_IMAGE_ID = "image_id";
    private static final String KEY_ARRAY_OF_TITLE = "title_array";
    private static final String KEY_ARRAY_OF_USER_REGISTER = "user_register";

    private int SELECT_PICTURE = 100;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create new event (for admin only)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        bottomNavFunction(); //bottom navigation bar

        pickStartButton(); //pick start date button
        pickEndButton(); //pick end date button

        newEvent(); //confirm to create new event
        backButton(); //back button
        selectImage(); //select image button
    }

    private void sendNotification() {
        String title = "New Event";
        String message = "Please check out the new event";

        if (!title.equals("") && !message.equals("")) {
            pd.show();
            FirebaseFirestore.getInstance().collection("Tokens").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Token token = new Token();
                            token.setToken(document.getString("token"));
                            FCMSend.pushNotification(
                                    NewEvent.this,
                                    token.getToken(),
                                    title,
                                    message
                            );
                        }
                        startActivity(new Intent(NewEvent.this, EventPage.class));
                        pd.dismiss();
                    } else {
                        Log.d(TAG, "Error: " + task.getException().toString());
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    pd.dismiss();
                }
            });
        }
    }

    private void newEvent()
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        TextInputEditText titleInput = (TextInputEditText) findViewById(R.id.new_event_edittext);
        TextInputEditText descriptionInput = (TextInputEditText) findViewById(R.id.new_event_edittext1);
        TextInputEditText locationInput = (TextInputEditText) findViewById(R.id.new_event_edittext2);

        imageView = (ImageView) findViewById(R.id.new_event_logo3);

        Button createBut = (Button) findViewById(R.id.new_event_button4);
        createBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                Event event = new Event();
                event.setName(titleInput.getText().toString() != null ? titleInput.getText().toString() : null);
                event.setDescription(descriptionInput.getText().toString() != null ? descriptionInput.getText().toString() : null);
                event.setLocation(locationInput.getText().toString() != null ? locationInput.getText().toString() : null);
                event.setStatus(0);
                event.setViewCount(0);
                event.setSubCount(0);
                event.setStartDate(DateConverter.stringToDate(setDateToStringFormat(sYear,sMonth,sDay,sHour,sMin)));
                event.setEndDate(DateConverter.stringToDate(setDateToStringFormat(eYear,eMonth,eDay,eHour,eMin)));

//                event.setStartDate(DateConverter.stringToDate("2022-01-01 00:00:00"));
//                event.setEndDate(DateConverter.stringToDate("2022-01-01 00:00:00"));

                if (event.getName().isEmpty() || event.getLocation().isEmpty())
                {
                    Toast.makeText(NewEvent.this, "Missing fields. Please try again.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else if (!checkTimeStatus())
                {
                    Toast.makeText(NewEvent.this, "Please picks time!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else if (compareDates(event.getStartDate(), event.getEndDate()) >0)
                {
                    Toast.makeText(NewEvent.this, "Start date cannot occur after End date!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else {
                    String imageId= null;
                    String eventId = db.collection("Events").document().getId();
                    if (imageUri != null) {
                        imageId = db.collection("Images").document().getId();
                    }
                    Map<String, Object> newEvent = new HashMap<>();
                    newEvent.put(KEY_NAME,event.getName());
                    newEvent.put(KEY_DESCRIPTION,event.getDescription());
                    newEvent.put(KEY_LOCATION,event.getLocation());
                    newEvent.put(KEY_FROM_DATE,event.getStartDate());
                    newEvent.put(KEY_TO_DATE,event.getEndDate());
                    newEvent.put(KEY_POSTED_DATE, new Date());
                    newEvent.put(KEY_STATUS,event.getStatus());
                    newEvent.put(KEY_VIEW,event.getViewCount());
                    newEvent.put(KEY_SUB,event.getSubCount());
                    newEvent.put(KEY_IMAGE_ID,imageId);
                    newEvent.put(KEY_ARRAY_OF_TITLE, Arrays.asList(event.getName().trim().toLowerCase(Locale.ROOT).split(" ")));
                    newEvent.put(KEY_ARRAY_OF_USER_REGISTER, new ArrayList<String>());

                    db.collection("Events").document(eventId).set(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                if (imageUri != null)
                                {
                                    uploadImage(eventId, newEvent.get(KEY_IMAGE_ID).toString());
                                }
                                else
                                {
                                    Toast.makeText(NewEvent.this, "New event added", Toast.LENGTH_SHORT).show();
                                    sendNotification();
                                    pd.dismiss();
                                }
                            }
                            else
                            {
                                Toast.makeText(NewEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewEvent.this, e.getMessage().toLowerCase(Locale.ROOT), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    private int compareDates (Date date1, Date date2)
    {
        return date1.compareTo(date2);
    }

    private void uploadImage(String eventId, String imageId)
    {
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        collectionReference = FirebaseFirestore.getInstance().collection("Images");


        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "."
                + getFileExtension(imageUri));

        UploadTask uploadTask = fileRef.putFile(imageUri);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                {
                    throw  task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {

                        String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        Map <String, Object> uploadMap = new HashMap<>();
                        uploadMap.put("event_id", eventId);
                        uploadMap.put("url",photoStringLink);
                        uploadMap.put("type",2); //1 is post's image and 2 is event's image
                        collectionReference.document(imageId).set(uploadMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    sendNotification();
                                    Toast.makeText(NewEvent.this, "New event and image added", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(NewEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewEvent.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(NewEvent.this, "Cannot find event.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(NewEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewEvent.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void selectImage()
    {
        imageView = findViewById(R.id.new_event_logo3);
        Button addImageBut = findViewById(R.id.new_event_button3);
        addImageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagesFromGallery();
            }
        });
    }

    private String getFileExtension (Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void backButton()
    {
        Button button = (Button) findViewById(R.id.new_event_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(NewEvent.this, EventPage.class);
                NewEvent.this.finish();
                NewEvent.this.startActivity(myIntent);
            }
        });
    }

    private String setDateToStringFormat (int year, int month, int day, int hour, int min)
    {
        return year+"-"+month+"-"+day+" "+hour +":"+min+":"+"00";
    }

    private void loadImagesFromGallery(){
        Intent myIntent = new Intent();
        myIntent.setType("image/*");
        myIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(myIntent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (imageUri != null) {
                Picasso.with(this).load(imageUri).into(imageView);
                imageView.setImageURI(imageUri);
            }
        }
    }

    private boolean checkTimeStatus()
    {
        if (sYear == 0 || sMonth == 0 || sDay ==0
        || eYear == 0 || eMonth == 0 || eDay ==0)
            return false;
        return true;
    }

    private void pickEndButton() {
        Button pickEndButton = (Button) findViewById(R.id.new_event_button2);
        pickEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                eYear = c.get(Calendar.YEAR);
                eMonth = c.get(Calendar.MONTH);
                eDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener click = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        eYear = year;
                        eMonth = month+1;
                        eDay = day;
                        timePicker(1);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewEvent.this, click, eYear, eMonth, eDay);
                datePickerDialog.show();
            }
        });
    }

    private void pickStartButton() {
        Button pickStartBut = (Button) findViewById(R.id.new_event_button1);
        pickStartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                sYear = c.get(Calendar.YEAR);
                sMonth = c.get(Calendar.MONTH);
                sDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener click = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        sYear = year;
                        sMonth = month+1;
                        sDay = day;
                        timePicker(0);
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewEvent.this, click, sYear, sMonth, sDay);
                datePickerDialog.show();
            }
        });
    }

    private void timePicker(int status)
    {
        if (status == 0)
        {
            final Calendar c = Calendar.getInstance();
            sHour = c.get(Calendar.HOUR_OF_DAY);
            sMin = c.get(Calendar.MINUTE);
            int style = AlertDialog.THEME_HOLO_LIGHT;

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            sHour = hourOfDay;
                            sMin = minute;
                            TextView pickDateEndEText = (TextView) findViewById(R.id.new_event_textview1);
                            pickDateEndEText.setText(sHour+":"+sMin+" "+sDay+"-"+sMonth+"-"+sYear);
                        }
                    }, sHour, sMin, false);
            timePickerDialog.show();
        }
        else{
            final Calendar c = Calendar.getInstance();
            eHour = c.get(Calendar.HOUR_OF_DAY);
            eMin = c.get(Calendar.MINUTE);
            int style = AlertDialog.THEME_HOLO_LIGHT;

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            eHour = hourOfDay;
                            eMin = minute;
                            TextView pickDateEndEText = (TextView) findViewById(R.id.new_event_textview2);
                            pickDateEndEText.setText(eHour+":"+eMin+" "+eDay+"-"+eMonth+"-"+eYear);
                        }
                    }, eHour, eMin, false);
            timePickerDialog.show();
        }
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.new_event_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(NewEvent.this, HomePage.class);
                    NewEvent.this.finish();
                    NewEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(NewEvent.this, VideoPage.class);
                    NewEvent.this.finish();
                    NewEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(NewEvent.this, EventPage.class);
                    NewEvent.this.finish();
                    NewEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(NewEvent.this, SavedPage.class);
                    NewEvent.this.finish();
                    NewEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(NewEvent.this, ToolbarPage.class);
                    NewEvent.this.finish();
                    NewEvent.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}