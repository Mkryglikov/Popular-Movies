package mkruglikov.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
    private static final String KEY_SORTING = "sorted_by";
    private static final String KEY_RV_STATE = "main_recycler_view_state";
    private final int POPULAR = 555;
    private final int FAVORITE = 666;
    private final int TOP_RATED = 777;

    private int sortedBy = POPULAR; //Default sorting

    private MoviesAdapter moviesAdapter;
    private GridLayoutManager gridLayoutManager;
    private Parcelable rvState;
    private boolean isNetworkAvailable;
    private RecyclerView rvMain;
    private ConnectivityManager cm;
    private SwipeRefreshLayout swipeRefreshMain;
    private TextView tvToolbarTitle;
    private BottomNavigationView bottomNavigation;
    private ConstraintLayout constraintNetworkError, constraintFavoriteError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_popular:
                        if (constraintFavoriteError.getVisibility() != View.VISIBLE)
                            rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                        if (cm != null && cm.getActiveNetworkInfo() != null) {
                            tvToolbarTitle.setText(getText(R.string.popular_title));
                            sortedBy = POPULAR;
                        }
                        rvMain.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvMain.setVisibility(View.INVISIBLE);
                                loadMovies();
                            }
                        }, getResources().getInteger(R.integer.animation_duration));
                        break;
                    case R.id.action_favorite:
                        rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                        if (cm != null && cm.getActiveNetworkInfo() != null) {
                            tvToolbarTitle.setText(getText(R.string.favorite_title));
                            sortedBy = FAVORITE;
                        }
                        rvMain.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvMain.setVisibility(View.INVISIBLE);
                                loadMovies();
                            }
                        }, getResources().getInteger(R.integer.animation_duration));
                        break;
                    case R.id.action_topRated:
                        if (constraintFavoriteError.getVisibility() != View.VISIBLE)
                            rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                        if (cm != null && cm.getActiveNetworkInfo() != null) {
                            tvToolbarTitle.setText(getText(R.string.top_rated_title));
                            sortedBy = TOP_RATED;
                        }
                        rvMain.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvMain.setVisibility(View.INVISIBLE);
                                loadMovies();
                            }
                        }, getResources().getInteger(R.integer.animation_duration));
                        break;
                }
                return true;
            }
        });
        bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                loadMovies();
            }
        });

        constraintNetworkError = findViewById(R.id.constraintNetworkError);
        constraintFavoriteError = findViewById(R.id.constraintFavoriteError);
        tvToolbarTitle = findViewById(R.id.tvToolbarMainTitle);
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvMain != null) rvMain.smoothScrollToPosition(0);
            }
        });

        rvMain = findViewById(R.id.rvMain);
        rvMain.setHasFixedSize(true);

        swipeRefreshMain = findViewById(R.id.swipeRefreshMain);
        swipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMovies();
            }
        });

        isNetworkAvailable = (cm != null && cm.getActiveNetworkInfo() != null);
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
                if (constraintNetworkError.getVisibility() != View.VISIBLE)
                    constraintNetworkError.setVisibility(View.VISIBLE);
            }
            isNetworkAvailable = false;
            bottomNavigation.setActivated(false);
        } else { //There is an internet connection
            bottomNavigation.setActivated(true);
            if (moviesAdapter == null) { //First load
                gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
                rvMain.setLayoutManager(gridLayoutManager);

                switch (sortedBy) {
                    case POPULAR:
                        MoviesUtils.getPopular(new MoviesUtils.OnPopularMoviesDownloadedListener() {
                            @Override
                            public void onDownload(List<Movie> popularMovies) {
                                moviesAdapter = new MoviesAdapter(MainActivity.this, popularMovies, MainActivity.this);
                                rvMain.setAdapter(moviesAdapter);
                                gridLayoutManager.onRestoreInstanceState(rvState);
                                if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                    constraintFavoriteError.setVisibility(View.GONE);
                                if (rvMain.getVisibility() != View.VISIBLE) {
                                    rvMain.setVisibility(View.VISIBLE);
                                    rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                }
                            }
                        });
                        break;
                    case FAVORITE:
                        MoviesUtils.getFavorite(this, new MoviesUtils.OnFavoriteMoviesDownloadedListener() {
                            @Override
                            public void onDownload(List<Movie> favoriteMovies) {
                                if (favoriteMovies != null) {
                                    moviesAdapter = new MoviesAdapter(MainActivity.this, favoriteMovies, MainActivity.this);
                                    rvMain.setAdapter(moviesAdapter);
                                    gridLayoutManager.onRestoreInstanceState(rvState);
                                    if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                        constraintFavoriteError.setVisibility(View.GONE);
                                    if (rvMain.getVisibility() != View.VISIBLE) {
                                        rvMain.setVisibility(View.VISIBLE);
                                        rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                    }
                                } else {
                                    rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                                    rvMain.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvMain.setVisibility(View.GONE);
                                            constraintFavoriteError.setVisibility(View.VISIBLE);
                                            constraintFavoriteError.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                        }
                                    }, getResources().getInteger(R.integer.animation_duration));
                                }
                            }
                        });
                        break;
                    case TOP_RATED:
                        MoviesUtils.getTopRated(new MoviesUtils.OnTopRatedMoviesDownloadedListener() {
                            @Override
                            public void onDownload(List<Movie> topRatedMovies) {
                                moviesAdapter = new MoviesAdapter(MainActivity.this, topRatedMovies, MainActivity.this);
                                rvMain.setAdapter(moviesAdapter);
                                gridLayoutManager.onRestoreInstanceState(rvState);
                                if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                    constraintFavoriteError.setVisibility(View.GONE);
                                if (rvMain.getVisibility() != View.VISIBLE) {
                                    rvMain.setVisibility(View.VISIBLE);
                                    rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                }
                            }
                        });
                        break;
                }
            } else { //Update current posters
                rvMain.scrollToPosition(0);
                switch (sortedBy) {
                    case POPULAR:
                        MoviesUtils.getPopular(new MoviesUtils.OnPopularMoviesDownloadedListener() {
                            @Override
                            public void onDownload(List<Movie> popularMovies) {
                                moviesAdapter.setMovies(popularMovies);
                                if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                    constraintFavoriteError.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                                constraintFavoriteError.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                            constraintFavoriteError.setVisibility(View.GONE);
                                        if (rvMain.getVisibility() != View.VISIBLE) {
                                            rvMain.setVisibility(View.VISIBLE);
                                            rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                        }
                                    }
                                }, getResources().getInteger(R.integer.animation_duration));
                            }
                        });
                        break;
                    case FAVORITE:
                        MoviesUtils.getFavorite(this, new MoviesUtils.OnFavoriteMoviesDownloadedListener() {
                            @Override
                            public void onDownload(List<Movie> favoriteMovies) {
                                if (favoriteMovies != null) {
                                    moviesAdapter.setMovies(favoriteMovies);
                                    if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                        constraintFavoriteError.setVisibility(View.GONE);
                                    if (rvMain.getVisibility() != View.VISIBLE) {
                                        rvMain.setVisibility(View.VISIBLE);
                                        rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                    }
                                } else {
                                    if (rvMain.getVisibility() == View.VISIBLE)
                                        rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                                    rvMain.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (rvMain.getVisibility() == View.VISIBLE)
                                                rvMain.setVisibility(View.GONE);
                                            if (rvMain.getVisibility() != View.VISIBLE) {
                                                constraintFavoriteError.setVisibility(View.VISIBLE);
                                                constraintFavoriteError.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                            }
                                        }
                                    }, getResources().getInteger(R.integer.animation_duration));
                                }
                            }
                        });
                        break;
                    case TOP_RATED:
                        MoviesUtils.getTopRated(new MoviesUtils.OnTopRatedMoviesDownloadedListener() {
                            @Override
                            public void onDownload(List<Movie> topRatedMovies) {
                                moviesAdapter.setMovies(topRatedMovies);
                                if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                    constraintFavoriteError.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                                rvMain.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (constraintFavoriteError.getVisibility() == View.VISIBLE)
                                            constraintFavoriteError.setVisibility(View.GONE);
                                        if (rvMain.getVisibility() != View.VISIBLE) {
                                            rvMain.setVisibility(View.VISIBLE);
                                            rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
                                        }
                                    }
                                }, getResources().getInteger(R.integer.animation_duration));
                            }
                        });
                        break;
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
        swipeRefreshMain.setRefreshing(false);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MoviesAdapter.INTENT_EXTRA_KEY, Parcels.wrap(movie));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
        switch (sortedBy) {
            case POPULAR:
                tvToolbarTitle.setText(getText(R.string.popular_title));
                break;
            case FAVORITE:
                tvToolbarTitle.setText(getText(R.string.favorite_title));
                break;
            case TOP_RATED:
                tvToolbarTitle.setText(getText(R.string.top_rated_title));
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SORTING, sortedBy);
        if (gridLayoutManager != null)
            outState.putParcelable(KEY_RV_STATE, gridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sortedBy = savedInstanceState.getInt(KEY_SORTING);
        rvState = savedInstanceState.getParcelable(KEY_RV_STATE);
    }
}
