package com.optic.tribejam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.optic.tribejam.R;
import com.optic.tribejam.adapters.PostsAdapter;
import com.optic.tribejam.models.Post;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.PostProvider;
import com.optic.tribejam.utils.ViewedMessageHelper;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;

    TextView mTextViewNumberFilter;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mRecyclerView = findViewById(R.id.recylerViewFilter);

        //Mostramos las targetas de comentario una debajo de otra
        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this,2));

        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilter);

        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }


    @Override
    public void onStart() {
        super.onStart();
        //Consulta a nuestro Post provider
        Query query = mPostProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        mPostsAdapter = new PostsAdapter(options,FiltersActivity.this,mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
        ViewedMessageHelper.updateOnline(false, FiltersActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, FiltersActivity.this);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

}