package mkruglikov.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mkruglikov.popularmovies.data.Movie;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    static final String INTENT_EXTRA_KEY = "intent_bundle_key";
    private List<Movie> movies = new ArrayList<>();
    private OnMovieClickListener listener;
    private final Context context;

    MoviesAdapter(Context context, List<Movie> movies, OnMovieClickListener listener) {
        this.movies = movies;
        this.context = context;
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPosterItem;

        ViewHolder(View itemView) {
            super(itemView);
            ivPosterItem = itemView.findViewById(R.id.ivPosterItem);
        }
    }

    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesAdapter.ViewHolder holder, int position) {
<<<<<<< HEAD
        Picasso picasso = Picasso.with(context);
        picasso.setLoggingEnabled(true);
        picasso.load(movies.get(position).getPoster()).into(holder.ivPosterItem);
=======
        Glide.with(context)
                .load(movies.get(position).getPoster())
                .into(holder.ivPosterItem);
>>>>>>> dev

        holder.ivPosterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMovieClick(movies.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void setMovies(List<Movie> movies) {
        if (!this.movies.equals(movies)) {
            this.movies = movies;
            notifyDataSetChanged();
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }
}
