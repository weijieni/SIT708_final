package com.example.pika2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private DatabaseReference mRef;
    private EditText username;
    private EditText password;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        hideSystemBars();

        username = findViewById(R.id.nameTxt);
        password = findViewById(R.id.passwordTxt);

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    public void login(View view){
        String u = username.getText().toString().trim();
        String p = password.getText().toString().trim();

        mRef = FirebaseDatabase.getInstance().getReference("users");

        if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
            Toast.makeText(getApplicationContext(), "Enter all required fields!", Toast.LENGTH_SHORT).show();
        } else {
            mRef.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    boolean hasUser = false;
                    String key = "";
                    for (DataSnapshot val : task.getResult().getChildren()) {
                        if (val.child("username").getValue(String.class).contains(u)) {
                            hasUser = true;
                            key = val.getKey();
                        }
                    }
                    if (!hasUser) {
                        Toast.makeText(getApplicationContext(), "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                    if (hasUser) {
                        String tmp = task.getResult().child(key).child("password").getValue().toString();
                        if (Objects.equals(tmp, p)) {
                            Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_SHORT).show();
                            loginSuccess(u);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
    }

    public void loginSuccess(String n) {
        viewModel.setUser_login(true);
        viewModel.setUsername(n);
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("username", n);
        startActivity(intent);
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
