package com.arakadds.arak.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.model.ImageUploadModel
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID

class GlobalMethods {

    private val TAG = "GlobalMethods"
    private var instance: GlobalMethodsOldClass? = null
    private val mStorageReference = FirebaseStorage.getInstance().reference
    fun getInstance(): GlobalMethodsOldClass? {
        if (instance == null) {
            instance = GlobalMethodsOldClass()
        }
        return instance
    }



    interface MediaUrl {
        fun sendUploadedMediaUrl(url: String?)
        fun sendUploadedVideoMediaUrl(url: String?)
    }

    fun deleteImageFromFirebaseStorage(
        imageUriArrayList: ArrayList<String?>,
        activity: Activity,
        resources: Resources
    ) {
        for (i in imageUriArrayList.indices) {
            try {
                val firebaseStorage = FirebaseStorage.getInstance()
                val photoRef = firebaseStorage.getReferenceFromUrl(imageUriArrayList[i]!!)
                photoRef.delete().addOnSuccessListener { activity.finish() }
                    .addOnFailureListener { // Uh-oh, an error occurred!
                        //dialog.dismiss();
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.toast_please_try_again_later),
                            Toast.LENGTH_SHORT
                        ).show();
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addVideoToFirebase(
        context: Context,
        mediaUriList: Uri?,
        userId: String,
        dialog: Dialog,
        resources: Resources,
        mediaUrl: MediaUrl
    ): String? {
        val urlString = arrayOfNulls<String>(1)
        val fileReference = mStorageReference.child(
            userId + "/videos/" + UUID.randomUUID().toString() + "." + getFileExtension(
                context,
                mediaUriList
            )
        )
        fileReference.putFile(mediaUriList!!)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = fileReference.downloadUrl
                    urlString[0] = uri.toString()
                    val imageUploadModel = ImageUploadModel(uri.toString())
                    mediaUrl.sendUploadedVideoMediaUrl(urlString[0])
                }
            }
            .addOnFailureListener { //Toaster.show(e.getMessage());
                Toast.makeText(
                    context,
                    resources.getString(R.string.toast_Failed_upload_Image),
                    Toast.LENGTH_SHORT
                ).show();

                mediaUrl.sendUploadedVideoMediaUrl("")
                //count[0]++;
                dialog.dismiss()
            }
            .addOnProgressListener { snapshot ->
                val progress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount
                dialog.show()
            }
        return urlString[0]
    }


