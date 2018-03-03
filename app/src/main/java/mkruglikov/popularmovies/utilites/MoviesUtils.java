package mkruglikov.popularmovies.utilites;

import android.annotation.SuppressLint;
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

import mkruglikov.popularmovies.Keys;
import mkruglikov.popularmovies.data.Movie;

public class MoviesUtils {
    private static final List<Movie> posters = new ArrayList<>();

    private static final String URL_POPULAR = "https://api.themoviedb.org/3/movie/popular";
    private static final String URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String URL_POSTERS_BASE = "https://image.tmdb.org/t/p/w342";

    @SuppressLint("StaticFieldLeak")
    public static List<Movie> getPopular() {
        if (!posters.isEmpty()) posters.clear();

        try {
            return new AsyncTask<Void, Void, List<Movie>>() {
                @Override
                protected List<Movie> doInBackground(Void... params) {
                    try {
                        URL popularMoviesUrl = new URL(Uri.parse(URL_POPULAR).buildUpon()
                                .appendQueryParameter("api_key", Keys.MOVIEDB_KEY)
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
                                JSONObject JSONresponse = new JSONObject(response);
                                JSONArray moviesArray = JSONresponse.getJSONArray("results");


                                for (int i = 0; i < moviesArray.length(); i++) {
                                    JSONObject JSONMovie = moviesArray.getJSONObject(i);

                                    String title = JSONMovie.optString("title");
                                    String releaseDate = JSONMovie.optString("release_date");
                                    String poster = URL_POSTERS_BASE + JSONMovie.optString("poster_path");
                                    float voteAverage = Float.valueOf(JSONMovie.optString("vote_average"));
                                    String overview = JSONMovie.optString("overview");

                                    posters.add(new Movie(title, releaseDate, poster, voteAverage, overview));
                                }
                                return posters;
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
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static List<Movie> getTopRated() {
        if (!posters.isEmpty()) posters.clear();
        try {
            return new AsyncTask<Void, Void, List<Movie>>() {
                @Override
                protected List<Movie> doInBackground(Void... params) {
                    try {
                        URL popularMoviesUrl = new URL(Uri.parse(URL_TOP_RATED).buildUpon()
                                .appendQueryParameter("api_key", Keys.MOVIEDB_KEY)
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
                                JSONObject JSONresponse = new JSONObject(response);
                                JSONArray moviesArray = JSONresponse.getJSONArray("results");


                                for (int i = 0; i < moviesArray.length(); i++) {
                                    JSONObject JSONMovie = moviesArray.getJSONObject(i);

                                    String title = JSONMovie.optString("title");
                                    String releaseDate = JSONMovie.optString("release_date");
                                    String poster = URL_POSTERS_BASE + JSONMovie.optString("poster_path");
                                    float voteAverage = Float.valueOf(JSONMovie.optString("vote_average"));
                                    String overview = JSONMovie.optString("overview");

                                    posters.add(new Movie(title, releaseDate, poster, voteAverage, overview));
                                }
                                return posters;
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
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
