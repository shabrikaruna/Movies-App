package com.example.android.movie;

import retrofit.Callback;
import retrofit.http.GET;

public interface MoviesApiService {
    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> callback);

    @GET("/movie/top_rated")
    void getTopRatedMovies(Callback<Movie.MovieResult> callback);
}
