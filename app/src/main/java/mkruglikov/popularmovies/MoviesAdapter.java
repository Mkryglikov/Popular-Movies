package mkruglikov.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import mkruglikov.popularmovies.data.Movie;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    public static final String INTENT_EXTRA_KEY = "intent_bundle_key";
    private List<Movie> posters = new ArrayList<>();
    private final Context context;

    MoviesAdapter(Context context, List<Movie> posters) {
        this.posters = posters;
        this.context = context;
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
        Picasso picasso = Picasso.with(context);
        picasso.setLoggingEnabled(true);
        picasso.load(posters.get(position).getPoster()).into(holder.ivPosterItem);
        holder.ivPosterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(INTENT_EXTRA_KEY, posters.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    void setPosters(List<Movie> posters) {
        this.posters = posters;
        notifyDataSetChanged();
    }
}
