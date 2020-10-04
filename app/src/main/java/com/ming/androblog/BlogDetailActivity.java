package com.ming.androblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ming.androblog.models.Blog;
import com.ming.androblog.utils.BlogViewModel;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Map;


public class BlogDetailActivity extends AppCompatActivity {
    private static final int IMAGE_INTENT_CODE = 11;
    private static final String TAG = "BlogDetailActivity";
    private ImageView ivBlogImage, btnAddImage;
    private EditText etDetail;
    private TextView tvCreatedDate;
    Button btnSaveBlog, btnDeleteBlog;
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private CollectionReference blogReference;
    private Uri imageUri;
    private String userId;
    private String currentDate;
    private Blog currentBlog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        initViews();

        currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
        tvCreatedDate.setText(MessageFormat.format("Date: {0}", currentDate));
        if (getIntent().hasExtra(Blog.REF_BLOG)) {
            btnDeleteBlog.setVisibility(View.VISIBLE);
            currentBlog = getIntent().getParcelableExtra(Blog.REF_BLOG);
            Glide.with(BlogDetailActivity.this)
                    .asBitmap().load(currentBlog.getImageUrl()).centerCrop().into(ivBlogImage);
            etDetail.setText(currentBlog.getDetail());
            tvCreatedDate.setText("Post Date: " + currentBlog.getDateCreated());

        }


        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();

            }
        });
        btnSaveBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null && etDetail.getText() != null)
                    uploadBlog();
                else {
                    Toast.makeText(BlogDetailActivity.this, "image or detail cant be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnDeleteBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlogViewModel viewModel = ViewModelProviders.of(BlogDetailActivity.this).get(BlogViewModel.class);
                viewModel.deleteBlogToFirebase(currentBlog.getBlogId());
                startActivity(new Intent(BlogDetailActivity.this, MainActivity.class));

            }
        });


    }

    private void initViews() {

        ivBlogImage = findViewById(R.id.iv_journal_bg);
        btnAddImage = findViewById(R.id.iv_journal_add_image);
        etDetail = findViewById(R.id.et_journal_desc);
        tvCreatedDate = findViewById(R.id.tv_journal_date);
        progressBar = findViewById(R.id.pb_save_blog);
        btnSaveBlog = findViewById(R.id.btn_save_blog);
        btnDeleteBlog = findViewById(R.id.btn_delete_blog);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        blogReference = firestore.collection(Blog.REF_BLOG);
    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_INTENT_CODE);
    }

    private void uploadBlog() {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference imagePath = storageReference.child("blog_image").child(userId + "_" + Timestamp.now().getSeconds() + ".jpg");
        imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DocumentReference doc = blogReference.document();
                        String id = doc.getId();
                        Map<String, Object> blogMap = new Blog(id, uri.toString(), etDetail.getText().toString(), currentUser.getDisplayName(), userId, currentDate).toMap();
                        blogReference.document(id).set(blogMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(BlogDetailActivity.this, MainActivity.class));
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(BlogDetailActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


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
                    Glide.with(BlogDetailActivity.this)
                            .asBitmap().load(imageUri).centerCrop().into(ivBlogImage);
                }
                break;
        }
    }
}
