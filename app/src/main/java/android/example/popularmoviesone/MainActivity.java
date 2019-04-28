package android.example.popularmoviesone;

import android.example.popularmoviesone.utilities.NetworkUtils;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO change recycler view
        String[] movies = getResources().getStringArray(R.array.mock_data);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, movies);

        // Simplification: Using a ListView instead of a RecyclerView
        ListView listView = findViewById(R.id.recyclerview_movies);
        listView.setAdapter(adapter);

        new FetchMovieTask().execute();

    }

    class FetchMovieTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buidlUrl();

            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                Log.v(TAG, jsonMovieResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new String[0];
        }
    }


}
