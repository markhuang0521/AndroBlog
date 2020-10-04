package com.ming.androblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ming.androblog.models.Blog;
import com.ming.androblog.models.User;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Context context;
    private List<Blog> blogList;
    private OnBlogItemClicker onBlogItemClicker;

    public BlogAdapter(Context context, OnBlogItemClicker onBlogItemClicker) {
        this.context = context;
        this.onBlogItemClicker = onBlogItemClicker;
    }

    public void setBlogList(List<Blog> blogList) {
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Blog blog = blogList.get(position);
        holder.postDate.setText(blog.getDateCreated());
        holder.username.setText(blog.getUsername());
        holder.blogDetail.setText(blog.getDetail());
        Glide.with(context).asBitmap().load(blog.getImageUrl()).centerCrop().into(holder.blogImage);

        firestore.collection(User.REF_USER).document(blog.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String userImage = task.getResult().getString("imageUrl");
                        Glide.with(context).asBitmap().load(blog.getImageUrl()).centerCrop().into(holder.profileImage);

                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return blogList != null ? blogList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImage, blogImage;
        TextView username, blogDetail, postDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.iv_profile_image);
            blogImage = itemView.findViewById(R.id.iv_blog_image);
            username = itemView.findViewById(R.id.tv_name);
            blogDetail = itemView.findViewById(R.id.tv_detail);
            postDate = itemView.findViewById(R.id.tv_time);
            blogImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Blog blog = blogList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.iv_blog_image:
                    onBlogItemClicker.onClick(blog);
                    break;
            }
        }
    }

    public interface OnBlogItemClicker {
        void onClick(Blog blog);
    }
}
