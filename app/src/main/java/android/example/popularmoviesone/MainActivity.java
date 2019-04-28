package android.example.popularmoviesone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {


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

    }


}
