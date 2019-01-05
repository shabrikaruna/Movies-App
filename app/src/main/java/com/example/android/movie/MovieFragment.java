package com.example.android.movie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MovieFragment extends Fragment implements MoviesAdapter.ItemClickListener {

    private MoviesAdapter mAdapter;
    private final static String API_KEY = "d557ce13bede53617fbb431b9de5791e";
    private EndlessRecyclerViewScrollListener scrollListener;
    private int page = 1;
    private RecyclerView mRecyclerView;
    private static final int NUMBER_OF_COLUMNS = 2;
    private List<Movie> movies = new ArrayList<>();
    private int id = R.id.popular;
    private ProgressBar progressBar;
    private ProgressBar progressBar_Bottom;

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUMBER_OF_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setMovieList(movies);
        progressBar = view.findViewById(R.id.progressbar);
        progressBar_Bottom = view.findViewById(R.id.progressbar_bottom);

        progressBar.setVisibility(View.VISIBLE);
        progressBar_Bottom.setVisibility(View.INVISIBLE);
        setHasOptionsMenu(true);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                progressBar_Bottom.setVisibility(View.VISIBLE);
                getMovies();
            }
        };
        getMovies();
        mRecyclerView.addOnScrollListener(scrollListener);
        return view;
    }


    public void getMovies() {

        if (id == R.id.popular) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(API_KEY, page++);
            call.enqueue(new retrofit2.Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                    List<Movie> list = response.body().getResults();
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar_Bottom.setVisibility(View.INVISIBLE);
                    mAdapter.setMovieList(list);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {

                }
            });
        } else if (id == R.id.toprated) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY, page++);
            call.enqueue(new retrofit2.Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                    List<Movie> list = response.body().getResults();
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar_Bottom.setVisibility(View.INVISIBLE);
                    mAdapter.setMovieList(list);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onMovieSelected(Movie movie) {
        Intent intentToStartMovieDetail = new Intent(getActivity(), MovieDetails.class);
        intentToStartMovieDetail.putExtra(MovieDetails.MOVIE_OBJECT, movie);
        startActivity(intentToStartMovieDetail);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.filteritems, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        id = item.getItemId();
        switch (id) {
            case R.id.toprated:
                page = 1;
                getMovies();
                progressBar.setVisibility(View.VISIBLE);
                mAdapter.clearData();
                return true;
            case R.id.popular:
                page = 1;
                getMovies();
                mAdapter.clearData();
                progressBar.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
