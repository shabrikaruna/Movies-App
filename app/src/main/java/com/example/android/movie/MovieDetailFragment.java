package com.example.android.movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movie.database.MovieVideoKey;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {
    Movie mMovie;
    Activity activity = this.getActivity();
    List<Result> mMovieVideoKey;
    private static final String TAG = "MovieDetailFragment";

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MovieListActivity.MOVIE_KEY)) {
            Bundle arguments = getArguments();
            mMovie = arguments.getParcelable(MovieListActivity.MOVIE_KEY);
        }
    }

    private void getVideoKey() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieVideoKey> call = apiService.getMovieVideo(mMovie.getId(), MovieListActivity.API_KEY);
        call.enqueue(new Callback<MovieVideoKey>() {
            @Override
            public void onResponse(Call<MovieVideoKey> call, Response<MovieVideoKey> response) {
                mMovieVideoKey = response.body().getResults();
                Intent intent = new Intent(getActivity(), YouTubeActivity.class);
                intent.putExtra("video_key", mMovieVideoKey.get(0).getKey());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<MovieVideoKey> call, Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ImageView mImageViewPoster = rootView.findViewById(R.id.image_view_poster);
        TextView mTextViewTitle = rootView.findViewById(R.id.text_view_title);
        TextView mTextViewRating = rootView.findViewById(R.id.text_view_rating);
        TextView mTextViewPlot = rootView.findViewById(R.id.text_view_plot);
        TextView mTextViewVotes = rootView.findViewById(R.id.text_view_votes);
        TextView mTextViewReleaseDate = rootView.findViewById(R.id.text_view_release_date);
        Button button = rootView.findViewById(R.id.youtube_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideoKey();
//                Toast.makeText(getActivity(),"ID "+ mMovieVideoKey.get(0).getKey(),Toast.LENGTH_SHORT).show();


            }
        });

        Picasso.with(activity)
                .load(MovieListActivity.TMDB_IMAGE_PATH + mMovie.getPoster())
                .fit()
                .into(mImageViewPoster);

        mTextViewTitle.setText(mMovie.getTitle());
        Double rating = mMovie.getVoteAverage();
        String ratingString = Double.toString(rating);
        ratingString += getString(R.string.rating_string);
        mTextViewRating.setText(String.format("%s %s", getString(R.string.rating), ratingString));
        mTextViewPlot.setText(mMovie.getDescription());
        mTextViewReleaseDate.setText(String.format("%s %s", getString(R.string.releaseDate), mMovie.getReleaseDate()));
        mTextViewVotes.setText(String.format("%s %d", getString(R.string.Votes), mMovie.getVoteCount()));
        return rootView;
    }
}
