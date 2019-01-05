package com.example.android.movie;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {
    Movie mMovie;
    Activity activity = this.getActivity();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ImageView mImageViewPoster = rootView.findViewById(R.id.image_view_poster);
        TextView mTextViewTitle = rootView.findViewById(R.id.text_view_title);
        TextView mTextViewRating = rootView.findViewById(R.id.text_view_rating);
        TextView mTextViewPlot = rootView.findViewById(R.id.text_view_plot);
        TextView mTextViewVotes = rootView.findViewById(R.id.text_view_votes);
        TextView mTextViewReleaseDate = rootView.findViewById(R.id.text_view_release_date);

        Picasso.with(activity)
                .load(MovieListActivity.TMDB_IMAGE_PATH + mMovie.getPoster())
                .fit()
                .into(mImageViewPoster);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mMovie.getTitle());

        mTextViewTitle.setText(mMovie.getTitle());
        String title = mMovie.getTitle();
        Double rating = mMovie.getVoteAverage();
        String ratingString = Double.toString(rating);
        ratingString += " / 10";
        mTextViewRating.setText(String.format("%s %s", getString(R.string.rating), ratingString));
        mTextViewPlot.setText(mMovie.getDescription());
        mTextViewReleaseDate.setText(String.format("%s %s", getString(R.string.releaseDate), mMovie.getReleaseDate()));
        mTextViewVotes.setText(String.format("%s %d", getString(R.string.Votes), mMovie.getVoteCount()));
        return rootView;
    }
}
