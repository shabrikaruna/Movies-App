package com.example.android.movie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.movie.database.AppDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;
    private AppDatabase mDb;
    private static final int IS_ID_PRESENT_IN_DATABASE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        mDb = AppDatabase.getInstance(getApplicationContext());
        final FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments = getIntent().getBundleExtra(MovieListActivity.MOVIE_KEY_INTENT);

            ImageView imageView = findViewById(R.id.image_view_backdrop);
            mMovie = arguments.getParcelable(MovieListActivity.MOVIE_KEY);
            Picasso.with(MovieDetailActivity.this)
                    .load(MovieListActivity.TMDB_IMAGE_PATH_BACKDROP + mMovie.getBackdrop())
                    .placeholder(R.drawable.ic_movie_poster_landscape)
                    .fit()
                    .into(imageView);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();

            if (isAFavoriteMovie()) {
                myFab.setImageDrawable(ContextCompat.getDrawable(MovieDetailActivity.this, R.drawable.ic_like));
            }
            getSupportActionBar().setTitle(mMovie.getTitle());
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (insertAndDeleteFavouriteMovie()) {
                        myFab.setImageDrawable(ContextCompat.getDrawable(MovieDetailActivity.this, R.drawable.ic_like_white));
                    } else {
                        myFab.setImageDrawable(ContextCompat.getDrawable(MovieDetailActivity.this, R.drawable.ic_like));
                    }
                }
            });
        }
    }

    private boolean isAFavoriteMovie() {
        return mDb.movieDao().loadAllFavourites(mMovie.getId()) == IS_ID_PRESENT_IN_DATABASE;
    }

    public boolean insertAndDeleteFavouriteMovie() {
        if (isAFavoriteMovie()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteMovie(mMovie);
                }
            });
            return true;
        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insertMovie(mMovie);
                }
            });
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
