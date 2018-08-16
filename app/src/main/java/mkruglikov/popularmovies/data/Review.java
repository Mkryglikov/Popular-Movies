package mkruglikov.popularmovies.data;

import org.parceler.Parcel;

@Parcel
public class Review {
    String author;
    String content;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public Review() {
        // For Parceler
    }
}
