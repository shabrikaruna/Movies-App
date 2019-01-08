package com.example.android.movie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MovieDetailFragment extends Fragment implements MovieTrailerAdapter.ItemClickListener {
    Movie mMovie;
    Activity activity = this.getActivity();
    List<Result> mMovieVideoKey;
    List<MovieReview> mMovieReviews;
    private static final String TAG = "MovieDetailFragment";
    private MovieReviewAdapter movieReviewAdapter;
    private MovieTrailerAdapter movieTrailerAdapter;
    private TextView mTextViewReviewAuthor;
    private TextView mTextViewReviewContent;
    ImageView mImageViewPoster;
    TextView mTextViewRating;
    TextView mTextViewPlot;
    TextView mTextViewVotes;
    TextView mTextViewReleaseDate;
    Button button;
    TextView mTextViewLanguage;
    RecyclerView mRecyclerViewReviews;
    RecyclerView mRecyclerViewTrailers;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MovieListActivity.MOVIE_KEY)) {
            Bundle arguments = getArguments();
            mMovie = arguments.getParcelable(MovieListActivity.MOVIE_KEY);
        }

        getReviews();
        getVideoKey();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.sharemenu, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("text/plain");
//        String shareBody = "sharing trialer of " + mMovie.getTitle() + "\n" + MovieTrailerAdapter.YOUTUBE_BASE_URL + mMovieVideoKey.get(0).getKey();
//        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "video_url");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//        return true;
//    }

    private void getVideoKey() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieVideoKey> call = apiService.getMovieVideo(mMovie.getId(), MovieListActivity.API_KEY);
        call.enqueue(new Callback<MovieVideoKey>() {
            @Override
            public void onResponse(Call<MovieVideoKey> call, Response<MovieVideoKey> response) {
                mMovieVideoKey = response.body().getResults();
                movieTrailerAdapter.setMovieTrailer(mMovieVideoKey);
            }

            @Override
            public void onFailure(Call<MovieVideoKey> call, Throwable t) {

            }
        });
    }


    private void getReviews() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieReviewResponse> call = apiService.getMovieReviews(mMovie.getId(), MovieListActivity.API_KEY);
        call.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                mMovieReviews = response.body().getResults();
                movieReviewAdapter.setMovieReviews(mMovieReviews);
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        initViews(rootView);
        setupRecyclerViewForReviews();
        setupRecyclerViewForTrailers();

        Picasso.with(activity)
                .load(MovieListActivity.TMDB_IMAGE_PATH + mMovie.getPoster())
                .fit()
                .into(mImageViewPoster);
        Double rating = mMovie.getVoteAverage();
        String ratingString = Double.toString(rating);
        ratingString += getString(R.string.rating_string);
        mTextViewRating.setText(String.format("%s %s", getString(R.string.rating), ratingString));
        mTextViewPlot.setText(mMovie.getDescription());
        mTextViewReleaseDate.setText(String.format("%s %s", getString(R.string.releaseDate), mMovie.getReleaseDate()));
        mTextViewVotes.setText(String.format("%s %d", getString(R.string.Votes), mMovie.getVoteCount()));
        mTextViewLanguage.setText("Language " + mMovie.getOriginalLanguage());
        return rootView;
    }

    private void setupRecyclerViewForReviews() {
        movieReviewAdapter = new MovieReviewAdapter(getActivity());
        mRecyclerViewReviews.setAdapter(movieReviewAdapter);
        mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupRecyclerViewForTrailers() {
        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), this);
        mRecyclerViewTrailers.setAdapter(movieTrailerAdapter);
        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initViews(View rootView) {
        mTextViewLanguage = rootView.findViewById(R.id.text_view_language);
        mImageViewPoster = rootView.findViewById(R.id.image_view_poster);
        mTextViewRating = rootView.findViewById(R.id.text_view_rating);
        mTextViewPlot = rootView.findViewById(R.id.text_view_plot);
        mTextViewVotes = rootView.findViewById(R.id.text_view_votes);
        mTextViewReleaseDate = rootView.findViewById(R.id.text_view_release_date);
        mTextViewReviewContent = rootView.findViewById(R.id.tv_review_content);
        mTextViewReviewAuthor = rootView.findViewById(R.id.tv_review_author);
        mRecyclerViewReviews = rootView.findViewById(R.id.movie_list_review);
        mRecyclerViewTrailers = rootView.findViewById(R.id.movie_list_trailers);
    }

    @Override
    public void onItemClickListener(String trailer_url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailer_url));
        startActivity(intent);
    }
}
