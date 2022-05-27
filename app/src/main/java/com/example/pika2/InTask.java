package com.example.pika2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.divyanshu.draw.widget.DrawView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class InTask extends AppCompatActivity implements ExitTaskDialogFragment.ExitTaskDialogListener {
    private int taskNum;
    private int taskCnt;
    private int currentProgress = 100;
    private boolean taskComplete;
    private String answer, taskName, taskName_i, tmpDigit, userAnswer;
    private final ArrayList<String> taskList = new ArrayList<>();
    private final ArrayList<String> answerList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView qText, pText, aText;
    private DrawView drawView;
    private Button add, delete;
    private Handler handler;
    private DialogFragment dialogFragment;
    private final MyThread thread = new MyThread();
    private DigitClassifier digitClassifier = new DigitClassifier(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        hideSystemBars();

        Intent intent = getIntent();
        taskName_i = intent.getStringExtra("taskName");
        taskName = taskName_i.toLowerCase(Locale.ROOT).replaceAll(" ","");
        Toast.makeText(this,taskName_i,Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.progress);
        qText = findViewById(R.id.qTxt);
        pText = findViewById(R.id.pTxt);
        aText = findViewById(R.id.aTxt);
        drawView = findViewById(R.id.draw_view);
        add = findViewById(R.id.add);
        delete = findViewById(R.id.delete);

        taskComplete = false;

        progressBar.setProgress(currentProgress);
        progressBar.setMin(0);

        drawView.setStrokeWidth(50.0f);
        drawView.setColor(Color.WHITE);
        drawView.setBackgroundColor(Color.BLACK);

        // Setup classification trigger so that it classify after every stroke drew
        drawView.setOnTouchListener((v, event) -> {
            drawView.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_UP) {
                classifyDrawing();
            }

            return true;
        });

        digitClassifier.initialize();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskName);

        mRef.get().addOnCompleteListener(task -> {
            taskList.clear();
            answerList.clear();
            for (DataSnapshot snapshot1: task.getResult().getChildren()) {
                taskList.add(snapshot1.child("q").getValue().toString());
                answerList.add(snapshot1.child("a").getValue().toString());
            }
            qText.setText(taskList.get(taskNum - 1));
            answer = answerList.get(taskNum - 1);
        });

        add.setOnClickListener(v -> {
            addNumber();
        });

        delete.setOnClickListener(v -> {
            deleteNumber();
        });

        taskNum = 1;
        taskCnt = 0;
        userAnswer = "";

        pText.setText(taskNum + "/10");

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d("thread_pika", "running");
                currentProgress -= 2;
                progressBar.setProgress(currentProgress);
                if (aText.getText().toString().equals(answer)) {
                    taskCnt += 1;
                    taskNum += 1;
                    if (taskNum == 11) {
                        onTaskComplete();
                    } else if (taskNum <= 10){
                        userAnswer = "";
                        aText.setText("");
                        answer = answerList.get(taskNum - 1);
                        qText.setText(taskList.get(taskNum - 1));
                        pText.setText(taskNum + "/10");
                        currentProgress = 100;
                    }
                } else if (currentProgress == 0) {
                    taskNum += 1;
                    if (taskNum == 11) {
                        onTaskComplete();
                    } else if (taskNum <= 10){
                        userAnswer = "";
                        aText.setText("");
                        qText.setText(taskList.get(taskNum - 1));
                        answer = answerList.get(taskNum - 1);
                        pText.setText(taskNum + "/10");
                        currentProgress = 100;
                    }
                }
            }
        };

        thread.start();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        thread.interrupt();
        digitClassifier.close();
    }

    private void classifyDrawing() {
        Bitmap bitmap = drawView.getBitmap();

        if ((bitmap != null) && (digitClassifier.isInitialized())) {
            digitClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener(s -> tmpDigit = s)
                    .addOnFailureListener(e -> Log.e("DigitClassifier", "Error classifying drawing.", e));
        }
    }

    private void addNumber() {
        userAnswer += tmpDigit;
        aText.setText(userAnswer);
        drawView.clearCanvas();
    }

    private void deleteNumber() {
        drawView.clearCanvas();
        if (userAnswer.length() > 0) {
            StringBuffer sb = new StringBuffer(userAnswer);
            sb.deleteCharAt(sb.length()-1);
            userAnswer = sb.toString();
            aText.setText(userAnswer);
        }
    }

    public void onTaskComplete() {
        taskComplete = true;
        thread.interrupt();
        Intent intent;
        if (taskCnt == 10) {
            intent = new Intent(this, Success.class);
            intent.putExtra("taskName", taskName_i);
        } else {
            intent = new Intent(this, Fail.class);
        }
        startActivity(intent);
        finish();
    }

    public void showDialog() {
        dialogFragment = new ExitTaskDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ExitTaskDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        dialogFragment.dismiss();
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

    class MyThread extends Thread {
        @Override
        public void run() {
            while(!taskComplete) {
                Log.d("pika_thread", "running");
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
