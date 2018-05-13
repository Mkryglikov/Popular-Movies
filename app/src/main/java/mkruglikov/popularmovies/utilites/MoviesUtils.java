package mkruglikov.popularmovies.utilites;

import android.net.Uri;
import android.os.AsyncTask;

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

public class MoviesUtils {
    private static OnPopularMoviesDownloadedListener popularMoviesListener;
    private static OnTopRatedMoviesDownloadedListener topRatedMoviesListener;
    private static List<Movie> popularMovies = new ArrayList<>();
    private static List<Movie> topRatedMovies = new ArrayList<>();

    private static final String URL_POPULAR_BASE = "https://api.themoviedb.org/3/movie/popular";
    private static final String URL_TOP_RATED_BASE = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String URL_POSTERS_BASE = "https://image.tmdb.org/t/p/w342";

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

                            String title = JSONMovie.optString("title");
                            String releaseDate = JSONMovie.optString("release_date");
                            String poster = URL_POSTERS_BASE + JSONMovie.optString("poster_path");
                            float voteAverage = Float.valueOf(JSONMovie.optString("vote_average"));
                            String overview = JSONMovie.optString("overview");

                            tempMovies.add(new Movie(title, releaseDate, poster, voteAverage, overview));
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

                            String title = JSONMovie.optString("title");
                            String releaseDate = JSONMovie.optString("release_date");
                            String poster = URL_POSTERS_BASE + JSONMovie.optString("poster_path");
                            float voteAverage = Float.valueOf(JSONMovie.optString("vote_average"));
                            String overview = JSONMovie.optString("overview");

                            tempMovies.add(new Movie(title, releaseDate, poster, voteAverage, overview));
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
}
