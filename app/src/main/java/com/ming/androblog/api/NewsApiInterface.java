package com.ming.androblog.api;

import com.ming.androblog.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiInterface {

    @GET("top-headlines")
    Call<News> getNews(
            @Query("country") String country,
            @Query("apiKey") String apiKey);


    @GET("everything")
    Call<News> getNewsByKeyword(
            @Query("qInTitle") String keyword,
            @Query("language") String language,
            @Query("sortBy") String sort,

            @Query("apiKey") String apiKey
    );


}
