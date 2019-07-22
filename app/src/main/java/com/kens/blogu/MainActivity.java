package com.kens.blogu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText userNameMain;
    private EditText passWordMain;
    private Button loginMain;
    private Button newUserMain;

    private FirebaseDatabase fbDatabase; // to grape and send data
    private DatabaseReference dbReference;
    private FirebaseUser fbUser;

    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameMain = (EditText) findViewById(R.id.emailMain);
        passWordMain = (EditText) findViewById(R.id.passwordMain);
        loginMain = (Button) findViewById(R.id.loginMain);
        newUserMain = (Button) findViewById(R.id.newAccountMain);

        fbAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                fbUser = firebaseAuth.getCurrentUser();

                if(fbUser != null){
                    Toast.makeText(MainActivity.this, "signed in", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "not signed in", Toast.LENGTH_SHORT).show();

                }
            }
        };

        loginMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(userNameMain.getText().toString()) &&
                        !TextUtils.isEmpty(passWordMain.getText().toString())) {

                    //fire login
                    String email = userNameMain.getText().toString();
                    String pwd = passWordMain.getText().toString();
                    loginUser(email, pwd);

                } else {

                }

            }


        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_signout){
            fbAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fbAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(authListener != null){
            fbAuth.removeAuthStateListener(authListener);
        }
    }

    private void loginUser(String email, String pwd) {

        fbAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            //reach here to show this user succeed on sign in
                            Toast.makeText(MainActivity.this, "we are in~", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(MainActivity.this, PostListActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "is not in...", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

}
