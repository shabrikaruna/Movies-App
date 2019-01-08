package com.example.android.movie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.android.movie.database.AppDatabase;

import java.util.List;

import retrofit2.Call;

import static com.example.android.movie.MovieListActivity.API_KEY;
import static com.example.android.movie.MovieListActivity.pageIncrement;

public class MovieViewModel extends AndroidViewModel {

    private MutableLiveData<List<Movie>> moviesViewModel;

    private AppDatabase mDb;

    public MovieViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Movie>> loadPopularMovies() {
        if (moviesViewModel == null) {
            moviesViewModel = new MutableLiveData<>();
            getPopularMovies();
        }
        return moviesViewModel;
    }

    public LiveData<List<Movie>> loadTopRatedMovies() {
        if (moviesViewModel == null) {
            moviesViewModel = new MutableLiveData<>();
            getTopRatedMovies();
        }
        return moviesViewModel;
    }

    public LiveData<List<Movie>> getFavouriteMovies() {
        mDb = AppDatabase.getInstance(this.getApplication());
        moviesViewModel = new MutableLiveData<>();
        moviesViewModel.setValue(mDb.movieDao().getFavourites());
        return moviesViewModel;
    }

    private void getPopularMovies() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = apiService.getPopularMovies(API_KEY, pageIncrement);
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {

            @Override
            public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                moviesViewModel.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getTopRatedMovies() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY, pageIncrement);
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, retrofit2.Response<MoviesResponse> response) {
                moviesViewModel.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}


