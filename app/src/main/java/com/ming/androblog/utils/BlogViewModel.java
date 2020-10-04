package com.ming.androblog.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ming.androblog.models.Blog;

import java.util.List;

public class BlogViewModel extends ViewModel implements BlogRepo.FireStoreTaskComplete {
    private BlogRepo blogRepo = new BlogRepo(this);
    private MutableLiveData<List<Blog>> blogViewModel = new MutableLiveData<>();
    private MutableLiveData<List<Blog>> queryBlogViewModel = new MutableLiveData<>();


    public LiveData<List<Blog>> getBlogById(String userId) {
        blogRepo.getAllBlogByUserId(userId);
        return blogViewModel;
    }

    public LiveData<List<Blog>> getBlogByquery(String userId, String query) {
        blogRepo.getBlogsByQuery(userId, query);
        return queryBlogViewModel;
    }

    public void addBlogToFirebase(Blog blog) {
        blogRepo.addBlog(blog);
    }

    public void deleteBlogToFirebase(String id) {
        blogRepo.deleteBlog(id);
    }


    // callback from query function in firebase

    @Override
    public void blogListStored(List<Blog> blogs) {
        blogViewModel.setValue(blogs);

    }

    // callback from query function in firebase
    @Override
    public void queryBlogsStored(List<Blog> blogs) {
        queryBlogViewModel.setValue(null);
        queryBlogViewModel.setValue(blogs);

    }

    @Override
    public void onError(Exception exception) {

    }
}
