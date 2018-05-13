package mkruglikov.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mkruglikov.popularmovies.data.Review;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<Review> reviews = new ArrayList<>();
    private final Context context;

    ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvReviewContent;
        final TextView tvReviewAuthor;

        ViewHolder(View itemView) {
            super(itemView);
            tvReviewContent = itemView.findViewById(R.id.tvReviewContent);
            tvReviewAuthor = itemView.findViewById(R.id.tvReviewAuthor);
        }
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.review_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapter.ViewHolder holder, int position) {
        holder.tvReviewAuthor.setText(reviews.get(position).getAuthor());
        holder.tvReviewContent.setText(reviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}
