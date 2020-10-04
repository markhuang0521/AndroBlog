package com.ming.androblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ming.androblog.models.Article;
import com.ming.androblog.utils.ArticleViewModel;
import com.ming.androblog.utils.BlogViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavArticleActivity extends AppCompatActivity implements ArticleAdapter.ItemOnClicker {
    private ArticleAdapter articleAdapter;
    private RecyclerView articleRecyclerView;
    private List<Article> articleList = new ArrayList<>();
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArticleViewModel articleViewModel;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_article);
        toolbar = findViewById(R.id.toolbar_fav);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        articleRecyclerView = findViewById(R.id.recycler_fav_article);
        articleAdapter = new ArticleAdapter(this, this);
        articleRecyclerView.setAdapter(articleAdapter);
        articleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleRecyclerView.setHasFixedSize(true);
        swipeRefreshLayout = findViewById(R.id.layout_swipe_refresh_fav);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        showFavArticles();


    }

    private void showFavArticles() {
        if (currentUser != null) {
            articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
            articleViewModel.getArticleByUserId(currentUser.getUid()).observe(this, new Observer<List<Article>>() {
                @Override
                public void onChanged(List<Article> articles) {
                    if (articles.size() > 0) {
                        articleAdapter.setArticleList(articles);
                        articleAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(FavArticleActivity.this, "no article at this time", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(Article article, View view) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(Article.INTENT_ARTICLE, article);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Search News....");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (query.length() > 0) {
                            loadArticleByKeyword(query);
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        loadArticleByKeyword(newText);
                        return false;
                    }
                });
                break;

        }
        return false;
    }

    private void loadArticleByKeyword(String query) {
        if (!query.isEmpty()){

        }
    }
}
