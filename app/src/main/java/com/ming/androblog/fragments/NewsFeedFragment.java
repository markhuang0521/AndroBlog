package com.ming.androblog.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.ming.androblog.ArticleAdapter;
import com.ming.androblog.BlogDetailActivity;
import com.ming.androblog.FavArticleActivity;
import com.ming.androblog.NewsDetailActivity;
import com.ming.androblog.R;
import com.ming.androblog.api.ApiClient;
import com.ming.androblog.api.NewsApiInterface;
import com.ming.androblog.models.Article;
import com.ming.androblog.models.Blog;
import com.ming.androblog.models.News;
import com.ming.androblog.utils.NewsUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedFragment extends Fragment implements ArticleAdapter.ItemOnClicker {
    private ArticleAdapter articleAdapter;
    private RecyclerView newsRecycler;
    private List<Article> articleList = new ArrayList<>();
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorPageLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage;
    private Button errorButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Add this! (as above)
        return inflater.inflate(R.layout.activity_news_feed, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar_news);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_search:
                        SearchView searchView = (SearchView) item.getActionView();
                        searchView.setQueryHint("Search News....");

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                if (query.length() > 0) {
                                    loadNewsJson(query);
                                }
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                loadNewsJson(newText);
                                return false;
                            }
                        });
                        break;
                    case R.id.menu_show_fav_articles:
                        startActivity(new Intent(getActivity(), FavArticleActivity.class));

                    default:
                        break;
                }
                return false;
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.layout_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNewsJson("");

            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        newsRecycler = view.findViewById(R.id.recycler_news);
        articleAdapter = new ArticleAdapter(getActivity(), this);
        newsRecycler.setAdapter(articleAdapter);
        newsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsRecycler.setHasFixedSize(true);

        // error page loadout
        errorPageLayout = view.findViewById(R.id.layout_error_page);
        errorButton = view.findViewById(R.id.btn_error_retry);
        errorImage = view.findViewById(R.id.iv_error_image);
        errorTitle = view.findViewById(R.id.tv_error_title);
        errorMessage = view.findViewById(R.id.tv_error_message);
        onLoadingRefresh("");


    }


    private void loadNewsJson(final String keyword) {
        errorPageLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        articleList.clear();
        NewsApiInterface newsApi = ApiClient.getRetrofit().create(NewsApiInterface.class);
        String apiKey = getActivity().getString(R.string.news_api_key);
        String country = NewsUtil.getCountry();
        String language = NewsUtil.getLanguage();
        Call<News> newsCall;
        if (keyword.isEmpty()) {
            newsCall = newsApi.getNews(country, apiKey);
        } else {
            newsCall = newsApi.getNewsByKeyword(keyword, language, "publishedAt", apiKey);
        }
        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {
                    swipeRefreshLayout.setRefreshing(true);

                    Toast.makeText(getActivity(), "it works", Toast.LENGTH_SHORT).show();

                    articleList = response.body().getArticles();
                    articleAdapter.setArticleList(articleList);
                    articleAdapter.hideProgressBar();
                    swipeRefreshLayout.setRefreshing(false);


                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    errorPageLayout.setVisibility(View.VISIBLE);
                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server not found";
                            break;
                        default:
                            errorCode = "unknow error kappa";
                            break;

                    }
                    showErrorPage(errorCode);

                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

                showErrorPage("network failure please use internet" + t.toString());

            }
        });

    }

    private void showErrorPage(String message) {
        if (errorPageLayout.getVisibility() == View.GONE) {
            errorPageLayout.setVisibility(View.VISIBLE);
        }
        Glide.with(getActivity()).load(R.drawable.no_result).centerCrop().into(errorImage);
        errorTitle.setText("no result");
        errorMessage.setText(message);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewsJson("");

            }
        });

    }


    @Override
    public void onClick(Article article, View view) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Article.INTENT_ARTICLE, article);
        startActivity(intent);

    }

    private void onLoadingRefresh(final String keyword) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadNewsJson(keyword);

            }
        });

    }



    //    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_main, menu);
//        MenuItem item = menu.findItem(R.id.menu_search);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        SearchView searchView = (SearchView) item.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (query.length() > 2) {
//                    loadNewsJson(query);
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                loadNewsJson(newText);
//                return true;
//            }
//
//        });
//    }

}
