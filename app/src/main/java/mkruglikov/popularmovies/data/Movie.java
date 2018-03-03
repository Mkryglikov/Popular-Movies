package mkruglikov.popularmovies.data;

import java.io.Serializable;

public class Movie implements Serializable{
    private final String title;
    private final String releaseDate;
    private final String poster;
    private final String overview;
    private final float voteAverage;


    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPoster() {
        return poster;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public Movie(String title, String releaseDate, String poster, float voteAverage, String overview) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }
}
