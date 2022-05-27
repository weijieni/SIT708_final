package com.example.pika2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private EditText username;
    private EditText password;
    private EditText age;
    private int userCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        hideSystemBars();

        username = findViewById(R.id.nameTxt);
        age = findViewById(R.id.ageTxt);
        password = findViewById(R.id.passwordTxt);
        mDatabase = FirebaseDatabase.getInstance();
        Button signup = findViewById(R.id.signupBtn);
        mRef = mDatabase.getReference();

        ValueEventListener userCntListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                userCnt = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("firebase", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mRef.child("userCnt").addListenerForSingleValueEvent(userCntListener);

        signup.setOnClickListener(v -> signUp());
    }

    public void signUp(){
        String n = username.getText().toString().trim();
        String a = age.getText().toString().trim();
        String p = password.getText().toString().trim();
        User user = new User(n, a, p);

        if (TextUtils.isEmpty(n) || TextUtils.isEmpty(a) || TextUtils.isEmpty(p)) {
            Toast.makeText(getApplicationContext(), "Enter all required fields!", Toast.LENGTH_SHORT).show();
        } else {
            mRef.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    boolean hasUser = false;
                    for (DataSnapshot val : task.getResult().child("users").getChildren()) {
                        if (val.child("username").getValue(String.class).contains(n)) {
                            Toast.makeText(getApplicationContext(), "Username exists!", Toast.LENGTH_SHORT).show();
                            hasUser = true;
                        }
                    }
                    if (!hasUser) {
                        addUser(mRef,user);
                    }
                }
            });
        }
    }

    public void addUser(DatabaseReference r, User user) {
        String userId;
        TaskStatus status = new TaskStatus("false", "false", "false", "false", "false", "false", "false", "false");

        userId = String.valueOf(userCnt);

        r.child("users").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid){
                        // SUCCESS

                        // Log the details
                        Log.d("FirebaseData","user data uploaded successfully");
                        // Make a toast
                        Toast.makeText(getApplicationContext(), "user data uploaded successfully", Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){
                        // FAILURE

                        // Log the details
                        Log.d("FirebaseData","user data upload failed");
                        // Make a toast
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        r.child("users").child(userId).child("Task Status").setValue(status);
        r.child("userCnt").setValue(userCnt + 1);

        Intent intent = new Intent(this, Home.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("userage", user.getAge());
        startActivity(intent);
        finish();
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
    }
}
