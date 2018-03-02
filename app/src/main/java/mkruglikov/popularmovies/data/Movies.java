package mkruglikov.popularmovies.data;

import java.util.LinkedList;
import java.util.List;

public class Movies {
    private static List<String> posters = new LinkedList<>();

    public static List<String> getPopular() {
        if (!posters.isEmpty()) posters.clear();
        for (int i = 0; i < 3; i++) {
            posters.add("https://image.tmdb.org/t/p/w342//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        }
        return posters;
    }

    public static List<String>  getTopRated() {
        if (!posters.isEmpty()) posters.clear();
        for (int i = 0; i < 6; i++) {
            posters.add("https://image.tmdb.org/t/p/w342//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        }
        return posters;
    }
}
