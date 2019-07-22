package com.kens.blogu.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kens.blogu.Model.Blog;
import com.kens.blogu.R;

import java.util.HashMap;
import java.util.Map;

public class AddPostActvity extends AppCompatActivity {

    private ImageButton aImageButton;
    private EditText aPostTitle;
    private EditText aPostDesc;
    private Button aAddButton;

    private DatabaseReference DBReference;
    private FirebaseAuth DBAuth;
    private FirebaseUser DBUser;

    private StorageReference mStorage;

    //progressDialog is not supported by API28 anymore
    private ProgressBar progressBar;

    private static final int GALLERY_CODE = 1;
    private Uri imageUri; //image uri to image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_actvity);

        DBAuth = FirebaseAuth.getInstance();
        DBUser = DBAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();

        DBReference = FirebaseDatabase.getInstance().getReference().child("BlogU");

        aImageButton = (ImageButton)  findViewById(R.id.imageButton);
        aPostTitle = (EditText) findViewById(R.id.postTitleET);
        aPostDesc = (EditText) findViewById(R.id.postDesET);
        aAddButton = (Button) findViewById(R.id.submitPost);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        aAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //post to database
                startPosting();
            }
        });

        aImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK)/*everything ok*/{

            imageUri = data.getData();
            aImageButton.setImageURI(imageUri);


        }
    }

    private void startPosting() {

        //this is an alternative way to set the progress dialog
        //setProgressDialog();
        progressBar.setVisibility(View.VISIBLE);


        final String titleVal = aPostTitle.getText().toString().trim();
        final String descVal = aPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) &&
                imageUri != null){
            //start the uploading

            StorageReference filePath = mStorage.child("BlogU_Image").
                    child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> Uri1 = taskSnapshot.getStorage().getDownloadUrl();
                    while(!Uri1.isComplete()); //what is this....
                    Uri downloadurl = Uri1.getResult();

                    DatabaseReference newPost = DBReference.push();

                    Map<String, String> dataToSave = new HashMap<>();

                    dataToSave.put("title", titleVal);
                    dataToSave.put("desc", descVal);
                    dataToSave.put("image", downloadurl.toString());
                    dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userid", DBUser.getUid());

//                    //the olf way to storage
//                    newPost.child("title").setValue(titleVal);
//                    newPost.child("desc").setValue(descVal);
//                    newPost.child("image").setValue(downloadurl.toString());
//                    newPost.child("timestamp").setValue(java.lang.System.currentTimeMillis());

                    newPost.setValue(dataToSave);
                    progressBar.setVisibility(View.INVISIBLE); //turn off the progress bar
                }
            });

//             //testing the put on storage functionality
//            Blog blog = new Blog("title", "description",
//                    "imageUrl", "timestamp", "user1");
//
//            DBReference.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Toast.makeText(getApplicationContext(), "item added", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
//            });

        }
    }
}
