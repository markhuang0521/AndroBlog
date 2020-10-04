package com.ming.androblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ming.androblog.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountSettingActivity extends AppCompatActivity {
    private static final int IMAGE_INTENT_CODE = 202;
    private ImageView ivProfileImage;
    private EditText etName;
    private Button btnImage;
    private static final int READ_STORAGE_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String userId;
    private CollectionReference userReference;
    private Uri imageUri;
    private ProgressBar progressBar;
    private ArrayList<String> imageList = new ArrayList<>();


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        firestore = FirebaseFirestore.getInstance();
        userReference = firestore.collection(User.REF_USER);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        ivProfileImage = findViewById(R.id.iv_profile_image);
        progressBar = findViewById(R.id.pb_upload_image);
        etName = findViewById(R.id.et_profile_name);
        btnImage = findViewById(R.id.btn_profile);
        userId = firebaseAuth.getCurrentUser().getUid();
        getUserInfo(userId);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePermission();
            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = etName.getText().toString();
                if (!TextUtils.isEmpty(userName) && imageUri != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadUserInfo(userName, imageUri);
                } else {
                    Toast.makeText(AccountSettingActivity.this, "image or name cant be empty", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        etName.setVisibility(View.GONE);
        ivProfileImage.setVisibility(View.GONE);
        btnImage.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        etName.setVisibility(View.VISIBLE);
        ivProfileImage.setVisibility(View.VISIBLE);
        btnImage.setVisibility(View.VISIBLE);

    }

    private void getUserInfo(String userId) {
        showProgressBar();
        userReference.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {
                        String username = task.getResult().getString(User.COL_USER_NAME);
                        String userImage = task.getResult().getString(User.COL_USER_IMAGE);
                        etName.setText(username);
                        Glide.with(AccountSettingActivity.this).asBitmap().load(userImage).into(ivProfileImage);
                        hideProgressBar();

                    } else {
                        Toast.makeText(AccountSettingActivity.this, "please upload your name and image", Toast.LENGTH_SHORT).show();
                        hideProgressBar();

                    }

                } else {
                    Toast.makeText(AccountSettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void handlePermission() {
        if (ContextCompat.checkSelfPermission(
                AccountSettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showSnackBar();
            } else {
                ActivityCompat.requestPermissions(AccountSettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_CODE);

            }
        } else {
            openPhoto();
        }
    }

    private void showSnackBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("we need this permission ")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(AccountSettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_CODE);

                    }
                })
                .setNegativeButton("no", null);
        builder.create().show();
    }

    private void openPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_INTENT_CODE);
    }

    private void uploadUserInfo(final String userName, Uri imageUri) {
        final StorageReference imagePath = storageReference.child("profile_image").child(userId + "_" + Timestamp.now().getSeconds() + ".jpg");

        imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, Object> userMap = new User(userId, userName, uri.toString()).toMap();
                        userReference.document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(userName).build();
                                    currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                startActivity(new Intent(AccountSettingActivity.this, MainActivity.class));
                                            else {
                                                Toast.makeText(AccountSettingActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });
                                } else {
                                    Toast.makeText(AccountSettingActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountSettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_INTENT_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData();
                    Glide.with(AccountSettingActivity.this)
                            .asBitmap().load(imageUri).circleCrop().centerCrop().into(ivProfileImage);
                }
                break;
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
//        userReference.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().exists()) {
//                        String username = task.getResult().getString(User.COL_USER_NAME);
//                        String userImage = task.getResult().getString(User.COL_USER_IMAGE);
//                        etName.setText(username);
//                        Glide.with(AccountSettingActivity.this).asBitmap().load(userImage).into(ivProfileImage);
//
//
//                    } else {
//                        Toast.makeText(AccountSettingActivity.this, "please upload your name and image", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                } else {
//                    Toast.makeText(AccountSettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });
//
//
    }
}
