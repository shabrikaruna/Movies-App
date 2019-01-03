package com.example.android.movie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Movie object = (Movie) getIntent().getParcelableExtra("myDataKey");
        Toast.makeText(MovieDetails.this,object.getTitle(),Toast.LENGTH_SHORT).show();
    }
}
