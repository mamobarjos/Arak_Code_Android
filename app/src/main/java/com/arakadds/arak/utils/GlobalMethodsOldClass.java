package com.arakadds.arak.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.ActivityHelper;
import com.arakadds.arak.model.ImageUploadModel;
import com.arakadds.arak.presentation.activities.authentication.LoginActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GlobalMethodsOldClass {
    private static final String TAG = "GlobalMethods";
    private static GlobalMethodsOldClass instance;
    private static StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    public static GlobalMethodsOldClass getInstance() {
        if (instance == null) {
            instance = new GlobalMethodsOldClass();
        }
        return instance;
    }


    public interface MediaUrl{
        void sendUploadedMediaUrl(String url);
        void  sendUploadedVideoMediaUrl(String url);
    }

    public static void deleteImageFromFirebaseStorage(ArrayList<String> imageUriArrayList, Activity activity, Resources resources) {
        for (int i = 0; i < imageUriArrayList.size(); i++) {
            try {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference photoRef = firebaseStorage.getReferenceFromUrl(imageUriArrayList.get(i));
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activity.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        //dialog.dismiss();
                        Toast.makeText(activity,resources.getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String addVideoToFirebase(Context context, Uri mediaUriList, String userId, Dialog dialog, Resources resources,MediaUrl mediaUrl) {
        final String[] urlString = new String[1];
        StorageReference fileReference = mStorageReference.child(userId + "/videos/" + UUID.randomUUID().toString() + "." + getFileExtension(context, mediaUriList));

        fileReference.putFile(mediaUriList)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Task<Uri> downloadUrl = fileReference.getDownloadUrl();
                        urlString[0] = String.valueOf(uri);
                        ImageUploadModel imageUploadModel = new ImageUploadModel(String.valueOf(uri));
                        mediaUrl.sendUploadedVideoMediaUrl(urlString[0]);
                    }
                }))
                .addOnFailureListener(e -> {
                    //Toaster.show(e.getMessage());
                    Toast.makeText(context,resources.getString(R.string.toast_Failed_upload_Image), Toast.LENGTH_SHORT).show();
                    mediaUrl.sendUploadedVideoMediaUrl("");
                    //count[0]++;
                    dialog.dismiss();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    dialog.show();
                });
        return urlString[0];
    }


    public static void addImagesToFirebase(Context context, Uri imageUri, String userId, Dialog dialog, Resources resources,MediaUrl mediaUrl) {
        final String[] imageUrl = new String[1];

        StorageReference fileReference = mStorageReference.child(userId + "/images/" + UUID.randomUUID().toString() + "." + getFileExtension(context, imageUri));
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        fileReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Task<Uri> downloadUrl = fileReference.getDownloadUrl();
                                imageUrl[0] = String.valueOf(uri);
                                ImageUploadModel imageUploadModel = new ImageUploadModel(String.valueOf(uri));
                                Log.d("TAG", "onSuccess: imageUploadModel: uri: " + String.valueOf(uri));
                                mediaUrl.sendUploadedMediaUrl(imageUrl[0]);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toaster.show(e.getMessage());
                        Toast.makeText(context,resources.getString(R.string.toast_Failed_upload_Image), Toast.LENGTH_SHORT).show();
                        imageUrl[0] = null;
                        mediaUrl.sendUploadedMediaUrl("");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                          /*dialog.show();*/
                    }
                });
    }

    public static String getFileExtension(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
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

    public static void askGuestLogin(Resources resources,Context context){
        Toast.makeText(context,resources.getString(R.string.toast_login_with_account), Toast.LENGTH_SHORT).show();
        ActivityHelper.goToActivity(context, LoginActivity.class, false);
    }

    public static void openMediaOptions(Context context){

    }

    public static String getCoordinatesAddressName(Context context, LatLng latLng) {
        String selectedAddress = "";
        String selectedCityName = "";
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            selectedAddress = addresses.get(0).getThoroughfare();
            selectedCityName = addresses.get(0).getAdminArea();
            Log.d("TAG", "getCoordinatesAddressName: addresses.toString: " + addresses.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

}
