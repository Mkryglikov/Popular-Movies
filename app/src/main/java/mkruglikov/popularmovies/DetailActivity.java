package mkruglikov.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
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

import org.parceler.Parcels;

import mkruglikov.popularmovies.data.Movie;
import mkruglikov.popularmovies.data.Review;
import mkruglikov.popularmovies.utilites.DBHelper;
import mkruglikov.popularmovies.utilites.MoviesUtils;

public class DetailActivity extends AppCompatActivity {

    boolean isFavorite;
    private static AnimatedVectorDrawableCompat icFavoriteEmpty, icFavoriteFull;
    private RecyclerView rvReviews;
    private DBHelper dbHelper;
    private Movie movie;
    private ContentValues cv;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

<<<<<<< HEAD
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MoviesAdapter.INTENT_EXTRA_KEY));
=======
        movie = Parcels.unwrap(getIntent().getParcelableExtra(MoviesAdapter.INTENT_EXTRA_KEY));
>>>>>>> dev

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
        rvReviews = findViewById(R.id.rvReviews);

        tvDescription.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate());
        tvVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
        rvReviews.setHasFixedSize(true);
        rvReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReviews.setNestedScrollingEnabled(false);

        MoviesUtils.getReviews(movie.getId(), new MoviesUtils.OnReviewsDownloadedListener() {
            @Override
            public void onDownload(List<Review> downloadedReviews) {
                rvReviews.setAdapter(new ReviewsAdapter(DetailActivity.this, downloadedReviews));
            }
        });
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{DBHelper.KEY_ID}, DBHelper.KEY_ID + " = " + movie.getId(), null, null, null, null);
        if (cursor.moveToFirst())
            isFavorite = true;
        else
            isFavorite = false;
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
            if (!isFavorite)
                addMovieToFavorite();
            else
                deleteMovieFromFavorite();

            item.setIcon(isFavorite ? icFavoriteFull : icFavoriteEmpty);
            (isFavorite ? icFavoriteFull : icFavoriteEmpty).start();
            isFavorite = !isFavorite;
        } else if (item.getItemId() == R.id.homeAsUp) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    void addMovieToFavorite() {
        cv.put(DBHelper.KEY_ID, movie.getId());
        cv.put(DBHelper.KEY_TITLE, movie.getTitle());
        cv.put(DBHelper.KEY_RELEASE_DATE, movie.getReleaseDate());
        cv.put(DBHelper.KEY_POSTER, movie.getPoster());
        cv.put(DBHelper.KEY_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(DBHelper.KEY_OVERVIEW, movie.getOverview());

        db.insert(DBHelper.TABLE_NAME, null, cv);
    }

    void deleteMovieFromFavorite() {
        db.delete(DBHelper.TABLE_NAME, DBHelper.KEY_ID + "=" + movie.getId(), null);
    }

}
