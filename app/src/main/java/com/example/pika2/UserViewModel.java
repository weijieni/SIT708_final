package com.example.pika2;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<String> username = new MutableLiveData<String>();
    private String userage, taskname;
    private boolean user_login;

    public void setUsername(String name) {
        username.setValue(name);
    }

    public void setUserage(String age) {
        userage = age;
    }

    public void setTaskname(String name) {
        taskname = name;
    }

    public void setUser_login(boolean login) {
        user_login = login;
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public String getUserage() {
        return userage;
    }

    public String getTaskname() {
        return taskname;
    }

    public boolean isUser_login() {
        return user_login;
    }
}
