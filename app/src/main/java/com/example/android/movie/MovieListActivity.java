package com.example.android.movie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    public static int pageIncrement = 1;
    private boolean mTwoPane;
    private AppDatabase mDb;
    private RecyclerView recyclerView;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private ImageView mNoInternetImageView;
    private SwipeRefreshLayout refreshPage;
    private boolean doNotChangeThePage = true;
    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (savedInstanceState != null) {
            flag = savedInstanceState.getInt("FLAG");
            id = savedInstanceState.getInt("ITEM_NAME");

            if (id == R.id.favorites) {
                getSupportActionBar().setTitle(getResources().getString(R.string.favorites));
            } else if (id == R.id.popular) {
                getSupportActionBar().setTitle(getResources().getString(R.string.popular));
            } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.top_rated));
            }
        }

        progressBar = findViewById(R.id.progressbar);
        getSupportActionBar().setTitle(R.string.popular);
        mNoInternetImageView = findViewById(R.id.no_internet_image);

        mNoInternetImageView.setVisibility(View.INVISIBLE);
        refreshPage = findViewById(R.id.pullToRefresh);

        progressBar.setVisibility(View.VISIBLE);

        refreshPage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (flag != 3) {
                    progressBar.setVisibility(View.VISIBLE);
                    pageIncrement = 1;
                    mAdapter.clearData();
                    apiRequest(flag);
                }
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

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (flag != 3) {
                    pageIncrement++;
                    apiRequest(flag);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        setUpViewModels(flag);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("FLAG", flag);
        outState.putInt("ITEM_NAME", id);
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleItemRecyclerViewAdapter(this, mTwoPane);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void setupViewModelForPopularMovies() {
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.loadPopularMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                progressBar.setVisibility(View.INVISIBLE);
                mAdapter.setMovieList(movies);
                loadInitialMovie(movies.get(0));
            }
        });
    }

    private void setupViewModelForTopRatedMovies() {
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.loadTopRatedMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                progressBar.setVisibility(View.INVISIBLE);
                mAdapter.setMovieList(movies);
                loadInitialMovie(movies.get(0));
            }
        });
    }

    private void setupViewModelForFavoriteMovies() {
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getFavouriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (flag == 3) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mAdapter.clearData();
                    mAdapter.setMovieList(movies);
                    loadInitialMovie(movies.get(0));
                }
            }
        });
    }

    private void loadInitialMovie(Movie movie) {
        if (mTwoPane && movie != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MOVIE_KEY, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    public void setUpViewModels(int flag) {

        switch (flag) {
            case 1:
                setupViewModelForPopularMovies();
                break;
            case 2:
                setupViewModelForTopRatedMovies();
                break;
            case 3:
                setupViewModelForFavoriteMovies();
                break;
        }
    }

    private void getPopularMovies() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = apiService.getPopularMovies(API_KEY, pageIncrement);
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {

            @Override
            public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                List<Movie> list = response.body().getResults();
                progressBar.setVisibility(View.INVISIBLE);
                mNoInternetImageView.setVisibility(View.INVISIBLE);
                mAdapter.setMovieList(list);
                loadInitialMovie(list.get(0));
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                mNoInternetImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getTopRatedMovies() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY, pageIncrement);
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                List<Movie> list = response.body().getResults();
                progressBar.setVisibility(View.INVISIBLE);
                mNoInternetImageView.setVisibility(View.INVISIBLE);
                mAdapter.setMovieList(list);
                loadInitialMovie(list.get(0));
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                mNoInternetImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filteritems, menu);
        return true;
    }

    private void apiRequest(int flag) {
        switch (flag) {
            case 1:
                getPopularMovies();
                break;
            case 2:
                getTopRatedMovies();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        pageIncrement = 1;
        mAdapter.clearData();
        switch (id) {
            case R.id.popular:
                flag = 1;
                progressBar.setVisibility(View.VISIBLE);
                apiRequest(flag);
                getSupportActionBar().setTitle(R.string.popular);
                break;
            case R.id.toprated:
                flag = 2;
                progressBar.setVisibility(View.VISIBLE);
                apiRequest(flag);
                getSupportActionBar().setTitle(R.string.top_rated);
                break;
            case R.id.favorites:
                flag = 3;
                progressBar.setVisibility(View.VISIBLE);
                setupViewModelForFavoriteMovies();
                getSupportActionBar().setTitle(R.string.favorites);
        }
        return super.onOptionsItemSelected(item);
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
                notifyDataSetChanged();
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
