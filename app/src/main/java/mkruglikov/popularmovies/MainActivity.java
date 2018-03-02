package mkruglikov.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

import mkruglikov.popularmovies.data.Movies;

public class MainActivity extends AppCompatActivity {

    public static final int SPAN_COUNT = 2;
    private MoviesAdapter moviesAdapter;
    private boolean isSortedByPopular, isNetworkAvailable;
    private RecyclerView rvMain;
    private ConnectivityManager cm;
    private SwipeRefreshLayout swiperefreshMain;
    private TextView tvNoNetwork, tvToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        tvNoNetwork = findViewById(R.id.tvNoNetwork);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        rvMain = findViewById(R.id.rvMain);
        rvMain.setHasFixedSize(true);

        swiperefreshMain = findViewById(R.id.swiperefreshMain);
        swiperefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosters();
            }
        });

        loadPosters();
    }

    private void loadPosters() {
        if (cm == null || cm.getActiveNetworkInfo() == null) { //No internet connection
            if (isNetworkAvailable) {
                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                rvMain.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rvMain.setVisibility(View.GONE);
                    }
                }, 600);
            } else {
                rvMain.setVisibility(View.GONE);
            }
            tvNoNetwork.setVisibility(View.VISIBLE);
            if (isNetworkAvailable)
                tvNoNetwork.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
            isNetworkAvailable = false;
            invalidateOptionsMenu();
            swiperefreshMain.setRefreshing(false);
            return;
        } else {
            if (!isNetworkAvailable) {
                tvNoNetwork.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                tvNoNetwork.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvNoNetwork.setVisibility(View.GONE);
                    }
                }, 600);
            } else {
                tvNoNetwork.setVisibility(View.GONE);
            }
            rvMain.setVisibility(View.VISIBLE);
            if (!isNetworkAvailable)
                rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
            isNetworkAvailable = true;
            invalidateOptionsMenu();
        }

        if (moviesAdapter == null) { //First load
            rvMain.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
            moviesAdapter = new MoviesAdapter(this, Movies.getPopular());
            isSortedByPopular = true;
            rvMain.setAdapter(moviesAdapter);
            swiperefreshMain.setRefreshing(false);
        } else { //Update current posters
            moviesAdapter.setPosters(isSortedByPopular ? Movies.getPopular() : Movies.getTopRated());
            swiperefreshMain.setRefreshing(false);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort) {
            rvMain.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade));
            rvMain.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSortedByPopular) {
                        moviesAdapter.setPosters(Movies.getTopRated());
                        tvToolbarTitle.setText("Top Rated Movies");
                        isSortedByPopular = false;
                    } else {
                        moviesAdapter.setPosters(Movies.getPopular());
                        tvToolbarTitle.setText("Popular Movies");
                        isSortedByPopular = true;
                    }
                }
            }, 300);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
