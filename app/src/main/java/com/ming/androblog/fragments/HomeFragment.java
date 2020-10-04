package com.ming.androblog.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ming.androblog.BlogAdapter;
import com.ming.androblog.BlogDetailActivity;
import com.ming.androblog.LoginActivity;
import com.ming.androblog.MainActivity;
import com.ming.androblog.R;
import com.ming.androblog.models.Blog;
import com.ming.androblog.models.User;
import com.ming.androblog.utils.BlogViewModel;

import java.util.List;

public class HomeFragment extends Fragment implements BlogAdapter.OnBlogItemClicker {
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private CollectionReference userReference;
    private ImageView imageView;

    private RecyclerView recyclerBlog;
    private BlogViewModel blogViewModel;
    private BlogAdapter adapter;
    private TextView tvEmptyBlog;
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_home_page, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_search:
                        SearchView searchView = (SearchView) menuItem.getActionView();
                        searchView.setQueryHint("Search News....");

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                if (query.length() > 0) {
                                    loadBlogsByQuery(query);
                                }
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                loadBlogsByQuery(newText);
                                return false;
                            }

                        });
                        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                            @Override
                            public boolean onClose() {
                                loadBlogs();
                                return false;
                            }
                        });

                        break;
                    case R.id.menu_add_blog:
                        startActivity(new Intent(getActivity(), BlogDetailActivity.class));

                        break;
                    case R.id.menu_logout:
                        logout();
                        break;


                }
                return true;
            }
        });
        loadBlogs();

    }

    private void loadBlogs() {
        if (currentUser != null) {
            blogViewModel = ViewModelProviders.of(getActivity()).get(BlogViewModel.class);
            blogViewModel.getBlogById(currentUser.getUid()).observe(getViewLifecycleOwner(), new Observer<List<Blog>>() {
                @Override
                public void onChanged(List<Blog> blogs) {
                    if (blogs.size() > 0) {
                        adapter.setBlogList(blogs);
                        adapter.notifyDataSetChanged();
                    } else {
                        tvEmptyBlog.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void loadBlogsByQuery(String query) {
        if (!query.isEmpty()) {
            BlogViewModel blogViewModel = ViewModelProviders.of(getActivity()).get(BlogViewModel.class);
            blogViewModel.getBlogByquery(currentUser.getUid(), query).observe(getViewLifecycleOwner(), new Observer<List<Blog>>() {
                @Override
                public void onChanged(List<Blog> blogs) {

                    adapter.setBlogList(blogs);
                    adapter.notifyDataSetChanged();

                }
            });
        }


    }


    private void initView(View view) {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userReference = firestore.collection(User.REF_USER);
        currentUser = firebaseAuth.getCurrentUser();
        recyclerBlog = view.findViewById(R.id.recycler_blog);
        tvEmptyBlog = view.findViewById(R.id.tv_empty_blog);
        adapter = new BlogAdapter(getActivity(), this);
        recyclerBlog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerBlog.setAdapter(adapter);
        recyclerBlog.setHasFixedSize(true);
        toolbar = view.findViewById(R.id.toolbar_blog);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                break;
            case R.id.menu_add_blog:
                startActivity(new Intent(getActivity(), BlogDetailActivity.class));
                break;
            case R.id.menu_logout:
                logout();
                break;
        }
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void onClick(Blog blog) {
        Intent intent = new Intent(getActivity(), BlogDetailActivity.class);
        intent.putExtra(Blog.REF_BLOG, blog);
        startActivity(intent);

    }
}
