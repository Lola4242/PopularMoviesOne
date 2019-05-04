package android.example.popularmoviesone.model;

import java.util.List;

public class PopularMovieList {

    private final int page;
    private final List<Movie> results;
    private final int totalResults;
    private final int totalPages;

    public PopularMovieList(int page, List<Movie> results, int totalResults, int totalPages) {
        this.page = page;
        this.results = results;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }

}
