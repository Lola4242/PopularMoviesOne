package android.example.popularmoviesone;

import android.content.Context;
import android.example.popularmoviesone.database.AppDatabase;
import android.example.popularmoviesone.database.MovieEntry;
import android.example.popularmoviesone.model.Movie;
import android.example.popularmoviesone.model.PopularMovieList;
import android.example.popularmoviesone.utilities.MapperUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private PopularMovieList mMovieData;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MovieAdapterOnClickHandler mClickHandler;

    private AppDatabase mDb;



    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movieDetails);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }


    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        mDb = AppDatabase.getInstance(context);

        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final MovieAdapterViewHolder movieAdapterViewHolder, int position) {

        final Movie movieDetails = mMovieData.getResults().get(position);

        Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movieDetails.getPosterPath()).into(movieAdapterViewHolder.mMovieImageView);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movieEntry = mDb.movieDao().loadMovieById(movieDetails.getId());
                movieAdapterViewHolder.mStarButton.setChecked(movieEntry!=null);

            }
        });



    }

    @Override
    public int getItemCount() {
        if(mMovieData == null){
            return 0;
        }
        return mMovieData.getResults().size();
    }

    /**
     * This method is used to set the movie details on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param movieData The new weather data to be displayed.
     */
    public void setMovieDataa(PopularMovieList movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a movie list item
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mMovieImageView;
        final CheckBox mStarButton;

        MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.image_iv);
            mStarButton = itemView.findViewById(R.id.star_bt);
            mMovieImageView.setOnClickListener(this);
            mStarButton.setOnClickListener(this);

        }




        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            final Movie movieDetails = mMovieData.getResults().get(adapterPosition);

            switch (v.getId()) {
                case R.id.image_iv:
                    mClickHandler.onClick(movieDetails);
                    break;
                case R.id.star_bt:
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mStarButton.isChecked()) {
                                // insert new movie to favorites
                                mDb.movieDao().insertMovie(MapperUtils.MovieToMovieEntry(movieDetails));
                            } else {
                                // delete movie from favorites
                                mDb.movieDao().deleteMovie(MapperUtils.MovieToMovieEntry(movieDetails));
                            }
                        }
                    });
                    break;
            }

        }
    }
}
