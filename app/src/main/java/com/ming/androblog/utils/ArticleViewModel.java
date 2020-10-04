package com.ming.androblog.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ming.androblog.models.Article;
import com.ming.androblog.models.Blog;

import java.util.List;
import java.util.Map;

public class ArticleViewModel extends ViewModel implements ArticleRepo.FireStoreNewsComplete {
    private MutableLiveData<List<Article>> articleViewModel = new MutableLiveData<>();

    private ArticleRepo articleRepo = new ArticleRepo(this);

    public LiveData<List<Article>> getArticleByUserId(String userId) {
        articleRepo.getArticlesByUserId(userId);
        return articleViewModel;

    }

    public void addarticleToFirebase(Map<String, Object> article) {
        articleRepo.addArticle(article);
    }


    @Override
    public void articleStored(List<Article> articles) {
        articleViewModel.setValue(articles);

    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void queryArticles(List<Blog> blogs) {

    }
}
