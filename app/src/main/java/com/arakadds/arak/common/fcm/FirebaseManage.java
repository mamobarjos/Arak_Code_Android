package com.arakadds.arak.common.fcm;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;


public class FirebaseManage {
    private static final String TAG = "FirebaseManage";
    private static FirebaseManage instance;

    public static FirebaseManage shared() {
        if (instance == null) {
            instance = new FirebaseManage();
        }
        return instance;
    }

    private FirebaseManage() {
    }

    public void updateStatus(int userId,String status) {

        if (userId == -1) {
            return;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("messages-status").child(String.valueOf(userId) + "-").setValue(status);
    }

    private void checkMessagesStatusExist(CheckExistCallBack checkExistCallBack) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("messages-status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                checkExistCallBack.checkExist(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                checkExistCallBack.checkExist(false);
            }
        });
    }

    public void getFCMToken(FCMCallBack fcmCallBack) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    fcmCallBack.getFCM(task.getResult());
                } else {
                    fcmCallBack.getFCM("");
                }
            }
        });
    }

    public void messagesStatus(String desId, String title, String message, String chatKey) {

        checkMessagesStatusExist(isExists -> {

            if (isExists) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("messages-status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        HashMap<String, String> data = (HashMap<String, String>) snapshot.getValue();
                        Log.d(TAG, "onDataChange: snapshot: " + data);
                        Log.d(TAG, "onDataChange: desId: " + desId);
                        if (data.containsKey(desId + "-")) {
                            String status = data.get(desId + "-");
                            if (status.equalsIgnoreCase("0")) {
                                //Call Push Notification Api
                                FirebaseDatabase.getInstance().getReference().child("user-messages").child(chatKey).child("seen").setValue(false);
                                //FCMPushNotification(desId, title, message);
                            }
                        } else {
                            //Call Push Notification Api
                            //FCMPushNotification(desId, title, message);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            } else {
                // Call Push Notification
                //FCMPushNotification(desId, title, message);
            }
        });
    }

    /*private void FCMPushNotification(String userId, String title, String body) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", userId);
        hashMap.put("desc", body);
        hashMap.put("title", title);
        hashMap.put("type", "normal");
        Log.d(TAG, "FCMPushNotification: ssssss" + hashMap.toString());
        try {
            String token = SharedPreferencesHelper.getSharedPreferencesString(KEY_TOKEN, "non");
            Controller.pushNotification(token, hashMap, new Reception((data, tag) -> {
                if (data != null && tag == TAGS.PUSH_NOTIFICATION_ID) {
                    Log.d(TAG, "FCMPushNotification: response true if for test");
                    BaseResponse baseResponse = (BaseResponse) data;

                    if (baseResponse != null) {
                        if (baseResponse.getStatusCode() == 200) {

                        }
                    } else {
                        BaseResponse baseResponse1 = (BaseResponse) data;
                        Toaster.show(baseResponse1.getDescription());
                    }
                }
            }));
        } catch (Exception e) {
            Log.d(TAG, "FCMPushNotification: Exception: " + e.getMessage());
        }
    }*/

    public interface FCMCallBack {
        void getFCM(String fcm);
    }

    public interface CheckExistCallBack {
        void checkExist(Boolean isExists);

    }

}

