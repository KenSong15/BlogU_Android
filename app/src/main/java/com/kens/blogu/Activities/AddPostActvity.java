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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kens.blogu.Model.Blog;
import com.kens.blogu.R;

public class AddPostActvity extends AppCompatActivity {

    private ImageButton aImageButton;
    private EditText aPostTitle;
    private EditText aPostDesc;
    private Button aAddButton;

    private DatabaseReference aDBReference;
    private FirebaseAuth afbAuth;
    private FirebaseUser afbDBUser;

    //progressDialog is not supported by API28 anymore
    private ProgressBar progressBar;

    private static final int GALLERY_CODE = 1;
    private Uri imageUri; //image uri to image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_actvity);

        afbAuth = FirebaseAuth.getInstance();
        afbDBUser = afbAuth.getCurrentUser();

        aDBReference = FirebaseDatabase.getInstance().getReference().child("BlogU");

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


        String titleVal = aPostTitle.getText().toString().trim();
        String descVal = aPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)){
            //start the uploading


            Blog blog = new Blog("title", "description",
                    "imageUrl", "timestamp", "user1");

            aDBReference.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "item added", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }
    }
}
