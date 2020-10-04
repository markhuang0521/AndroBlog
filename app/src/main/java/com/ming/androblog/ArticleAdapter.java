package com.ming.androblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ming.androblog.models.Article;
import com.ming.androblog.models.NewsSource;
import com.ming.androblog.utils.NewsUtil;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> articleList;
    private Context context;
    private ItemOnClicker onClicker;
    private boolean pbHide = false;

    public ArticleAdapter(Context context, ItemOnClicker onClicker) {
        this.context = context;
        this.onClicker = onClicker;
    }

    public interface ItemOnClicker {
        void onClick(Article article, View view);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);
        NewsSource source = article.getNewsSource();
        holder.author.setText(article.getAuthor());
        holder.title.setText(article.getTitle());
        holder.desc.setText(article.getDescription());
        holder.date.setText(NewsUtil.DateFormat(article.getPublishedAt()));
        if (source != null) {
            holder.source.setText(source.getName());
        } else {
            holder.source.setText("Source not available");

        }
        holder.time.setText(NewsUtil.DateToTimeFormat(article.getPublishedAt()));
        Glide.with(context).asBitmap().load(article.getUrlToImage()).centerCrop().into(holder.newsImage);
        if (pbHide) {
            holder.progressBar.setVisibility(View.GONE);
        }


    }

    public void hideProgressBar() {
        // here set as true
        pbHide = true;
        notifyDataSetChanged();

    }

    public void clear() {
        articleList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        ImageView newsImage;
        TextView title, author, desc, date, source, time;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.pb_news);
            cardView = itemView.findViewById(R.id.cardview_news);
            cardView.setOnClickListener(this);
            newsImage = itemView.findViewById(R.id.iv_news_main);
            title = itemView.findViewById(R.id.tv_news_title);
            author = itemView.findViewById(R.id.tv_author_news);
            desc = itemView.findViewById(R.id.tv_news_desc);
            date = itemView.findViewById(R.id.tv_news_date);
            source = itemView.findViewById(R.id.tv_news_source);
            time = itemView.findViewById(R.id.tv_news_time);
        }

        @Override
        public void onClick(View view) {

            Article article = articleList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.cardview_news:
                    onClicker.onClick(article, view);
            }

        }
    }
}
