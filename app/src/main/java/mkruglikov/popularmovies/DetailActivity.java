package mkruglikov.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

import mkruglikov.popularmovies.data.Movie;
import mkruglikov.popularmovies.data.Review;
import mkruglikov.popularmovies.utilites.MoviesUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "FUCK";
    private boolean isFavorite;
    private static AnimatedVectorDrawableCompat icFavoriteEmpty, icFavoriteFull;
    private int scrollYPosition;
    private RecyclerView rvReviews;
    private NestedScrollView nestedScrollViewDetail;
    private Movie movie;
    private ContentValues cv;

    private static final String KEY_SCROLL_Y_POSITION = "detail_nested_scroll_view_y_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movie = Parcels.unwrap(getIntent().getParcelableExtra(MoviesAdapter.INTENT_EXTRA_KEY));
        setTitle("");

        TextView tvToolbarDetailTitle = findViewById(R.id.tvToolbarDetailTitle);
        tvToolbarDetailTitle.setText(movie.getTitle());
        final Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView ivPoster = findViewById(R.id.ivPoster);
        ivPoster.setColorFilter(Color.argb(50, 100, 100, 100));
        Glide.with(this)
                .load(movie.getPoster())
                .thumbnail(0.1f)
                .into(ivPoster);
        ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviesUtils.getTrailerkey(movie.getId(), new MoviesUtils.OnTrailerKeyDownloadedListener() {
                    @Override
                    public void onDownload(String trailerKey) {
                        try { //Open trailer in youtube app
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey)));
                        } catch (ActivityNotFoundException ex) { //Open trailer in browser
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerKey)));
                        }
                    }
                });
            }
        });

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvReleaseDate = findViewById(R.id.tvReleaseDate);
        TextView tvVoteAverage = findViewById(R.id.tvVoteAverage);
        tvDescription.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate());
        tvVoteAverage.setText(String.valueOf(movie.getVoteAverage()));

        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReviews.setNestedScrollingEnabled(false);

        nestedScrollViewDetail = findViewById(R.id.nestedScrollViewDetail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MoviesUtils.getReviews(movie.getId(), new MoviesUtils.OnReviewsDownloadedListener() {
            @Override
            public void onDownload(List<Review> reviews) {
                if (!reviews.isEmpty()) {
                    rvReviews.setAdapter(new ReviewsAdapter(DetailActivity.this, reviews));
                    rvReviews.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (nestedScrollViewDetail != null) {
                                nestedScrollViewDetail.scrollTo(0, scrollYPosition);
                            }
                        }
                    }, 20); //todo How do I know if RecyclerView is ready and shown?
                } else
                    findViewById(R.id.tvReviewsLabel).setVisibility(View.GONE);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(Uri.parse("content://mkruglikov.popularmovies/movies"), movie.getId()), new String[]{FavoriteMoviesProvider.KEY_ID}, FavoriteMoviesProvider.KEY_ID + " = " + movie.getId(), null, null);

        isFavorite = cursor.moveToFirst();
        cursor.close();

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        icFavoriteEmpty = AnimatedVectorDrawableCompat.create(DetailActivity.this, R.drawable.ic_favorite_empty);
        icFavoriteFull = AnimatedVectorDrawableCompat.create(DetailActivity.this, R.drawable.ic_favorite_full);
        menu.findItem(R.id.action_favorite).setIcon(isFavorite ? icFavoriteFull : icFavoriteEmpty);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            cv = new ContentValues();

            if (isFavorite)
                deleteMovieFromFavorite();
            else
                addMovieToFavorite();

            item.setIcon(isFavorite ? icFavoriteFull : icFavoriteEmpty);
            (isFavorite ? icFavoriteFull : icFavoriteEmpty).start();
            isFavorite = !isFavorite;
        }
        return super.onOptionsItemSelected(item);
    }

    void addMovieToFavorite() {
        cv.put(FavoriteMoviesProvider.KEY_ID, movie.getId());
        cv.put(FavoriteMoviesProvider.KEY_TITLE, movie.getTitle());
        cv.put(FavoriteMoviesProvider.KEY_RELEASE_DATE, movie.getReleaseDate());
        cv.put(FavoriteMoviesProvider.KEY_POSTER, movie.getPoster());
        cv.put(FavoriteMoviesProvider.KEY_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(FavoriteMoviesProvider.KEY_OVERVIEW, movie.getOverview());

        getContentResolver().insert(FavoriteMoviesProvider.CONTENT_URI, cv);
    }

    void deleteMovieFromFavorite() {
        getContentResolver().delete(Uri.parse("content://" + FavoriteMoviesProvider.AUTHORITY + "/" + FavoriteMoviesProvider.PATH + "/" + movie.getId()), null, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCROLL_Y_POSITION, nestedScrollViewDetail.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollYPosition = savedInstanceState.getInt(KEY_SCROLL_Y_POSITION);
    }
}
