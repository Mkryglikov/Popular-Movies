package mkruglikov.popularmovies.utilites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mkruglikov.popularmovies.BuildConfig;
import mkruglikov.popularmovies.data.Movie;
import mkruglikov.popularmovies.data.Review;

public class MoviesUtils {
    private static OnPopularMoviesDownloadedListener popularMoviesListener;
    private static OnFavoriteMoviesDownloadedListener favoriteMoviesListener;
    private static OnTopRatedMoviesDownloadedListener topRatedMoviesListener;
    private static OnTrailerKeyDownloadedListener trailerKeyListener;
    private static OnReviewsDownloadedListener reviewsListener;
    private static List<Movie> popularMovies = new ArrayList<>();
    private static List<Movie> favoriteMovies = new ArrayList<>();
    private static List<Movie> topRatedMovies = new ArrayList<>();
    private static List<Review> reviews = new ArrayList<>();

    private static final String URL_POPULAR_BASE = "https://api.themoviedb.org/3/movie/popular";
    private static final String URL_TOP_RATED_BASE = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String URL_POSTERS_BASE = "https://image.tmdb.org/t/p/w500";
    private static final String URL_TRAILERS_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String URL_REVIEWS_BASE = "https://api.themoviedb.org/3/movie/";

    private static DBHelper dbHelper;
    private static ContentValues cv;
    private static SQLiteDatabase db;

    public static void getPopular(OnPopularMoviesDownloadedListener onPopularMoviesDownloadedListener) {
        popularMoviesListener = onPopularMoviesDownloadedListener;
        new getPopularAsyncTask().execute();
    }

    public interface OnPopularMoviesDownloadedListener {
        void onDownload(List<Movie> downloadedPopularMovies);
    }

    private static class getPopularAsyncTask extends AsyncTask<OnPopularMoviesDownloadedListener, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(OnPopularMoviesDownloadedListener... listeners) {
            try {
                URL popularMoviesUrl = new URL(Uri.parse(URL_POPULAR_BASE).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIEDB_KEY)
                        .appendQueryParameter("language", "en-US")
                        .build().toString());
                HttpURLConnection connection = (HttpURLConnection) popularMoviesUrl.openConnection();
                try {
                    InputStream in = connection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");
                    String response;
                    if (scanner.hasNext())
                        response = scanner.next();
                    else
                        return null;

                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        JSONArray moviesArray = JSONResponse.getJSONArray("results");
                        List<Movie> tempMovies = new ArrayList<>();

                        for (int i = 0; i < moviesArray.length(); i++) {
                            JSONObject JSONMovie = moviesArray.getJSONObject(i);

                            int id = Integer.valueOf(JSONMovie.optString("id"));
                            String title = JSONMovie.optString("title");
                            String releaseDate = JSONMovie.optString("release_date");
                            String poster = URL_POSTERS_BASE + JSONMovie.optString("poster_path");
                            float voteAverage = Float.valueOf(JSONMovie.optString("vote_average"));
                            String overview = JSONMovie.optString("overview");

                            tempMovies.add(new Movie(id, title, releaseDate, poster, voteAverage, overview));
                        }
                        return tempMovies;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> tempMovies) {
            if (!popularMovies.isEmpty()) popularMovies.clear();
            popularMovies = tempMovies;
            if (popularMoviesListener != null)
                popularMoviesListener.onDownload(tempMovies);
        }
    }


