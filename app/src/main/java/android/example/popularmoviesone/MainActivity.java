package android.example.popularmoviesone;

import android.example.popularmoviesone.model.PopularMovieList;
import android.example.popularmoviesone.utilities.MovieDbJsonUtils;
import android.example.popularmoviesone.utilities.NetworkUtils;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchMovieTask().execute();

    }

    class FetchMovieTask extends AsyncTask<String, Void, String[]>{

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

                return movieTitles;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] titleData) {
            if (titleData != null) {


                //TODO change recycler view
                final ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, titleData);

                // Simplification: Using a ListView instead of a RecyclerView
                ListView listView = findViewById(R.id.recyclerview_movies);
                listView.setAdapter(adapter);

            }
        }
    }


}
