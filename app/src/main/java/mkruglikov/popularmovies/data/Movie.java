package mkruglikov.popularmovies.data;

import org.parceler.Parcel;

@Parcel
public class Movie {
    int id;
    String title;
    String releaseDate;
    String poster;
    String overview;
    float voteAverage;


    public int getId() {
        return id;
    }

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

    public Movie(int id, String title, String releaseDate, String poster, float voteAverage, String overview) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.poster = poster;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    // Empty constructor needed by the Parceler library
    public Movie() {
    }
}
