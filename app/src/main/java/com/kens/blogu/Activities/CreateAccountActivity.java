package com.kens.blogu.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kens.blogu.R;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createButton;
    private ProgressBar progressBar;

    private DatabaseReference DBref;
    private FirebaseDatabase DB;
    private FirebaseAuth DBAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        DB = FirebaseDatabase.getInstance();
        DBref = DB.getReference().child("MUsers");

        DBAuth = FirebaseAuth.getInstance();

        firstName = (EditText) findViewById(R.id.firstAct);
        lastName = (EditText) findViewById(R.id.lastAct);
        email = (EditText) findViewById(R.id.emailAct);
        password = (EditText) findViewById(R.id.passwordAct);
        createButton = (Button) findViewById(R.id.createButtonAct);

        progressBar = (ProgressBar) findViewById(R.id.progressBarAct);
        progressBar.setVisibility(View.INVISIBLE);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
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
                                String userid = DBAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDB = DBref.child(userid);

                                currentUserDB.child("firstname").setValue(fname);
                                currentUserDB.child("lastname").setValue(lname);
                                currentUserDB.child("image").setValue("none");

                                progressBar.setVisibility(View.INVISIBLE);

                                //send user to postlist
                                Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                startActivity(intent);

                            }

                        }
                    });
        }

    }
}
