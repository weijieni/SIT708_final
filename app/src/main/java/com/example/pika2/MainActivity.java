package com.example.pika2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction ft;
    LoginFragment lf = new LoginFragment();
    SignUpFragment sf = new SignUpFragment();
    WelcomeFragment wf = new WelcomeFragment();
    private Button login, signup;
    RoomDB localDB;

    @Override
    public void onStart() {
        super.onStart();

        localDB = RoomDB.getInstance(this);
        boolean user_login = localDB.userDao().get_user_login(1);
        if (user_login) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        login = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signupBtn);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.holder, wf);
        ft.commit();

        login.setOnClickListener(v -> {
            signup.setBackgroundResource(R.drawable.btn_style_fragment);
            login.setBackgroundResource(R.drawable.btn_style_active);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.holder, lf);
            ft.commit();
        });

        signup.setOnClickListener(v -> {
            signup.setBackgroundResource(R.drawable.btn_style_active);
            login.setBackgroundResource(R.drawable.btn_style_fragment);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.holder, sf);
            ft.commit();
        });

        hideSystemBars();
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