    fun addImagesToFirebase(
        context: Context,
        imageUri: Uri?,
        userId: String,
        dialog: Dialog,
        resources: Resources,
        mediaUrl: MediaUrl
    ) {
        val imageUrl = arrayOfNulls<String>(1)
        val fileReference = mStorageReference.child(
            userId + "/images/" + UUID.randomUUID().toString() + "." + getFileExtension(
                context,
                imageUri
            )
        )
        var bmp: Bitmap? = null
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val baos = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val data = baos.toByteArray()
        fileReference.putBytes(data)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = fileReference.downloadUrl
                    imageUrl[0] = uri.toString()
                    val imageUploadModel = ImageUploadModel(uri.toString())
                    Log.d("TAG", "onSuccess: imageUploadModel: uri: $uri")
                    mediaUrl.sendUploadedMediaUrl(imageUrl[0])
                }
            }
            .addOnFailureListener { //Toaster.show(e.getMessage());
                Toast.makeText(
                    context,
                    resources.getString(R.string.toast_Failed_upload_Image),
                    Toast.LENGTH_SHORT
                ).show();
                imageUrl[0] = null
                mediaUrl.sendUploadedMediaUrl("")
            }
            .addOnProgressListener { snapshot ->
                val progress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount
                dialog.show()
            }
    }

    fun getFileExtension(context: Context, uri: Uri?): String? {
        val contentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


    /*public static void socialRegisterLogin(String socialId, GoogleSignInAccount account, Context context, ProgressBar progressBar,String name,String email,Resources resources) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("social_token", socialId);

        Controller.socialRegisterLogin(hashMap, new Reception((data, tag) -> {
            if (tag == TAGS.SOCIAL_REGISTRATION_ID) {
                Log.d(TAG, "getSubCategories: response true if for test");
                if (data == null) {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, RegistrationActivity.class);
                    if (account != null) {
                        intent.putExtra("email", account.getEmail());
                        intent.putExtra("name", account.getDisplayName());
                    } else {
                        //ActivityHelper.goToActivity(LoginActivity.this, UserRegistrationActivity.class, false, "social_token", socialId);
                        intent.putExtra("email", email);
                        intent.putExtra("name", name);
                    }
                    intent.putExtra("social_token", socialId);
                    context.startActivity(intent);
                } else {
                    progressBar.setVisibility(View.GONE);
                    RegistrationResponseData registrationResponseData = (RegistrationResponseData) data;
                    BaseResponse baseResponse = (BaseResponse) data;
                    if (registrationResponseData != null) {
                        if (registrationResponseData.getStatusCode() == 200) {
                            setPreferencesValues(registrationResponseData);
                            Log.d(TAG, "socialRegisterLogin: token" + SharedPreferencesHelper.getSharedPreferencesString(KEY_TOKEN, "non"));
                            goToActivity(context, HomeActivity.class, false);
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.d(TAG, "login activity: registrationResponseData: getStatusCode: " + registrationResponseData.getStatusCode());
                        Log.d(TAG, "login activity: baseResponse: getStatusCode: " + baseResponse.getStatusCode());
                    }
                }
            } else {
                Toaster.show(resources.getString(R.string.toast_please_try_again_later));
                progressBar.setVisibility(View.GONE);
            }
        }));
    }*/

    /*public static void socialRegisterLogin(String socialId, GoogleSignInAccount account, Context context, ProgressBar progressBar,String name,String email,Resources resources) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("social_token", socialId);

        Controller.socialRegisterLogin(hashMap, new Reception((data, tag) -> {
            if (tag == TAGS.SOCIAL_REGISTRATION_ID) {
                Log.d(TAG, "getSubCategories: response true if for test");
                if (data == null) {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, RegistrationActivity.class);
                    if (account != null) {
                        intent.putExtra("email", account.getEmail());
                        intent.putExtra("name", account.getDisplayName());
                    } else {
                        //ActivityHelper.goToActivity(LoginActivity.this, UserRegistrationActivity.class, false, "social_token", socialId);
                        intent.putExtra("email", email);
                        intent.putExtra("name", name);
                    }
                    intent.putExtra("social_token", socialId);
                    context.startActivity(intent);
                } else {
                    progressBar.setVisibility(View.GONE);
                    RegistrationResponseData registrationResponseData = (RegistrationResponseData) data;
                    BaseResponse baseResponse = (BaseResponse) data;
                    if (registrationResponseData != null) {
                        if (registrationResponseData.getStatusCode() == 200) {
                            setPreferencesValues(registrationResponseData);
                            Log.d(TAG, "socialRegisterLogin: token" + SharedPreferencesHelper.getSharedPreferencesString(KEY_TOKEN, "non"));
                            goToActivity(context, HomeActivity.class, false);
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.d(TAG, "login activity: registrationResponseData: getStatusCode: " + registrationResponseData.getStatusCode());
                        Log.d(TAG, "login activity: baseResponse: getStatusCode: " + baseResponse.getStatusCode());
                    }
                }
            } else {
                Toaster.show(resources.getString(R.string.toast_please_try_again_later));
                progressBar.setVisibility(View.GONE);
            }
        }));
    }*/
    fun askGuestLogin(resources: Resources, context: Context?) {
        Toast.makeText(
            context,
            resources.getString(R.string.toast_login_with_account),
            Toast.LENGTH_SHORT
        ).show();
        ActivityHelper.goToActivity(context, LoginActivity::class.java, false)
    }

    fun openMediaOptions(context: Context?) {}

    fun getCoordinatesAddressName(context: Context?, latLng: LatLng): String? {
        var selectedAddress = ""
        var selectedCityName = ""
        var address = ""
        val geocoder = Geocoder(context!!, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            address = addresses!![0].getAddressLine(0)
            selectedAddress = addresses[0].thoroughfare
            selectedCityName = addresses[0].adminArea
            Log.d("TAG", "getCoordinatesAddressName: addresses.toString: $addresses")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return address
    }


}