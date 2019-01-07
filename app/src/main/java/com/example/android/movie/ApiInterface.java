package com.example.android.movie;

import com.example.android.movie.database.MovieVideoKey;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int pages);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int pages);

    @GET("movie/{id}/videos")
    Call<MovieVideoKey> getMovieVideo(@Path("id") Integer id, @Query("api_key") String apiKey);
}