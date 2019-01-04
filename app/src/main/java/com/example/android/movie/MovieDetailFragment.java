package com.example.android.movie;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MovieListActivity.MOVIE_KEY)) {

            Bundle arguments = getArguments();
            mMovie = arguments.getParcelable(MovieListActivity.MOVIE_KEY);
            Activity activity = this.getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ImageView mImageViewBackdrop = rootView.findViewById(R.id.image_view_backdrop);
        TextView mTextViewTitle = rootView.findViewById(R.id.text_view_title);
        TextView mTextViewRating = rootView.findViewById(R.id.text_view_rating);
        TextView mTextViewPlot = rootView.findViewById(R.id.text_view_plot);

        Picasso.with(getActivity())
                .load(mMovie.getBackdrop())
                .placeholder(R.drawable.ic_movie_poster_landscape)
                .fit()
                .into(mImageViewBackdrop);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mMovie.getTitle());

        mTextViewTitle.setText(mMovie.getTitle());
        String title = mMovie.getTitle();
        Double rating = mMovie.getVoteAverage();
        String ratingString = Double.toString(rating);
        ratingString += " / 10";
        mTextViewRating.setText(ratingString);
        mTextViewPlot.setText(mMovie.getDescription());

        return rootView;
    }
}
