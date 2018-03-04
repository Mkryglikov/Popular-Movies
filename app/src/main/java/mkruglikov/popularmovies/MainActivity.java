package mkruglikov.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import mkruglikov.popularmovies.data.Movie;
import mkruglikov.popularmovies.utilites.MoviesUtils;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieClickListener {

    private static final int SPAN_COUNT = 2;
    private boolean isSortedByPopular = true;

    private MoviesAdapter moviesAdapter;
    private boolean isNetworkAvailable;
    private RecyclerView rvMain;
    private ConnectivityManager cm;
    private SwipeRefreshLayout swipeRefreshMain;
    private TextView tvToolbarTitle;
    private ConstraintLayout constraintNetworkError;
    private static AnimatedVectorDrawableCompat sortPopularIcon, sortTopRatedIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        constraintNetworkError = findViewById(R.id.constraintNetworkError);
        tvToolbarTitle = findViewById(R.id.tvToolbarMainTitle);
        tvToolbarTitle.setText(isSortedByPopular ? getText(R.string.popular_title) : getText(R.string.top_rated_title));
        rvMain = findViewById(R.id.rvMain);
        rvMain.setHasFixedSize(true);

        swipeRefreshMain = findViewById(R.id.swipeRefreshMain);
        swipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMovies();
                invalidateOptionsMenu();
            }
        });

        isNetworkAvailable = (cm != null && cm.getActiveNetworkInfo() != null);
        loadMovies();
        sortPopularIcon = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sort_popular);
        sortTopRatedIcon = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sort_top_rated);
    }

    private void loadMovies() {
        if (cm == null || cm.getActiveNetworkInfo() == null) { //No internet connection
            if (isNetworkAvailable) { //Network WAS available before method call
                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                rvMain.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rvMain.setVisibility(View.GONE);
                        constraintNetworkError.setVisibility(View.VISIBLE);
                        constraintNetworkError.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                    }
                }, getResources().getInteger(R.integer.animation_duration));
            } else { //Network WASN'T available before method call
                if (rvMain.getVisibility() == View.VISIBLE)
                    rvMain.setVisibility(View.GONE);
            }
            isNetworkAvailable = false;
            invalidateOptionsMenu();
            swipeRefreshMain.setRefreshing(false);
        } else { //There is an internet connection

            if (moviesAdapter == null) { //First load
                rvMain.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false));

                if (isSortedByPopular) {
                    MoviesUtils.getPopular(new MoviesUtils.OnPopularMoviesDownloadedListener() {
                        @Override
                        public void onDownload(List<Movie> downloadedPopularMovies) {
                            moviesAdapter = new MoviesAdapter(MainActivity.this, downloadedPopularMovies, MainActivity.this);
                            rvMain.setAdapter(moviesAdapter);
                            if (rvMain.getVisibility() != View.VISIBLE) {
                                rvMain.setVisibility(View.VISIBLE);
                                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                            }
                            swipeRefreshMain.setRefreshing(false);
                        }
                    });
                } else {
                    MoviesUtils.getTopRated(new MoviesUtils.OnTopRatedMoviesDownloadedListener() {
                        @Override
                        public void onDownload(List<Movie> downloadedTopRatedMovies) {
                            moviesAdapter = new MoviesAdapter(MainActivity.this, downloadedTopRatedMovies, MainActivity.this);
                            rvMain.setAdapter(moviesAdapter);
                            if (rvMain.getVisibility() != View.VISIBLE) {
                                rvMain.setVisibility(View.VISIBLE);
                                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                            }
                            swipeRefreshMain.setRefreshing(false);
                        }
                    });
                }
            } else { //Update current posters
                if (isSortedByPopular) {
                    MoviesUtils.getPopular(new MoviesUtils.OnPopularMoviesDownloadedListener() {
                        @Override
                        public void onDownload(List<Movie> downloadedPopularMovies) {
                            tvToolbarTitle.setText(getText(R.string.popular_title));
                            moviesAdapter.setMovies(downloadedPopularMovies);
                            if (rvMain.getVisibility() != View.VISIBLE) {
                                rvMain.setVisibility(View.VISIBLE);
                                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                            }
                            swipeRefreshMain.setRefreshing(false);
                        }
                    });
                } else {
                    MoviesUtils.getTopRated(new MoviesUtils.OnTopRatedMoviesDownloadedListener() {
                        @Override
                        public void onDownload(List<Movie> downloadedTopRatedMovies) {
                            tvToolbarTitle.setText(getText(R.string.top_rated_title));
                            moviesAdapter.setMovies(downloadedTopRatedMovies);
                            if (rvMain.getVisibility() != View.VISIBLE) {
                                rvMain.setVisibility(View.VISIBLE);
                                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                            }
                            swipeRefreshMain.setRefreshing(false);
                        }
                    });
                }
            }

            if (!isNetworkAvailable) { //Network WASN'T available before method call
                constraintNetworkError.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                constraintNetworkError.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        constraintNetworkError.setVisibility(View.GONE);
                        rvMain.setVisibility(View.VISIBLE);
                        rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                    }
                }, getResources().getInteger(R.integer.animation_duration));
            }
            isNetworkAvailable = true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem miSort = menu.findItem(R.id.sort);
        miSort.setEnabled(isNetworkAvailable);
        miSort.getIcon().setAlpha(isNetworkAvailable ? 255 : 64);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //Recreate drawables to reset their state
        sortPopularIcon = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sort_popular);
        sortTopRatedIcon = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sort_top_rated);
        menu.findItem(R.id.sort).setIcon(isSortedByPopular ? sortPopularIcon : sortTopRatedIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.sort) {

            rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
            if (cm != null && cm.getActiveNetworkInfo() != null) {
                item.setIcon(isSortedByPopular ? sortPopularIcon : sortTopRatedIcon);
                (isSortedByPopular ? sortPopularIcon : sortTopRatedIcon).start();
                tvToolbarTitle.setText(getText(isSortedByPopular ? R.string.top_rated_title : R.string.popular_title));
                isSortedByPopular = !isSortedByPopular;
            }
            rvMain.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMain.setVisibility(View.INVISIBLE);
                    loadMovies();
                }
            }, getResources().getInteger(R.integer.animation_duration));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MoviesAdapter.INTENT_EXTRA_KEY, Parcels.wrap(movie));
        startActivity(intent);
    }
}
