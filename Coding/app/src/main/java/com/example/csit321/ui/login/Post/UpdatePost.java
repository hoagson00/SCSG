package com.example.csit321.ui.login.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.csit321.EntityClass.Event;
import com.example.csit321.EntityClass.Post;
import com.example.csit321.Format.DateFormat;
import com.example.csit321.R;
import com.example.csit321.ui.login.Event.EventPage;
import com.example.csit321.ui.login.Event.UpdateEvent;
import com.example.csit321.ui.login.Save.SavedPage;
import com.example.csit321.ui.login.Toolbar.ToolbarPage;
import com.example.csit321.ui.login.Video.VideoPage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdatePost extends AppCompatActivity {

    private static final String TAG = "UpdatePost";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_HASHTAG = "hashtag";
    private static final String KEY_IMAGE_ID = "image_id";
    private static final String KEY_ARRAY_OF_TITLE = "title_array";

    private Uri imageUri;

    private FirebaseFirestore db;

    private ProgressDialog pd;

    private EditText title, description, hashtag;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait!!");

        setData(getPostId()); //show the current post's details for admin
        updatePost(getPostId()); //update post details
        selectImage(); //select image button
        deleteButton(getPostId()); // delete the post
        backButton(getPostId());// back button
        bottomNavFunction();// bottom navigation function
    }



    private void deletePost(String postId)
    {
        pd.show();
        DocumentReference docRef = db.collection("Posts").document(postId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Post post = new Post();
                        post.setPostID(document.getId());
                        post.setImageId(document.getString("image_id"));
                        if (!post.getImageId().equals(""))
                        {
                            deleteStorageFile(post.getImageId(), "delete");
                        }
                        else
                        {
                            deleteLikedOfUser(postId);
                        }
                    }
                    else
                    {
                        Toast.makeText(UpdatePost.this, "Cannot find post.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
                else
                {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(UpdatePost.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdatePost.this, "Error"+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void deleteLikedOfUser(String postId)
    {
        db.collection("Posts").document(postId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection("Users").whereArrayContains("posts_liked", postId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : querySnapshot)
                            {
                                db.collection("Users").document(document.getId()).update("posts_liked", FieldValue.arrayRemove(postId))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
//                                                Toast.makeText(UpdatePost.this, "", Toast.LENGTH_SHORT).show();
//                                                pd.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(UpdatePost.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                                                pd.dismiss();
                                            }
                                        });
                            }
                            Toast.makeText(UpdatePost.this, "Delete post successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(UpdatePost.this, HomePage.class));
                            pd.dismiss();
                        }
                        else
                        {
                            Toast.makeText(UpdatePost.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdatePost.this, "Error"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdatePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void deleteButton(String postId)
    {
        Button button = findViewById(R.id.update_post_button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePost.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Do you want to delete this post?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });
    }

    private void backButton(String postId)
    {
        Button button = findViewById(R.id.update_post_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdatePost.this, PostDetail.class);
                intent.putExtra("postId", postId);
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
                                Toast.makeText(UpdatePost.this, "Update successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdatePost.this, PostDetail.class);
                                intent.putExtra("postId", post.getPostID());
                                finish();
                                startActivity(intent);
                                pd.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdatePost.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    }
                    else {
                        Toast.makeText(UpdatePost.this, "Cant find URL", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(UpdatePost.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }

    private void deleteStorageFile(String imageId, String status)
    {

        db.collection("Images").document(imageId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String postId = documentSnapshot.getString("post_id");
                        String url = documentSnapshot.getString("url");
                        StorageReference fileStorage = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        fileStorage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (status.equals("delete")) {
                                        db.collection("Images").document(imageId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            deleteLikedOfUser(postId);
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(UpdatePost.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            pd.dismiss();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpdatePost.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        });
                                    }
                                    else if (status.equals("update"))
                                    {
                                        uploadImage(imageId);
                                    }
                                    else {
                                        Toast.makeText(UpdatePost.this, "Delete post successfully.", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(UpdatePost.this, HomePage.class));
                                        pd.dismiss();
                                    }
                                }
                                else {
                                    Toast.makeText(UpdatePost.this, "Error " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdatePost.this, "Delete Images failed:" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Log.d(TAG, "Delete file successfully");
                }
            }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdatePost.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

    private String getPostId()
    {
        String postId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            postId = extras.getString("postId");
        }
        return postId;
    }


    private void updatePost(String postId)
    {
        Button button = findViewById(R.id.update_post_button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                String newTitle = title.getText().toString();
                String newDescription = description.getText().toString();
                String newHashTag = hashtag.getText().toString();
                String newTitleArray = newTitle.trim().toLowerCase(Locale.ROOT);

                if (newTitle.isEmpty() || newDescription.isEmpty()) {
                    Toast.makeText(UpdatePost.this, "Missing fields. Please try again", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else
                {
                    Map<String, Object> updatePost = new HashMap<>();
                    updatePost.put(KEY_TITLE, newTitle);
                    updatePost.put(KEY_DESCRIPTION, newDescription);
                    updatePost.put(KEY_HASHTAG, newHashTag == "" ? null : newHashTag);
                    updatePost.put(KEY_ARRAY_OF_TITLE, Arrays.asList(newTitleArray.split(" ")));

                    db.collection("Posts").document(postId).update(updatePost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (imageUri != null)
                            {
                                deleteStorageFile(post.getImageId(), "update");
                            }
                            else
                            {
                                Toast.makeText(UpdatePost.this, "Update successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdatePost.this, PostDetail.class);
                                intent.putExtra("postId", postId);
                                finish();
                                startActivity(intent);
                                pd.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdatePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void setData(String postId)
    {
        DateFormat dateFormat = new DateFormat();
        DocumentReference docRef = db.collection("Posts").document(postId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        post = new Post();
                        post.setPostID(document.getId());
                        post.setTitle(document.getString("title"));
                        post.setDescription(document.getString("description"));
                        post.setHashtag(document.getString("hashtag"));
                        post.setImageId(document.getString("image_id"));
                        post.setDate(document.getDate("date_posted"));
//                        try {
//                            post.setDate(dateFormat.StringToDate1(document.getString("date_posted")));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }

                        title = (EditText) findViewById(R.id.update_post_textView);
                        title.setText(post.getTitle());

                        description = (EditText) findViewById(R.id.update_post_textView1);
                        description.setText(post.getDescription());

                        hashtag = (EditText) findViewById(R.id.update_post_textView2);
                        hashtag.setText(post.getHashtag());

                        TextView date = (TextView) findViewById(R.id.update_post_textView3);
                        try {
                            date.setText("Posted in: " + dateFormat.dateToString1(post.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        loadImage(post.getImageId());
//                        updateView(postId, post.getViewCount());
                    }
                    else
                    {
                        Log.d(TAG, "Cannot find post");
                    }
                }
                else
                {
                    Log.d(TAG, "Get failed with", task.getException());
                    Toast.makeText(UpdatePost.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadImage(String imageId)
    {
        ImageView imageView = (ImageView) findViewById(R.id.update_post_imageView);
        if (imageId == null || imageId.equals(""))
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
                        Toast.makeText(UpdatePost.this, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void selectImage()
    {
        ImageView imageView = findViewById(R.id.update_post_imageView);
        Button addImageBut = findViewById(R.id.update_post_button2);
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
        ImageView imageView = findViewById(R.id.update_post_imageView);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (imageUri != null) {
                Picasso.with(this).load(imageUri).into(imageView);
                imageView.setImageURI(imageUri);
            }
        }
    }

    private void bottomNavFunction()
    {
        BottomNavigationView nav = findViewById(R.id.update_post_nav);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homelogo:
                {
                    Intent myIntent = new Intent(UpdatePost.this, HomePage.class);
                    UpdatePost.this.finish();
                    UpdatePost.this.startActivity(myIntent);
                    break;
                }
                case R.id.videologo:
                {
                    Intent myIntent = new Intent(UpdatePost.this, VideoPage.class);
                    UpdatePost.this.finish();
                    UpdatePost.this.startActivity(myIntent);
                    break;
                }
                case R.id.eventlogo:
                {
                    Intent myIntent = new Intent(UpdatePost.this, EventPage.class);
                    UpdatePost.this.finish();
                    UpdatePost.this.startActivity(myIntent);
                    break;
                }
                case R.id.savedlogo:
                {
                    Intent myIntent = new Intent(UpdatePost.this, SavedPage.class);
                    UpdatePost.this.finish();
                    UpdatePost.this.startActivity(myIntent);
                    break;
                }
                case R.id.toolbarlogo:
                {
                    Intent myIntent = new Intent(UpdatePost.this, ToolbarPage.class);
                    UpdatePost.this.finish();
                    UpdatePost.this.startActivity(myIntent);
                    break;
                }
            }
            return true;
        });
    }
}