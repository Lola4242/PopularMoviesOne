package android.example.popularmoviesone.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.example.popularmoviesone.utilities.SecretKeys.API_KEY;

/**
 * There utilities will be used to communicate with the moviedb server
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /* Pass a ISO 639-1 value to display translated data for the fields that support it.*/
    private static final String language = "en-US";
    /* Specify which page to query. */
    private static final String firstPage = "1";


    private final static String API_KEY_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";
    private final static String PAGE_PARAM = "page";


    /**
     * Builds the URL used to talk to the moviedb server using a api_key.
     *
     * @return The URL to use to query the weather server.
     */
    public static URL buidlUrl(String movieUrl){

        Uri buildUri = Uri.parse(movieUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, firstPage)
                .build();

        URL url = null;

        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e ){
            e.printStackTrace();
        }

        Log.v(TAG, "Build URI" + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
