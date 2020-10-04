package com.ming.androblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ming.androblog.fragments.HomeFragment;
import com.ming.androblog.fragments.NewsFeedFragment;
import com.ming.androblog.models.Blog;
import com.ming.androblog.models.User;
import com.ming.androblog.utils.BlogViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private CollectionReference userReference;

    private RecyclerView recyclerBlog;
    private BlogViewModel blogViewModel;
    private BlogAdapter adapter;
    private TextView tvEmptyBlog;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userReference = firestore.collection(User.REF_USER);
        initBottomNav();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
        fragmentTransaction.commit();

//        if (currentUser != null) {
//            blogViewModel = ViewModelProviders.of(MainActivity.this).get(BlogViewModel.class);
//            blogViewModel.getBlogById(currentUser.getUid()).observe(MainActivity.this, new Observer<List<Blog>>() {
//                @Override
//                public void onChanged(List<Blog> blogs) {
//                    if (blogs.size() > 0) {
//                        adapter.setBlogList(blogs);
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        tvEmptyBlog.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//        }


    }

    private void initView() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userReference = firestore.collection(User.REF_USER);
        currentUser = firebaseAuth.getCurrentUser();
//        recyclerBlog = findViewById(R.id.recycler_blog);
//        tvEmptyBlog = findViewById(R.id.tv_empty_blog);
//        adapter = new BlogAdapter(this, this);
//        recyclerBlog.setLayoutManager(new LinearLayoutManager(this));
//        recyclerBlog.setAdapter(adapter);
//        recyclerBlog.setHasFixedSize(true);
    }

    private void initBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_nav_main);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.menu_news:

                        replaceFragment(new NewsFeedFragment());

                        break;
                    case R.id.menu_setting:

                        startActivity(new Intent(MainActivity.this, AccountSettingActivity.class));
                        break;
                    default:
                        replaceFragment(new HomeFragment());
                        break;

                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        } else {
            String userId = currentUser.getUid();
            userReference.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            startActivity(new Intent(MainActivity.this, AccountSettingActivity.class));
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                break;
            case R.id.menu_add_blog:
                startActivity(new Intent(MainActivity.this, BlogDetailActivity.class));
                break;
            case R.id.menu_logout:
                logout();
                break;
        }
        return true;

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

}
