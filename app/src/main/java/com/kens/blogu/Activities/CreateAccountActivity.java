package com.kens.blogu.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kens.blogu.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createButton;
    private ProgressBar progressBar;
    private ImageButton profileImageButton;

    private final static int GALLERY_CODE = 1;
    private Uri profileImageUri = null; //image uri to image

    private DatabaseReference DBref;
    private FirebaseDatabase DB;
    private FirebaseAuth DBAuth;
    private StorageReference STO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        DB = FirebaseDatabase.getInstance();
        DBref = DB.getReference().child("MUsers");
        STO = FirebaseStorage.getInstance().getReference().child("BlogU_profile_Pics");

        DBAuth = FirebaseAuth.getInstance();

        firstName = (EditText) findViewById(R.id.firstAct);
        lastName = (EditText) findViewById(R.id.lastAct);
        email = (EditText) findViewById(R.id.emailAct);
        password = (EditText) findViewById(R.id.passwordAct);
        createButton = (Button) findViewById(R.id.createButtonAct);
        profileImageButton = (ImageButton) findViewById(R.id.profilePic);

        progressBar = (ProgressBar) findViewById(R.id.progressBarAct);
        progressBar.setVisibility(View.INVISIBLE);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }


        });

        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);

            }
        });

    }

    private void createNewAccount() {

        final String fname = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pwd = password.getText().toString().trim();

        if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) &&
            !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pwd)) {

            //start to make the new account here
            progressBar.setVisibility(View.VISIBLE);

            DBAuth.createUserWithEmailAndPassword(em,pwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            if(authResult != null){

                                StorageReference imagePath = STO.child("BlogU_profile_Pics")
                                        .child(profileImageUri.getLastPathSegment());

                                imagePath.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        String userid = DBAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDB = DBref.child(userid);

                                        currentUserDB.child("firstname").setValue(fname);
                                        currentUserDB.child("lastname").setValue(lname);
                                        currentUserDB.child("image").setValue(profileImageUri.toString());

                                        progressBar.setVisibility(View.INVISIBLE);

                                        //send user to postlist
                                        Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        startActivity(intent);
                                        finish();
                                    }
                                });






                            }

                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {

                profileImageUri = result.getUri();
                profileImageButton.setImageURI(profileImageUri);

            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }

    }
}
