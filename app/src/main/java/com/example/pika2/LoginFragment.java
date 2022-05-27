package com.example.pika2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private DatabaseReference mRef;
    private EditText username;
    private EditText password;
    private String u, a;
    private UserState userState;
    RoomDB localDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConstraintLayout loginLayout = (ConstraintLayout) inflater.inflate(R.layout.login, null);

        username = loginLayout.findViewById(R.id.nameTxt);
        password = loginLayout.findViewById(R.id.passwordTxt);
        Button login = loginLayout.findViewById(R.id.loginBtn);

        login.setOnClickListener(v -> login());

        return loginLayout;
    }

    public void login(){
        u = username.getText().toString().trim();
        String p = password.getText().toString().trim();

        mRef = FirebaseDatabase.getInstance().getReference("users");

        if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
            Toast.makeText(getActivity(), "Enter all required fields!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                    if (hasUser) {
                        String tmp = task.getResult().child(key).child("password").getValue().toString();
                        if (Objects.equals(tmp, p)) {
                            a = task.getResult().child(key).child("age").getValue().toString();
                            Toast.makeText(getActivity(), "Login success!", Toast.LENGTH_SHORT).show();
                            loginSuccess(u, a);
                        } else {
                            Toast.makeText(getActivity(), "Wrong password!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
    }

    public void loginSuccess(String u, String a) {
        localDB = RoomDB.getInstance(requireContext());
        userState = new UserState();
        userState.setID(1);
        userState.setUsername(u);
        userState.setUserage(a);
        userState.setUser_login(true);
        localDB.userDao().insert(userState);
        Intent intent = new Intent(getActivity(), Home.class);
        startActivity(intent);
        getActivity().finish();
    }
}
