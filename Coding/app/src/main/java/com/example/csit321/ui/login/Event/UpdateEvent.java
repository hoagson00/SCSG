package com.example.csit321.ui.login.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.EntityClass.Post;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.SupportClass.DateConverter;
import com.example.csit321.ui.login.Post.HomePage;
import com.example.csit321.ui.login.Post.UpdatePost;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateEvent extends AppCompatActivity {

    private static final String TAG = "UpdateEvent";

    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_FROM_DATE = "date_from";
    private static final String KEY_TO_DATE = "date_to";
    private static final String KEY_ARRAY_OF_TITLE = "title_array";

    private FirebaseFirestore db;

    private Event event;

    private TextInputEditText title, description, location;
    private TextView timeStart, timeEnd;

    private DateFormat dateFormat;

    private int sYear=0, sMonth=0, sDay=0, sHour=0, sMin=0;
    private int eYear=0, eMonth=0, eDay=0, eHour=0, eMin=0;

    private ImageView imageView;

    private Uri imageUri;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Update the event's details
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        db = FirebaseFirestore.getInstance();
        dateFormat = new DateFormat();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        setEventData(getEventId()); //show the current event's details for admin
        pickStartButton(); //pick start date button
        pickEndButton(); //pick end date button
        bottomNavFunction(); //bottom navigator button
        updateEvent(getEventId()); //confirm to update the event
        backButton(getEventId()); //back button
        deleteButton(getEventId()); //delete the event
        selectImage(); //select image button
    }

    private void deleteRegisteredOfUsers(String eventId)
    {
        db.collection("Events").document(eventId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection("Users").whereArrayContains("events_saved", eventId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : querySnapshot)
                            {
                                db.collection("Users").document(document.getId()).update("events_saved", FieldValue.arrayRemove(eventId))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Delete document: "+document.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, e.getMessage().toString());
                                            }
                                        });
                            }
                            Toast.makeText(UpdateEvent.this, "Delete event successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UpdateEvent.this, EventPage.class));
                            finish();
                            pd.dismiss();
                        }
                        else
                        {
                            pd.dismiss();
                            Toast.makeText(UpdateEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateEvent.this, "Error"+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void deleteEvent(String eventId)
    {
        pd.show();
        DocumentReference docRef = db.collection("Events").document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Event event = new Event();
                        event.setImageId(document.getString("image_id"));
                        if (event.getImageId() != null)
                        {
                            deleteStorageFile(event.getImageId(), "delete");
                        }
                        else
                        {
                            deleteRegisteredOfUsers(eventId);
                        }
                    }
                    else
                    {
                        Toast.makeText(UpdateEvent.this, "Cannot find event.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
                else
                {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(UpdateEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateEvent.this, "Error"+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void deleteButton(String eventId)
    {
        Button button = findViewById(R.id.update_event_button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEvent.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Do you want to delete this event?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(eventId);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
    }

    private void backButton(String eventId)
    {
        Button button = findViewById(R.id.update_event_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateEvent.this, EventDetail.class);
                intent.putExtra("event_id", eventId);
                finish();
                startActivity(intent);
            }
        });
    }

    private String getFileExtension (Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage(String imageId)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images");
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Images");

        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "."
                + getFileExtension(imageUri));

        UploadTask uploadTask = fileRef.putFile(imageUri);

        Task <Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        Map<String, Object> uploadMap = new HashMap<>();
                        uploadMap.put("url", photoStringLink);
                        collectionReference.document(imageId).update(uploadMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                Toast.makeText(UpdateEvent.this, "Update successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateEvent.this, EventDetail.class);
                                intent.putExtra("event_id", event.getEventId());
                                finish();
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(UpdateEvent.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        pd.dismiss();
                        Toast.makeText(UpdateEvent.this, "Cannot find the url.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(UpdateEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteStorageFile(String imageId, String status)
    {

        db.collection("Images").document(imageId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists())
                    {
                        String eventId = documentSnapshot.getString("event_id");
                        String url = documentSnapshot.getString("url");
                        StorageReference fileStorage = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        fileStorage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (status.equals("delete"))
                                {
                                    db.collection("Images").document(imageId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                deleteRegisteredOfUsers(eventId);
                                            }
                                            else
                                            {
                                                Toast.makeText(UpdateEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });
                                }
                                else if (status.equals("update"))
                                {
                                    uploadImage(imageId);
                                }
                                else
                                {
                                    pd.dismiss();
                                    Toast.makeText(UpdateEvent.this, "Delete events successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UpdateEvent.this, EventPage.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(UpdateEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        pd.dismiss();
                        Toast.makeText(UpdateEvent.this, "Cannot find event.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(UpdateEvent.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UpdateEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent(String eventId)
    {
        Button updateBut = findViewById(R.id.update_event_button4);
        updateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                String newTitle = title.getText().toString();
                String newDescription = description.getText().toString();
                String newLocation = location.getText().toString();
                String newTitleArray = newTitle.trim().toLowerCase(Locale.ROOT);

                int checkDate = checkTimeStatus();
                Date startDate = DateConverter.stringToDate(setDateToStringFormat(sYear,sMonth,sDay,sHour,sMin));
                Date endDate = DateConverter.stringToDate(setDateToStringFormat(eYear,eMonth,eDay,eHour,eMin));

                if (newTitle.isEmpty() || newDescription.isEmpty() || newLocation.isEmpty()) {
                    pd.dismiss();
                    Toast.makeText(UpdateEvent.this, "Missing fields. Please try again", Toast.LENGTH_SHORT).show();
                }
                else if ((checkDate == 1 && (compareDates(startDate, endDate) >0)) ||
                        (checkDate == 2 && compareDates(event.getStartDate(), endDate) >0) ||
                        (checkDate == 3 && compareDates(startDate, event.getEndDate()) >0))
                {
                    pd.dismiss();
                    Toast.makeText(UpdateEvent.this, "Start date cannot occur after End date!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Map<String, Object> updateEvent = new HashMap<>();
                    updateEvent.put(KEY_NAME,event.getName());
                    updateEvent.put(KEY_DESCRIPTION,event.getDescription());
                    updateEvent.put(KEY_LOCATION,event.getLocation());
                    updateEvent.put(KEY_ARRAY_OF_TITLE, Arrays.asList(newTitleArray.split(" ")));

                    switch (checkDate){
                        case 1:
                            updateEvent.put(KEY_FROM_DATE,startDate);
                            updateEvent.put(KEY_TO_DATE,endDate);
                            break;
                        case 2:
                            updateEvent.put(KEY_TO_DATE,endDate);
                            break;
                        case 3:
                            updateEvent.put(KEY_FROM_DATE,startDate);
                            break;
                        default:
                            break;
                    }

                    db.collection("Events").document(eventId).update(updateEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (imageUri != null)
                            {
                                deleteStorageFile(event.getImageId(),"update");
                            }
                            else
                            {
                                pd.dismiss();
                                Toast.makeText(UpdateEvent.this, "Update successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateEvent.this, EventDetail.class);
                                intent.putExtra("event_id", eventId);
                                finish();
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(UpdateEvent.this, "Update failed", Toast.LENGTH_SHORT).show();
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

    private void setEventData(String eventId)
    {
        DocumentReference docRef = db.collection("Events").document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        event = new Event();
                        event.setEventId(document.getId());
                        event.setName(document.getString("name"));
                        event.setDescription(document.getString("description"));
                        event.setLocation(document.getString("location"));
                        event.setStartDate(document.getDate("date_from"));
                        event.setEndDate(document.getDate("date_to"));
                        event.setStatus(Integer.parseInt(document.get("status").toString()));
                        event.setViewCount(Integer.parseInt(document.get("total_views").toString()));
                        event.setSubCount(Integer.parseInt(document.get("total_subscribes").toString()));
                        event.setImageId(document.getString("image_id"));

                        title = findViewById(R.id.update_event_edittext);
                        title.setText(event.getName());

                        description = findViewById(R.id.update_event_edittext1);
                        description.setText(event.getDescription());

                        location = findViewById(R.id.update_event_edittext2);
                        location.setText(event.getLocation());

                        timeStart = findViewById(R.id.update_event_textview1);
                        timeStart.setText(DateConverter.dateToString(event.getStartDate()));

                        timeEnd = findViewById(R.id.update_event_textview2);
                        timeEnd.setText(DateConverter.dateToString(event.getEndDate()));

                        loadImage(event.getImageId());
                    }
                    else
                    {
                        Log.d(TAG, "Cannot find event");
                    }
                }
                else
                {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(UpdateEvent.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage()
    {
        ImageView imageView = findViewById(R.id.update_event_logo3);
        Button addImageBut = findViewById(R.id.update_event_button3);
        addImageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagesFromGallery();
            }
        });
    }

    public void loadImagesFromGallery(){
        Intent myIntent = new Intent();
        myIntent.setType("image/*");
        myIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(myIntent, "Select Picture"), 100);
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.update_event_logo3);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (imageUri != null) {
                Picasso.with(this).load(imageUri).into(imageView);
                imageView.setImageURI(imageUri);
            }
        }
    }

    private int checkTimeStatus()
    {
        int status = 0;
        if ((sYear != 0 && sMonth != 0 && sDay != 0) && (eYear != 0 && eMonth != 0 && eDay != 0))
            status = 1;
        else if ((sYear == 0 && sMonth == 0 && sDay == 0) && (eYear != 0 && eMonth != 0 && eDay != 0))
            status = 2;
        else if ((sYear != 0 && sMonth != 0 && sDay != 0) && (eYear == 0 && eMonth == 0 && eDay == 0))
            status = 3;
        return status;
    }

    private void loadImage(String imageId)
    {
        imageView = (ImageView) findViewById(R.id.update_event_logo3);
        if (imageId == null)
        {
            imageView.setVisibility(View.GONE);
        }
        else
        {
            DocumentReference docRef = db.collection("Images").document(imageId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            String url =document.getString("url");
//                            Picasso.with(getApplicationContext()).load(path).into(imageView);
                            Glide.with(getApplicationContext()).load(url).into(imageView);
                        }
                    }
                    else
                    {
                        Log.d(TAG, "Get failed with"+ task.getException());
                        Toast.makeText(UpdateEvent.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void pickEndButton() {
        Button pickEndButton = (Button) findViewById(R.id.update_event_button2);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEvent.this, click, eYear, eMonth, eDay);
                datePickerDialog.show();
            }
        });
    }

    private void pickStartButton() {
        Button pickStartBut = (Button) findViewById(R.id.update_event_button1);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEvent.this, click, sYear, sMonth, sDay);
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
                            TextView pickDateEndEText = (TextView) findViewById(R.id.update_event_textview1);
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
                            TextView pickDateEndEText = (TextView) findViewById(R.id.update_event_textview2);
                            pickDateEndEText.setText(eHour+":"+eMin+" "+eDay+"-"+eMonth+"-"+eYear);
                        }
                    }, eHour, eMin, false);
            timePickerDialog.show();
        }
    }

    private String getEventId()
    {
        String eventId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            eventId = extras.getString("event_id");
        }
        return eventId;
    }

    private String setDateToStringFormat (int year, int month, int day, int hour, int min)
    {
        return year+"-"+month+"-"+day+" "+hour +":"+min+":"+"00";
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.update_event_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(UpdateEvent.this, HomePage.class);
                    UpdateEvent.this.finish();
                    UpdateEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(UpdateEvent.this, VideoPage.class);
                    UpdateEvent.this.finish();
                    UpdateEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(UpdateEvent.this, EventPage.class);
                    UpdateEvent.this.finish();
                    UpdateEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(UpdateEvent.this, SavedPage.class);
                    UpdateEvent.this.finish();
                    UpdateEvent.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(UpdateEvent.this, ToolbarPage.class);
                    UpdateEvent.this.finish();
                    UpdateEvent.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}