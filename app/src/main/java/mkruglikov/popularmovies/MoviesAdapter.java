package mkruglikov.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    List<String> posters;
    Context context;

    MoviesAdapter(Context context, List<String> posters) {
        this.posters = posters;
        this.context = context;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPosterItem;

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
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {
        Picasso picasso = Picasso.with(context);
        picasso.setLoggingEnabled(true);
        picasso.load(posters.get(position)).into(holder.ivPosterItem);
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    void setPosters(List<String> posters) {
        this.posters = posters;
        notifyDataSetChanged();
    }
}
