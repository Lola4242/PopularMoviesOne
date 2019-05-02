package android.example.popularmoviesone;

import android.example.popularmoviesone.model.PopularMovieList;
import android.example.popularmoviesone.utilities.MovieDbJsonUtils;
import android.example.popularmoviesone.utilities.NetworkUtils;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclesView;
    private MovieAdapter mMovieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclesView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclesView.setLayoutManager(layoutManager);

        mRecyclesView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclesView.setAdapter(mMovieAdapter);

        new FetchMovieTask().execute();

    }

    @Override
    public void onClick(String movieDetails){
        Log.d(TAG, "onClick: "+movieDetails);
    }

    class FetchMovieTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buidlUrl();

            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);


                PopularMovieList popularMovieList = MovieDbJsonUtils.parsePopMovieListJson(jsonMovieResponse);

                String[] movieTitles = new String[Objects.requireNonNull(popularMovieList).getResults().size()];

                for(int i = 0; i< popularMovieList.getResults().size(); i++){
                    movieTitles[i] = popularMovieList.getResults().get(i).getTitle();

                }
                Log.d(TAG, jsonMovieResponse);

                return movieTitles;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] titleData) {
            if (titleData != null) {
                mMovieAdapter.setMovieDataa(titleData);

            }
        }
    }


}
