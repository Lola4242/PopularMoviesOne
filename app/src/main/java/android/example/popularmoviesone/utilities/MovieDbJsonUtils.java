package android.example.popularmoviesone.utilities;


import android.example.popularmoviesone.model.Movie;
import android.example.popularmoviesone.model.PopularMovieList;
import android.example.popularmoviesone.model.Review;
import android.example.popularmoviesone.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MovieDbJsonUtils {

    public static List<Trailer> parseTrailerListJson(String json){

        final String RESULT = "results";
        final String KEY = "key";



        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultsJson = jsonObject.getJSONArray(RESULT);


            List<Trailer> trailers = new ArrayList<>();


            for (int i = 0; i < resultsJson.length(); i++) {
                JSONObject trailer = resultsJson.getJSONObject(i);

                String key = trailer.getString(KEY);
                trailers.add(new Trailer(key));
            }

            return trailers;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method parses JSON and returns an Movie Object
     * containing the different properties of a movie.
     *
     * @param json JSON string
     * @return Movie object describing a movie
     */

    public static PopularMovieList parsePopMovieListJson(String json){
        final String PAGE = "page";
        final String RESULTS = "results";
        final String TOTAL_RESULTS = "total_results";
        final String TOTAL_PAGES = "total_pages";
        final String POSTER_PATH = "poster_path";
        final String ADULT = "adult";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String GENRE_IDS = "genre_ids";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String ORIGINAL_LANGUAGE = "original_language";
        final String TITLE = "title";
        final String BACKDROP_PATH = "backdrop_path";
        final String POPULARITY = "popularity";
        final String VOTE_COUNT = "vote_count";
        final String VIDEO = "video";
        final String VOTE_AVERAGE = "vote_average";

        try{
            /* Get the JSON object representing the sandwich */
            JSONObject jsonObject = new JSONObject(json);

            int page = jsonObject.getInt(PAGE);
            int totalResults = jsonObject.getInt(TOTAL_RESULTS);
            int totoalPages = jsonObject.getInt(TOTAL_PAGES);

            JSONArray resultsJson = jsonObject.getJSONArray(RESULTS);
            List<Movie> results = new ArrayList<>();
            for(int i = 0; i< resultsJson.length(); i++){
                JSONObject movie = resultsJson.getJSONObject(i);

                String posterPath = movie.getString(POSTER_PATH);
                boolean adult = movie.getBoolean(ADULT);
                String overview = movie.getString(OVERVIEW);
                String releaseDate = movie.getString(RELEASE_DATE);
                JSONArray genreIdsJson = movie.getJSONArray(GENRE_IDS);
                List<Integer> genreIds = new ArrayList<>();
                for(int j = 0; j<genreIdsJson.length(); j++){
                    genreIds.add(genreIdsJson.getInt(j));
                }
                int id = movie.getInt(ID);
                String originalTitle = movie.getString(ORIGINAL_TITLE);
                String orignalLanguage = movie.getString(ORIGINAL_LANGUAGE);
                String title = movie.getString(TITLE);
                String backdropPath = movie.getString(BACKDROP_PATH);
                int popularity = movie.getInt(POPULARITY);
                int voteCount = movie.getInt(VOTE_COUNT);
                boolean video = movie.getBoolean(VIDEO);
                long voteAvarage = movie.getLong(VOTE_AVERAGE);

                Movie newMovie = new Movie(posterPath, adult, overview, releaseDate, genreIds, id, originalTitle, orignalLanguage, title, backdropPath, popularity, voteCount, video, voteAvarage);

                results.add(newMovie);
            }

            return new PopularMovieList(page, results, totalResults, totoalPages);



        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static Review[] parseReviewListJson(String json) {

        final String ID = "id";
        final String RESULTS = "results";
        final String ID_RESULT = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            int id = jsonObject.getInt(ID);

            JSONArray resultsJson = jsonObject.getJSONArray(RESULTS);

            Review[] reviews = new Review[resultsJson.length()];

            for(int i = 0; i< resultsJson.length(); i++) {
                JSONObject movie = resultsJson.getJSONObject(i);

                String idResult = movie.getString(ID_RESULT);
                String author = movie.getString(AUTHOR);
                String content = movie.getString(CONTENT);
                String url = movie.getString(URL);
                reviews[i] = new Review(idResult, author, content, url);
            }
            return reviews;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;

    }
}
