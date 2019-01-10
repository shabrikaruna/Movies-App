package com.example.android.movie.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movie.R;
import com.example.android.movie.pojo.Result;

import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {

    private Context mContext;
    private List<Result> mMovieTrailerUrl;
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    final private ItemClickListener mItemClickListener;

    public MovieTrailerAdapter(Context mContext, ItemClickListener listener) {
        this.mContext = mContext;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list, viewGroup, false);
        return new MovieTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailerViewHolder movieTrailerViewHolder, int i) {
        Result result = mMovieTrailerUrl.get(i);
        movieTrailerViewHolder.trailerName.setText(result.getName());
    }

    public void setMovieTrailer(List<Result> results) {
        mMovieTrailerUrl = results;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String trailer_url);
    }

    @Override
    public int getItemCount() {
        return mMovieTrailerUrl == null ? 0 : mMovieTrailerUrl.size();
    }

    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerName;

        public MovieTrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.tv_trailer_link);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String elementId = mMovieTrailerUrl.get(getAdapterPosition()).getKey();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}


