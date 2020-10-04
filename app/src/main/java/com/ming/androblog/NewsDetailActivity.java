package com.ming.androblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.ming.androblog.models.Article;
import com.ming.androblog.models.NewsSource;
import com.ming.androblog.utils.ArticleViewModel;
import com.ming.androblog.utils.NewsUtil;

import java.util.Map;

public class NewsDetailActivity extends AppCompatActivity {
    private ImageView detailImage;
    private TextView appbarTitle, appSubTitle, newsTitle, newsSubTitle, date, time;
    private FrameLayout dateLayout;
    private LinearLayout titleAppBar;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private WebView webView;
    private Toolbar toolbar;
    private boolean isHideToolbar = false;
    private Article currentArticle;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference articleReference = firestore.collection(Article.REF_ARTICLE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        initViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float precentage = Math.abs(verticalOffset / (float) maxScroll);
                if (precentage == 1 && isHideToolbar) {
                    dateLayout.setVisibility(View.GONE);
                    titleAppBar.setVisibility(View.VISIBLE);
                    isHideToolbar = !isHideToolbar;
                } else {
                    dateLayout.setVisibility(View.VISIBLE);
                    titleAppBar.setVisibility(View.GONE);
                    isHideToolbar = !isHideToolbar;
                }
            }
        });
        if (getIntent().hasExtra(Article.INTENT_ARTICLE)) {
            currentArticle = getIntent().getParcelableExtra(Article.INTENT_ARTICLE);
            NewsSource source = currentArticle.getNewsSource();
            if (source != null) {
                appbarTitle.setText(currentArticle.getNewsSource().getName());

            }
            appSubTitle.setText(currentArticle.getUrl());
            date.setText(NewsUtil.DateFormat(currentArticle.getPublishedAt()));
            time.setText(NewsUtil.DateToTimeFormat(currentArticle.getPublishedAt()));
            newsTitle.setText(currentArticle.getTitle());
            Glide.with(this).load(currentArticle.getUrlToImage()).transition(DrawableTransitionOptions.withCrossFade()).into(detailImage);
            initWebview(currentArticle.getUrl());
        } else {
            Toast.makeText(this, "article not found", Toast.LENGTH_SHORT).show();
        }


    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        appBarLayout = findViewById(R.id.layout_appbar);
        dateLayout = findViewById(R.id.date_behavior);
        titleAppBar = findViewById(R.id.layout_detail_appbar);
        detailImage = findViewById(R.id.detail_image);
        appbarTitle = findViewById(R.id.tv_detail_source);
        appSubTitle = findViewById(R.id.tv_appbar_subtitle);
        newsTitle = findViewById(R.id.detail_title);
        date = findViewById(R.id.detail_date);
        time = findViewById(R.id.detail_time);
    }

    private void initWebview(String url) {
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_news_share:
                shareArticle();
                break;
            case R.id.menu_view_web:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(currentArticle.getUrl()));
                startActivity(intent);
                break;
            case R.id.menu_favorite:
                addArticleToFav(currentArticle);


        }
        return super.onOptionsItemSelected(item);
    }

    private void addArticleToFav(Article currentArticle) {
        if (currentArticle != null) {
            ArticleViewModel articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);

            Map<String, Object> articleMap = currentArticle.toMap(currentUser.getUid());
            articleViewModel.addarticleToFirebase(articleMap);
            Toast.makeText(this, "add to favorite", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareArticle() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plan");
        intent.putExtra(Intent.EXTRA_SUBJECT, currentArticle.getTitle());
        String body = currentArticle.getTitle() + "\n" + currentArticle.getUrl();
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Share link using"));

    }
}
