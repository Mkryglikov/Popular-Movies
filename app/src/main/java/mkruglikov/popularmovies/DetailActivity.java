package mkruglikov.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import mkruglikov.popularmovies.data.Movie;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MoviesAdapter.INTENT_EXTRA_KEY));

        setTitle(movie.getTitle());
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView ivPoster = findViewById(R.id.ivPoster);
        Picasso.with(this).load(movie.getPoster()).into(ivPoster);

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvReleaseDate = findViewById(R.id.tvReleaseDate);
        TextView tvVoteAverage = findViewById(R.id.tvVoteAverage);

        tvDescription.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate());
        tvVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
    }
}
