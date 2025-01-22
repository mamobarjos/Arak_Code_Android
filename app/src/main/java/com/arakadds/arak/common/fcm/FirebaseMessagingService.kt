package com.arakadds.arak.common.fcm

import android.util.Log
import com.arakadds.arak.common.NotificationsHelper
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Calendar

class FirebaseMessagingService : FirebaseMessagingService() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }

    /*    ArrayList<InComingNotification> inComingNotificationArrayList;
    private InComingNotification inComingNotification = new InComingNotification();*/
    /*   @Override
       public void onNewToken(String s) {
           Log.e("NEW_TOKEN", s);
       }
   */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FBMessagingService", "onMessageReceived: tt inComingNotificationArrayList ")

        /*Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("AAAARTx2760:APA91bGpWjL9NSylHqKEhy8LmgWQhygP2DXxJiBkocH7JtK2iFfKvdKC0EZ44Um14qr0QS7vyrnNzSC-1rUHw6We6KmqXiZdQ0YlCsOPrqy1JqwIHbaAilPerM3rW8Ob8T4SaBjhAZ1L");
*/

        //inComingNotificationArrayList = new ArrayList<>();

        val notificationStatus = preferenceHelper.getNotificationStatus()
        if (notificationStatus) {
            if (remoteMessage.notification != null) {
                //to load the old data and reinsert it to the array
                //loadData();
                // Log.d(TAG, "onMessageReceived: inComingNotificationArrayList : loadData method: " + inComingNotificationArrayList.size());
                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a")
                val dateTime = simpleDateFormat.format(calendar.time)
                val title = remoteMessage.notification!!.title
                val body = remoteMessage.notification!!.body
                //inComingNotification.setTitle(title);
                //inComingNotification.setBody(body);
                //inComingNotificationArrayList.add(inComingNotification);
                //inComingNotification.setDateTime(dateTime);
                //saveNotificationLocally();
                // Log.d(TAG, "onMessageReceived: inComingNotificationArrayList : size is: " + inComingNotificationArrayList.size());
                //Log.d(TAG, "onMessageReceived: inComingNotificationArrayList : title is: " + inComingNotificationArrayList.get(inComingNotificationArrayList.size() - 1).getTitle());
                //Log.d(TAG, "onMessageReceived: inComingNotificationArrayList : body is: " + inComingNotificationArrayList.get(inComingNotificationArrayList.size() - 1).getBody());
                NotificationsHelper.makeNotification(
                    applicationContext,
                    HomeActivity::class.java, notificationStatus, title, body
                )
            }
        }
    } /*  private void saveNotificationLocally() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("arrayList SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(inComingNotificationArrayList);
        editor.putString("task list", json);
        editor.apply();
    }
*/

    /*    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("arrayList SharedPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<InComingNotification>>() {
        }.getType();
        inComingNotificationArrayList = gson.fromJson(json, type);
        if (inComingNotificationArrayList == null) {
            inComingNotificationArrayList = new ArrayList<>();
            Log.d(TAG, "loadData: if:  digitalCartArrayList array size: " + inComingNotificationArrayList.size());
        }
        Log.d(TAG, "loadData: digitalCartArrayList array size: " + inComingNotificationArrayList.size());
    }*/
}
