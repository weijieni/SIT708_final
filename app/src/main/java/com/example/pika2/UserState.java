package com.example.pika2;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user_table")
public class UserState implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "userage")
    private String userage;

    @ColumnInfo(name = "user_login")
    private boolean user_login;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserage() {
        return userage;
    }

    public void setUserage(String userage) {
        this.userage = userage;
    }

    public boolean isUser_login() {
        return user_login;
    }

    public void setUser_login(boolean user_login) {
        this.user_login = user_login;
    }
}
