package com.example.android.movie.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movie.R;
import com.example.android.movie.pojo.MovieReview;

import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private Context mContext;
    private List<MovieReview> mMovieReviews;

    public MovieReviewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list, viewGroup, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder movieReviewViewHolder, int i) {
        MovieReview movieReview = mMovieReviews.get(i);
        movieReviewViewHolder.author.setText(movieReview.getAuthor());
        movieReviewViewHolder.content.setText(movieReview.getContent());
    }

    public void setMovieReviews(List<MovieReview> reviews) {
        mMovieReviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMovieReviews == null ? 0 : mMovieReviews.size();
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView content;

        public MovieReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.tv_review_author);
            content = itemView.findViewById(R.id.tv_review_content);
        }
    }
}

