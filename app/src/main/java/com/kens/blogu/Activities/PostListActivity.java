package com.kens.blogu.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kens.blogu.R;

public class PostListActivity extends AppCompatActivity {

    private DatabaseReference fbDBReference;
    private FirebaseDatabase fbDB;
    private FirebaseUser fbDBUser;
    private FirebaseAuth fbDBAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        fbDBAuth = FirebaseAuth.getInstance();
        fbDBUser = fbDBAuth.getCurrentUser();

        fbDB = FirebaseDatabase.getInstance();
        fbDBReference = fbDB.getReference().child("BlogU");
        fbDBReference.keepSynced(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_add:
                if(fbDBUser != null && fbDBAuth!= null){
                    //signed in and going to make a new post
                    startActivity(new Intent(PostListActivity.this, AddPostActvity.class));
                    finish();
                }
                break;

            case R.id.action_signout:
                if(fbDBUser != null && fbDBAuth!= null){
                    //signed out and go back to the main activity
                    fbDBAuth.signOut();
                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
