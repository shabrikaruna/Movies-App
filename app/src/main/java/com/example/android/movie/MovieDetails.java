package com.example.android.movie;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    public static final String MOVIE_OBJECT = "movie_object";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView mImageViewBackdrop = findViewById(R.id.image_view_backdrop);
        TextView mTextViewTitle = findViewById(R.id.text_view_title);
        TextView mTextViewRating = findViewById(R.id.text_view_rating);
        TextView mTextViewPlot = findViewById(R.id.text_view_plot);
        TextView mTextViewVotes = findViewById(R.id.text_view_votes);
        TextView mTextViewReleaseDate = findViewById(R.id.text_view_release_date);

        Movie movieObject = getIntent().getParcelableExtra(MOVIE_OBJECT);

        Picasso.with(mContext)
                .load(movieObject.getBackdrop())
                .placeholder(R.drawable.ic_movie_poster_landscape)
                .fit()
                .into(mImageViewBackdrop);

        mTextViewTitle.setText(movieObject.getTitle());
        Double rating = movieObject.getVoteAverage();
        String ratingString = Double.toString(rating);
        ratingString += " / 10";
        mTextViewRating.setText(ratingString);
        mTextViewPlot.setText(movieObject.getDescription());
        mTextViewReleaseDate.setText(String.format("%s%s", getString(R.string.releasedate), movieObject.getReleaseDate()));
        mTextViewVotes.setText(String.format(getString(R.string.votes), movieObject.getVoteCount()));
    }
}
