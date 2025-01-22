package com.arakadds.arak.common.helper;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.arakadds.arak.R;
import com.arakadds.arak.model.ImageUploadModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class TaskHelper {

    private static final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();//we use this to get reference to our images in firebase storage

    public interface TaskHelperCallBack {
        void onImageUploadedSuccessfullyEvent(Uri uri) throws IOException;

        void onImageUploadedFailureEvent(Exception e);
    }

    public static void updateImageToFireBase(Context context,String userId, Uri imageUri, String direction, ProgressDialog dialog, Resources resources, TaskHelperCallBack taskHelperCallBack) {
        dialog.show();

        StorageReference fileReference = mStorageReference.child(userId + "/" + direction + "/" + UUID.randomUUID().toString() + "." + getFileExtension(imageUri, context));
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Task<Uri> downloadUrl = fileReference.getDownloadUrl();
                    //urlArrayList.add(String.valueOf(uri));
                    ImageUploadModel imageUploadModel = new ImageUploadModel(String.valueOf(uri));
                    String uploadId = databaseReference.push().getKey();
                    //databaseReference.child(userId).child("Products").setValue(imageUploadModel);
                    //editUserProfileImage(uri);
                    //setImages();
                    try {
                        taskHelperCallBack.onImageUploadedSuccessfullyEvent(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //editUserImage(uri);
                }))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, resources.getString(R.string.toast_please_try_again_later), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //dialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    }
                });
    }

    private static String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
