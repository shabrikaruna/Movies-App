package com.example.android.movie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.android.movie.database.AppDatabase;
import com.example.android.movie.database.MovieVideoKey;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    Movie mMovie;
    private AppDatabase mDb;
    private static final int IS_ID_PRESENT_IN_DATABASE = 1;
    List<Result> mMovieVideoKey;
    private String video_key;

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
        getVideoKey();
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

    private void getVideoKey() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieVideoKey> call = apiService.getMovieVideo(mMovie.getId(), MovieListActivity.API_KEY);
        call.enqueue(new Callback<MovieVideoKey>() {
            @Override
            public void onResponse(Call<MovieVideoKey> call, Response<MovieVideoKey> response) {
                mMovieVideoKey = response.body().getResults();
                video_key = mMovieVideoKey.get(0).getKey();
            }

            @Override
            public void onFailure(Call<MovieVideoKey> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sharemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if(id == R.id.menu_item_share){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "sharing trialer of " + mMovie.getTitle() + "\n" + MovieTrailerAdapter.YOUTUBE_BASE_URL + video_key;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "video_url");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
