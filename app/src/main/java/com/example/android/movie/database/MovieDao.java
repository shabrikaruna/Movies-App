package com.example.android.movie.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.movie.pojo.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getFavourites();

    @Query("SELECT COUNT(*) FROM movie WHERE id = :id")
    int loadAllFavourites(int id);
}
