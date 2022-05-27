package com.example.pika2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpFragment extends Fragment {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private EditText username;
    private EditText password;
    private EditText age;
    private String n,a,p;
    private UserState userState;
    private UserViewModel viewModel;
    private int userCnt;
    RoomDB localDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConstraintLayout signupLayout = (ConstraintLayout) inflater.inflate(R.layout.signup, null);

        username = signupLayout.findViewById(R.id.nameTxt);
        age = signupLayout.findViewById(R.id.ageTxt);
        password = signupLayout.findViewById(R.id.passwordTxt);
        mDatabase = FirebaseDatabase.getInstance();
        Button signup = signupLayout.findViewById(R.id.signupBtn);
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
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

        return signupLayout;
    }

    public void signUp(){
        n = username.getText().toString().trim();
        a = age.getText().toString().trim();
        p = password.getText().toString().trim();
        User user = new User(n, a, p);

        if (TextUtils.isEmpty(n) || TextUtils.isEmpty(a) || TextUtils.isEmpty(p)) {
            Toast.makeText(getContext(), "Enter all required fields!", Toast.LENGTH_SHORT).show();
        } else {
            mRef.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    boolean hasUser = false;
                    for (DataSnapshot val : task.getResult().child("users").getChildren()) {
                        if (val.child("username").getValue(String.class).contains(n)) {
                            Toast.makeText(getContext(), "Username exists!", Toast.LENGTH_SHORT).show();
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // SUCCESS

                        // Log the details
                        Log.d("FirebaseData", "user data uploaded successfully");
                        // Make a toast
                        Toast.makeText(getContext(), "user data uploaded successfully", Toast.LENGTH_LONG).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // FAILURE

                        // Log the details
                        Log.d("FirebaseData", "user data upload failed");
                        // Make a toast
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        r.child("users").child(userId).child("Task Status").setValue(status);
        r.child("userCnt").setValue(userCnt + 1);

        userState = new UserState();
        localDB = RoomDB.getInstance(getContext());
        userState.setID(1);
        userState.setUsername(n);
        userState.setUserage(a);
        userState.setUser_login(true);
        localDB.userDao().insert(userState);

        Intent intent = new Intent(getActivity(), Home.class);
        startActivity(intent);
        getActivity().finish();
    }

}
