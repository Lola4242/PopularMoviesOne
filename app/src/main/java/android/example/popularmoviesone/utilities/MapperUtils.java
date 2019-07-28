package android.example.popularmoviesone.utilities;

import android.example.popularmoviesone.database.MovieEntry;
import android.example.popularmoviesone.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MapperUtils {

    public static List<Movie> MovieDaoListToMovieList(List<MovieEntry> movieEntries){

        List<Movie> movies = new ArrayList<>();

        for(MovieEntry movieEntry : movieEntries){
            movies.add(new Movie(movieEntry.getPosterPath(), movieEntry.isAdult(), movieEntry.getOverview(),
                    movieEntry.getReleaseDate(), null, movieEntry.getId(),
                    movieEntry.getOriginalTitle(), movieEntry.getOriginalLanguage(),movieEntry.getTitle(),
                    movieEntry.getBackdropPath(),movieEntry.getPopularity(),
                    movieEntry.getVoteCount(), movieEntry.isVideo(), movieEntry.getVoteAverage()));
        }

        return movies;
    }

    public static MovieEntry MovieToMovieEntry(Movie movie){
        return new MovieEntry(movie.getPosterPath(), true, movie.getOverview(),
                movie.getReleaseDate(), movie.getId(),
                null, null, movie.getTitle(),
                movie.getBackdropPath(), 0L,
                0, movie.getVideo(), movie.getVoteAverage());
    }
}
