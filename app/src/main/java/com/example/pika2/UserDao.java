package com.example.pika2;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void insert(UserState userState);

    @Delete
    void delete(UserState userState);

    @Delete
    void reset(List<UserState> userStates);

    // update user login status
    @Query("UPDATE user_table SET user_login = :sStatus WHERE ID = :id")
    void update_login(int id, boolean sStatus);

    // get userage
    @Query("SELECT userage FROM user_table WHERE ID = :id")
    String get_userage(int id);

    // get username
    @Query("SELECT username FROM user_table WHERE ID = :id")
    String get_username(int id);

    // get user_login
    @Query("SELECT user_login FROM user_table WHERE ID = :id")
    boolean get_user_login(int id);
}
