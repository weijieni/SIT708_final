package com.example.pika2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Success extends AppCompatActivity {
    private String username;
    private RoomDB localDB;
    private final ArrayList<String> taskNameList = new ArrayList<>(Arrays.asList("Addition 1", "Addition 2", "Subtraction 1", "Subtraction 2", "Multiplication 1", "Multiplication 2", "Division 1", "Division 2"));
    private final ArrayList<String> taskStatList = new ArrayList<>(Arrays.asList("task1Stat", "task2Stat", "task3Stat", "task4Stat", "task5Stat", "task6Stat", "task7Stat", "task8Stat"));
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congrats);

        hideSystemBars();

        Intent intent = getIntent();
        localDB = RoomDB.getInstance(this);
        username = localDB.userDao().get_username(1);
        String taskName = intent.getStringExtra("taskName");
        int pos = taskNameList.indexOf(taskName);
        Log.d("TaskPos", String.valueOf(pos));

        Button backToMenu = findViewById(R.id.backMenuBtn_success);
        TextView name = findViewById(R.id.nameTxt);

        mRef = FirebaseDatabase.getInstance().getReference("users");

        mRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                String key = "";
                for (DataSnapshot val : task.getResult().getChildren()) {
                    if (val.child("username").getValue(String.class).contains(username)) {
                        key = val.getKey();
                        break;
                    }
                }

                mRef.child(key).child("Task Status").child(taskStatList.get(pos)).setValue("true");
            }
        });

        name.setText(username);
        backToMenu.setOnClickListener(v -> onTaskSuccess());
    }

    public void onTaskSuccess() {
        Intent intent = new Intent(this, Home.class);
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
