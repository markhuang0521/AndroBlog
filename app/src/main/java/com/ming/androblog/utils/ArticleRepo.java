package com.ming.androblog.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ming.androblog.models.Article;
import com.ming.androblog.models.Blog;

import java.util.List;
import java.util.Map;

public class ArticleRepo {
    private FireStoreNewsComplete fireStoreNewsComplete;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference newsRef = firestore.collection(Article.REF_ARTICLE);

    public ArticleRepo(FireStoreNewsComplete fireStoreNewsComplete) {
        this.fireStoreNewsComplete = fireStoreNewsComplete;
    }

    public void addArticle(Map<String, Object> article) {
        newsRef.add(article);
    }

    public void deleteArticle(String id) {
        newsRef.document(id).delete();
    }

    public void getArticleByKeyword(String userId, String keyword) {
        newsRef.whereEqualTo("userId", userId)
                .whereArrayContains("title", keyword)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                fireStoreNewsComplete.queryArticles(task.getResult().toObjects(Blog.class));
            }
        });
    }

    public void getArticlesByUserId(String userId) {
        newsRef.whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    fireStoreNewsComplete.articleStored(task.getResult().toObjects(Article.class));

                } else {
                    fireStoreNewsComplete.onError(task.getException());
                }

            }
        });

    }


    public interface FireStoreNewsComplete {
        void articleStored(List<Article> articles);

        void onError(Exception exception);

        void queryArticles(List<Blog> blogs);

    }
}
