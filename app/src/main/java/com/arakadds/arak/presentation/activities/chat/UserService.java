package com.arakadds.arak.presentation.activities.chat;

import androidx.annotation.NonNull;

import com.arakadds.arak.model.message.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserService {

    private DatabaseReference databaseReference;

    public DatabaseReference getInstance() {
        if (databaseReference == null)
            databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public void CheckExists(String key, UserServiceCallBack userServiceCallBack) {
        getInstance().child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = null;
                    try {
                        user = snapshot.getValue(User.class);
                        userServiceCallBack.userCallBack(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    userServiceCallBack.userCallBack(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userServiceCallBack.userCallBack(null);
            }
        });
    }

    public void CreateNewUser(String key, HashMap<String, Object> data) {
        getInstance().child("users").child(key).setValue(data);
    }

    public void UpdateUser(String key, HashMap<String, Object> data) {
        getInstance().child("users").child(key).updateChildren(data);
    }

    public void getUser(String key, UserServiceCallBack userServiceCallBack) {
        if (key.isEmpty())
            userServiceCallBack.userCallBack(null);
        getInstance().child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    userServiceCallBack.userCallBack(null);
                User user = null;
                try {
                    user = snapshot.getValue(User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userServiceCallBack.userCallBack(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                userServiceCallBack.userCallBack(null);
            }
        });
    }

    public interface UserServiceCallBack {
        void userCallBack(User user);
    }

}
