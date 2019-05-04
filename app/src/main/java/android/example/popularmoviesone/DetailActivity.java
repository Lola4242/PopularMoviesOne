package android.example.popularmoviesone;

import android.content.Intent;
import android.example.popularmoviesone.model.Movie;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    Movie movie;

    ImageView iMovieBackDrop;

    TextView tMovieTitle;
    TextView tMovieDetails;
    TextView tMovieReleaseDate;
    TextView tMovieVoteAverage;

    public static final String EXTRA_MOVIE= "extra_movie";

    public static final String URL = "https://image.tmdb.org/t/p/w185/";
    public static final String GIVEN_DATE = "yyyy-MM-dd";
    public static final String NICE_DATE = "dd MMM yyyy";
    public static final String RATING = " / 10";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iMovieBackDrop = findViewById(R.id.iv_back_drop);
        tMovieTitle = findViewById(R.id.tv_title);
        tMovieDetails = findViewById(R.id.tv_details);
        tMovieReleaseDate = findViewById(R.id.tv_release_date);
        tMovieVoteAverage = findViewById(R.id.tv_voteAverage);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        if(!intent.hasExtra(EXTRA_MOVIE)){
            closeOnError();
            return;

        }

        movie = intent.getExtras().getParcelable(EXTRA_MOVIE);

        if(movie == null){
            closeOnError();
            return;
        }

        populateUI();

    }

    private void populateUI() {
        Picasso.get().load(URL + movie.getBackdropPath()).into(iMovieBackDrop);
        tMovieTitle.setText(movie.getTitle());
        tMovieDetails.setText(movie.getOverview());

        Date releaseDate;
        String releaseDateText = "";
        try {
            releaseDate=new SimpleDateFormat(GIVEN_DATE).parse(movie.getReleaseDate());
            releaseDateText = new SimpleDateFormat(NICE_DATE, Locale.ENGLISH).format(releaseDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        tMovieReleaseDate.setText(releaseDateText);
        tMovieVoteAverage.setText(movie.getVoteAverage() + RATING);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}