    public static void getFavorite(Context context, OnFavoriteMoviesDownloadedListener onFavoriteMoviesDownloadedListener) {

        favoriteMoviesListener = onFavoriteMoviesDownloadedListener;
        dbHelper = new DBHelper(context);
        cv = new ContentValues();
        db = dbHelper.getWritableDatabase();

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            List<Movie> tempMovies = new ArrayList<>();

            do {
                tempMovies.add(new Movie(c.getInt(c.getColumnIndex(DBHelper.KEY_ID)),
                        c.getString(c.getColumnIndex(DBHelper.KEY_TITLE)),
                        c.getString(c.getColumnIndex(DBHelper.KEY_RELEASE_DATE)),
                        c.getString(c.getColumnIndex(DBHelper.KEY_POSTER)),
                        c.getFloat(c.getColumnIndex(DBHelper.KEY_VOTE_AVERAGE)),
                        c.getString(c.getColumnIndex(DBHelper.KEY_OVERVIEW))
                ));
            } while (c.moveToNext());

            if (!favoriteMovies.isEmpty()) favoriteMovies.clear();
            favoriteMovies = tempMovies;
            if (favoriteMoviesListener != null)
                favoriteMoviesListener.onDownload(tempMovies);
        } else {
            Log.i("FUCK", "НЕТ ФИЛЬМОВ!");
            if (favoriteMoviesListener != null)
                favoriteMoviesListener.onDownload(null);
        }
        c.close();
    }

    public interface OnFavoriteMoviesDownloadedListener {
        void onDownload(List<Movie> downloadedFavoriteMovies);
    }


    public static void getTopRated(OnTopRatedMoviesDownloadedListener onTopRatedMoviesDownloadedListener) {
        topRatedMoviesListener = onTopRatedMoviesDownloadedListener;
        new getTopRatedAsyncTask().execute();
    }

    public interface OnTopRatedMoviesDownloadedListener {
        void onDownload(List<Movie> downloadedTopRatedMovies);
    }

    private static class getTopRatedAsyncTask extends AsyncTask<OnTopRatedMoviesDownloadedListener, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(OnTopRatedMoviesDownloadedListener... listeners) {
            try {
                URL topRatedMoviesUrl = new URL(Uri.parse(URL_TOP_RATED_BASE).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIEDB_KEY)
                        .appendQueryParameter("language", "en-US")
                        .build().toString());
                HttpURLConnection connection = (HttpURLConnection) topRatedMoviesUrl.openConnection();
                try {
                    InputStream in = connection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");
                    String response;
                    if (scanner.hasNext())
                        response = scanner.next();
                    else
                        return null;

                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        JSONArray moviesArray = JSONResponse.getJSONArray("results");
                        List<Movie> tempMovies = new ArrayList<>();

                        for (int i = 0; i < moviesArray.length(); i++) {
                            JSONObject JSONMovie = moviesArray.getJSONObject(i);

                            int id = Integer.valueOf(JSONMovie.optString("id"));
                            String title = JSONMovie.optString("title");
                            String releaseDate = JSONMovie.optString("release_date");
                            String poster = URL_POSTERS_BASE + JSONMovie.optString("poster_path");
                            float voteAverage = Float.valueOf(JSONMovie.optString("vote_average"));
                            String overview = JSONMovie.optString("overview");

                            tempMovies.add(new Movie(id, title, releaseDate, poster, voteAverage, overview));
                        }
                        return tempMovies;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> tempMovies) {
            if (!topRatedMovies.isEmpty()) topRatedMovies.clear();
            topRatedMovies = tempMovies;
            if (topRatedMoviesListener != null)
                topRatedMoviesListener.onDownload(tempMovies);
        }
    }


    public static void getTrailerkey(int movieId, OnTrailerKeyDownloadedListener OnTrailerKeyDownloadedListener) {
        trailerKeyListener = OnTrailerKeyDownloadedListener;
        new getTrailerKeyAsyncTask().execute(movieId);
    }

    public interface OnTrailerKeyDownloadedListener {
        void onDownload(String trailerKey);
    }

    private static class getTrailerKeyAsyncTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... movieIds) {
            try {
                URL trailerKeyUrl = new URL(Uri.parse(URL_TRAILERS_BASE + movieIds[0] + "/videos").buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIEDB_KEY)
                        .appendQueryParameter("language", "en-US")
                        .build().toString());
                HttpURLConnection connection = (HttpURLConnection) trailerKeyUrl.openConnection();
                try {
                    InputStream in = connection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");
                    String response;
                    if (scanner.hasNext())
                        response = scanner.next();
                    else
                        return null;

                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        JSONArray moviesArray = JSONResponse.getJSONArray("results");
                        JSONObject JSONTrailer = moviesArray.getJSONObject(0);
                        return JSONTrailer.optString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String trailerKey) {
            if (trailerKeyListener != null && !trailerKey.isEmpty())
                trailerKeyListener.onDownload(trailerKey);
        }
    }


    public static void getReviews(int movieId, OnReviewsDownloadedListener OnReviewsDownloadedListener) {
        reviewsListener = OnReviewsDownloadedListener;
        new getReviewsAsyncTask().execute(movieId);
    }

    public interface OnReviewsDownloadedListener {
        void onDownload(List<Review> reviews);
    }

    private static class getReviewsAsyncTask extends AsyncTask<Integer, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(Integer... movieIds) {
            try {
                URL reviewsUrl = new URL(Uri.parse(URL_REVIEWS_BASE + movieIds[0] + "/reviews").buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIEDB_KEY)
                        .appendQueryParameter("language", "en-US")
                        .build().toString());
                HttpURLConnection connection = (HttpURLConnection) reviewsUrl.openConnection();
                try {
                    InputStream in = connection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");
                    String response;
                    if (scanner.hasNext())
                        response = scanner.next();
                    else
                        return null;

                    try {
                        JSONObject JSONResponse = new JSONObject(response);
                        JSONArray reviewsArray = JSONResponse.getJSONArray("results");
                        List<Review> tempReviews = new ArrayList<>();

                        for (int i = 0; i < reviewsArray.length(); i++) {
                            JSONObject JSONReview = reviewsArray.getJSONObject(i);

                            String author = JSONReview.optString("author");
                            String content = JSONReview.optString("content");

                            tempReviews.add(new Review(author, content));
                        }
                        return tempReviews;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> tempReviews) {
            if (!reviews.isEmpty()) reviews.clear();
            reviews = tempReviews;
            if (reviewsListener != null)
                reviewsListener.onDownload(tempReviews);
        }
    }
}
