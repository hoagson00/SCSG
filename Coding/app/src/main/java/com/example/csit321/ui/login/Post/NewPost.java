package com.example.csit321.ui.login.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.csit321.EntityClass.Token;
import com.example.csit321.R;
import com.example.csit321.SupportClass.FCMSend;
import com.example.csit321.ui.login.AdminManagement.NewNotification;
import com.example.csit321.ui.login.Event.EventPage;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewPost extends AppCompatActivity {

    private Button addImageBut, postBut;
    private TextInputEditText title, description, hashtag;
    private ImageView imageView;

    private Uri imageUri;

    private int SELECT_PICTURE = 100;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressDialog pd;

    private static final String TAG = "NewPost";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_HASHTAG = "hashtag";
    private static final String KEY_IMAGE_ID = "image_id";
    private static final String KEY_DATE = "date_posted";
    private static final String KEY_LIKE = "total_likes";
    private static final String KEY_VIEW = "total_views";
    private static final String KEY_ARRAY_OF_TITLE = "title_array";

    private StorageReference storageReference;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        bottomNavFunction();
        activeBackBut(); //Back button
        selectImage(); //select image button
        addPost(); //add post button
    }

    private void sendNotification()
    {
        String title = "New Post";
        String message = "Please check out the new post";

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
                                    NewPost.this,
                                    token.getToken(),
                                    title,
                                    message
                            );
                        }
                        startActivity(new Intent(NewPost.this, HomePage.class));
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

    private void selectImage()
    {
        imageView = findViewById(R.id.new_post_logo4);
        addImageBut = findViewById(R.id.new_post_button2);
        addImageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagesFromGallery();
            }
        });
    }

    private void addPost()
    {
        title = (TextInputEditText) findViewById(R.id.new_post_edittext);
        description = (TextInputEditText) findViewById(R.id.new_post_edittext1);
        hashtag = (TextInputEditText) findViewById(R.id.new_post_edittext2);

        postBut = findViewById(R.id.new_post_button3);
        postBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String titleTxt = title.getText().toString();
                String descriptionTxt = description.getText().toString();
                String hashtagTxt = hashtag.getText().toString();

                if (titleTxt.isEmpty() || descriptionTxt.isEmpty()) {
                    Toast.makeText(NewPost.this, "Missing fields. Please try again", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {

                    String imageId= "";
                    String postId = db.collection("Posts").document().getId();
                    if (imageUri != null) {
                        imageId = db.collection("Images").document().getId();
                    }
                    Map<String, Object> newPost = new HashMap<>();
                    newPost.put(KEY_TITLE,titleTxt);
                    newPost.put(KEY_DESCRIPTION,descriptionTxt);
                    newPost.put(KEY_HASHTAG,hashtagTxt == ""? null: hashtagTxt);
                    newPost.put(KEY_LIKE,0);
                    newPost.put(KEY_VIEW,0);
                    newPost.put(KEY_DATE,new Date());
                    newPost.put(KEY_IMAGE_ID,imageId);
                    newPost.put(KEY_ARRAY_OF_TITLE, Arrays.asList(titleTxt.trim().toLowerCase(Locale.ROOT).split(" ")));

                    db.collection("Posts").document(postId).set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                                if (imageUri != null)
                                {
                                    uploadImage(postId,(newPost.get(KEY_IMAGE_ID).toString()));
                                }
                                else
                                {
                                    sendNotification();
                                    Toast.makeText(NewPost.this, "New post added", Toast.LENGTH_SHORT).show();
                                }
                            }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPost.this, "Error", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    private String getFileExtension (Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage(String postId, String imageId)
    {
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        collectionReference = FirebaseFirestore.getInstance().collection("Images");

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
                        Map <String, Object> uploadMap = new HashMap<>();
                        uploadMap.put("post_id", postId);
                        uploadMap.put("url",photoStringLink);
                        uploadMap.put("type",1); //1 is post's image and 2 is event's image
                        collectionReference.document(imageId).set(uploadMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sendNotification();
                                Toast.makeText(NewPost.this, "New post and image added", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewPost.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                        Log.d(TAG, "Upload " + photoStringLink);
                    }
                    else
                    {
                        Toast.makeText(NewPost.this, "Cannot find URL link.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(NewPost.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }

    public void loadImagesFromGallery(){
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


    public String getCurrentTimeByString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public void activeBackBut()
    {
        Button backBut = (Button) findViewById(R.id.new_post_button);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(NewPost.this,HomePage.class);
                NewPost.this.finish();
                NewPost.this.startActivity(myIntent);
            }
        });
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.new_post_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(NewPost.this, HomePage.class);
                    NewPost.this.finish();
                    NewPost.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(NewPost.this, VideoPage.class);
                    NewPost.this.finish();
                    NewPost.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(NewPost.this, EventPage.class);
                    NewPost.this.finish();
                    NewPost.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(NewPost.this, SavedPage.class);
                    NewPost.this.finish();
                    NewPost.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(NewPost.this, ToolbarPage.class);
                    NewPost.this.finish();
                    NewPost.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}