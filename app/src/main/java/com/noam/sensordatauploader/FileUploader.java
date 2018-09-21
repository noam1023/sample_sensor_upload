package com.noam.sensordatauploader;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


/** upload a file using Firebase to google cloud
 *
 */
public class FileUploader {

    public static String TAG = "nwm-fileuploader";
    private FirebaseStorage storage;
    private StorageReference storageReference;


    public FileUploader(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void uploadFile(File file) {
        StorageReference ref = storageReference.child(file.getName());
        Uri uri = Uri.fromFile(file);
        Log.d(TAG, "Going to upload " + file.getName());
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Log.i(TAG,"upload ok");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "upload failed: " + e.toString() );
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        Log.d(TAG,"progress...");
                    }
                });

    }
}
