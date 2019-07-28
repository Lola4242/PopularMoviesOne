package android.example.popularmoviesone;

import android.content.Context;

import android.example.popularmoviesone.model.Review;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private Review[] mReviewData;




    /**
     * Creates a Reviewdapter.
     *

     */
    public ReviewAdapter(){
    }


    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);


        return new ReviewAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param ReviewAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {

        final Review reviewDetails = mReviewData[position];
        reviewAdapterViewHolder.mReviewTextview.setText(reviewDetails.getContent());
    }

    @Override
    public int getItemCount() {
        if(mReviewData == null){
            return 0;
        }
        return mReviewData.length;
    }

    /**
     * This method is used to set the movie details on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param movieData The new weather data to be displayed.
     */
    public void setReviewData(Review[] reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a movie list item
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView mReviewTextview;

        ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mReviewTextview = itemView.findViewById(R.id.tv_review_data);


        }
    }
}
