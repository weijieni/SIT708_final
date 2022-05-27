package com.example.pika2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity implements TaskAdapter.OnItemListener, LogoutDialogFragment.LogoutDialogListener {
    private TaskStatus taskStatus;
    private RecyclerView taskRV;
    private String username, userage;
    private DialogFragment dialogFragment;
    RoomDB localDB;
    List<String> taskNameList = new ArrayList<>(Arrays.asList("Addition 1", "Addition 2", "Subtraction 1", "Subtraction 2", "Multiplication 1", "Multiplication 2", "Division 1", "Division 2"));

    // Arraylist for storing data
    private ArrayList<TaskModel> taskModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskRV = findViewById(R.id.tasks);

        hideSystemBars();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");

        localDB = RoomDB.getInstance(getApplicationContext());
        userage = localDB.userDao().get_userage(1);
        username = localDB.userDao().get_username(1);
        String welcomeTxt = "Hi! " + username + "!";

        TextView userTxt = findViewById(R.id.nameTxt);
        userTxt.setText(welcomeTxt);

        mRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                String key = "";
                for (DataSnapshot val : task.getResult().getChildren()) {
                    if (val.child("username").getValue(String.class).contains(username)) {
                        key = val.getKey();
                    }
                }
                taskStatus = task.getResult().child(key).child("Task Status").getValue(TaskStatus.class);
                updateUI(taskStatus);
            }
        });
    }

    public void updateUI(TaskStatus taskStatus) {
        taskModelArrayList.clear();
        if (taskStatus != null) {
            if (Objects.equals(taskStatus.getTask1Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(0), 1, R.drawable.incomplete, taskStatus.getTask1Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(0), 1, R.drawable.complete, taskStatus.getTask1Stat()));
            }
            if (Objects.equals(taskStatus.getTask2Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(1), 1, R.drawable.incomplete, taskStatus.getTask2Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(1), 1, R.drawable.complete, taskStatus.getTask2Stat()));
            }
            if (Objects.equals(taskStatus.getTask3Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(2), 2, R.drawable.incomplete, taskStatus.getTask3Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(2), 2, R.drawable.complete, taskStatus.getTask3Stat()));
            }
            if (Objects.equals(taskStatus.getTask4Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(3), 2, R.drawable.incomplete, taskStatus.getTask4Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(3), 2, R.drawable.complete, taskStatus.getTask4Stat()));
            }
            if (Objects.equals(taskStatus.getTask5Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(4), 3, R.drawable.incomplete, taskStatus.getTask5Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(4), 3, R.drawable.complete, taskStatus.getTask5Stat()));
            }
            if (Objects.equals(taskStatus.getTask6Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(5), 3, R.drawable.incomplete, taskStatus.getTask6Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(5), 3, R.drawable.complete, taskStatus.getTask6Stat()));
            }
            if (Objects.equals(taskStatus.getTask7Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(6), 4, R.drawable.incomplete, taskStatus.getTask7Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(6), 4, R.drawable.complete, taskStatus.getTask7Stat()));
            }
            if (Objects.equals(taskStatus.getTask8Stat(), "false")) {
                taskModelArrayList.add(new TaskModel(taskNameList.get(7), 4, R.drawable.incomplete, taskStatus.getTask8Stat()));
            } else {
                taskModelArrayList.add(new TaskModel(taskNameList.get(7), 4, R.drawable.complete, taskStatus.getTask8Stat()));
            }
        }

        TaskAdapter taskAdapter = new TaskAdapter(this, taskModelArrayList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        taskRV.setLayoutManager(linearLayoutManager);
        taskRV.setAdapter(taskAdapter);
    }

    @Override
    public void onItemClick(int position) {
        taskModelArrayList.get(position);
        Intent intent = new Intent(this, InTask.class);
        intent.putExtra("taskName", taskNameList.get(position));
        startActivity(intent);
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        dialogFragment = new LogoutDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "LogoutDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        localDB.userDao().update_login(1, false);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        dialogFragment.dismiss();
    }
}
