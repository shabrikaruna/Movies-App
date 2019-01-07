package com.example.android.movie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.movie.database.AppDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;

public class MovieListActivity extends AppCompatActivity {

    public final static String MOVIE_KEY = "movie_key";
    public final static String MOVIE_KEY_INTENT = "movie_key_intent";
    public static final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w500";
    public static final String TMDB_IMAGE_PATH_BACKDROP = "http://image.tmdb.org/t/p/w780";
    private static final int NUMBER_OF_COLUMNS = 2;
    public final static String API_KEY = "d557ce13bede53617fbb431b9de5791e";
    public static int id = R.id.popular;
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(MovieListActivity.this, NUMBER_OF_COLUMNS);
    private ProgressBar progressBar;
    private int page = 1;
    private boolean mTwoPane;
    private AppDatabase mDb;
    private RecyclerView recyclerView;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private ImageView mNoInternetImageView;
    private SwipeRefreshLayout refreshPage;
    private boolean doNotChangeThePage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle(R.string.popular);
        mNoInternetImageView = findViewById(R.id.no_internet_image);

        mNoInternetImageView.setVisibility(View.INVISIBLE);
        refreshPage = findViewById(R.id.pullToRefresh);

        refreshPage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getMovies();
                refreshPage.setRefreshing(false);
            }
        });

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }

        mDb = AppDatabase.getInstance(this.getApplication());
        recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        getMovies();

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getMovies();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleItemRecyclerViewAdapter(this, mTwoPane);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void getMovies() {
        switch (id) {
            case R.id.popular:
                getPopularMovies();
                break;
            case R.id.toprated:
                getTopRatedMovies();
                break;
            case R.id.favorites:
                getFavouriteMovies();
                break;
        }
    }

    private void getFavouriteMovies() {
        mAdapter.clearData();
        List<Movie> favorites = mDb.movieDao().getFavourites();
        mAdapter.setMovieList(favorites);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getPopularMovies() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = apiService.getPopularMovies(API_KEY, page++);
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {

            @Override
            public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                List<Movie> list = response.body().getResults();
                mNoInternetImageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                mAdapter.setMovieList(list);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                mNoInternetImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getTopRatedMovies() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY, page++);
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                List<Movie> list = response.body().getResults();
                mNoInternetImageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                mAdapter.setMovieList(list);
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                mNoInternetImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filteritems, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        id = item.getItemId();
        switch (id) {
            case R.id.toprated:
                page = 1;
                getMovies();
                progressBar.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(R.string.top_rated);
                mAdapter.clearData();
                return true;
            case R.id.popular:
                page = 1;
                getMovies();
                progressBar.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(R.string.popular);
                mAdapter.clearData();
                return true;
            case R.id.favorites:
                mAdapter.clearData();
                progressBar.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(R.string.favorites);
                getMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final MovieListActivity mParentActivity;
        private final boolean mTwoPane;
        private List<Movie> mMovieList;
        private LayoutInflater mInflater;


        SimpleItemRecyclerViewAdapter(MovieListActivity parent, boolean twoPane) {
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        public void clearData() {
            if (mMovieList != null) {
                mMovieList.clear();
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            Movie movie = mMovieList.get(position);
            Picasso.with(mParentActivity)
                    .load(TMDB_IMAGE_PATH + movie.getPoster())
                    .placeholder(R.drawable.ic_movie_poster_new_1)
                    .fit()
                    .into(holder.imageView);

            if (mTwoPane && doNotChangeThePage) {
                doNotChangeThePage = false;
                movie = mMovieList.get(0);
                Bundle arguments = new Bundle();
                arguments.putParcelable(MOVIE_KEY, movie);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            }

            if (id == R.id.favorites) {
                getSupportActionBar().setTitle(R.string.favorites);
            } else if (MovieListActivity.id == R.id.popular) {
                getSupportActionBar().setTitle(R.string.popular);
            } else {
                getSupportActionBar().setTitle(R.string.top_rated);
            }

            final View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Movie movie = mMovieList.get(position);
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(MOVIE_KEY, movie);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = view.getContext();
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(MOVIE_KEY, movie);
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MOVIE_KEY_INTENT, arguments);
                        context.startActivity(intent);
                    }
                }
            };
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        void setMovieList(List<Movie> movieList) {
            if (mMovieList == null) {
                mMovieList = movieList;
                notifyDataSetChanged();
            } else {
                mMovieList.addAll(movieList);
                notifyItemRangeInserted(mMovieList.size() - movieList.size(), mMovieList.size());
            }
        }

        @Override
        public int getItemCount() {
            return mMovieList == null ? 0 : mMovieList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;

            ViewHolder(View view) {
                super(view);
                imageView = itemView.findViewById(R.id.imageView);
            }

        }
    }
}
