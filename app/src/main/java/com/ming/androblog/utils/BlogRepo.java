package com.ming.androblog.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ming.androblog.models.Blog;

import java.util.ArrayList;
import java.util.List;

public class BlogRepo {
    private FireStoreTaskComplete fireStoreTaskComplete;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference blogRef = firestore.collection(Blog.REF_BLOG);


    public BlogRepo(FireStoreTaskComplete fireStoreTaskComplete) {
        this.fireStoreTaskComplete = fireStoreTaskComplete;


    }

    public void getBlogsByQuery(String userId, String keyword) {
        blogRef.whereEqualTo("userId", userId)
                .whereEqualTo("detail", keyword)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                fireStoreTaskComplete.queryBlogsStored(task.getResult().toObjects(Blog.class));
            }
        });
    }


    // getting blogs from userid using interface to carry over the list
    public void getAllBlogByUserId(String userId) {

        blogRef.whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    fireStoreTaskComplete.blogListStored(task.getResult().toObjects(Blog.class));

                } else {
                    fireStoreTaskComplete.onError(task.getException());
                }

            }
        });
    }


    public void addBlog(Blog blog) {
        blogRef.add(blog);
    }

    public void deleteBlog(String id) {
        blogRef.document(id).delete();
    }

    public interface FireStoreTaskComplete {
        void blogListStored(List<Blog> blogs);

        void onError(Exception exception);

        void queryBlogsStored(List<Blog> blogs);
    }
}
