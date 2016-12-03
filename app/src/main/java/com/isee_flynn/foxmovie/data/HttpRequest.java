//package com.isee_flynn.foxmovie.data;
//
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//import com.isee_flynn.foxmovie.BuildConfig;
//import com.isee_flynn.foxmovie.Consts;
//import com.isee_flynn.foxmovie.MovieEntity;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//
///**
// * 创建人： flynn
// * 创建日期： 2016/12/3.
// */
//public class HttpRequest extends AsyncTask<String, Integer, String> {
//
//    @Override
//    protected String doInBackground(String... params) {
//
//        if (params == null || params.length == 0) {
//            return "";
//        }
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//
//        // Will contain the raw JSON response as a string.
//        String forecastJsonStr = null;
//
//        try {
//            String baseUrl = Consts.BASEURL + params[0] + "?";
//            String apiKey = BuildConfig.FOXMOVIE_API_KEY;
//            Uri uri = Uri.parse(baseUrl).buildUpon()
//                    .appendQueryParameter("language", "zh")
//                    .appendQueryParameter("api_key", apiKey).build();
//            URL url = new URL(uri.toString());
//            Log.i(Consts.LOGTAG, "flynn >>>   url = " + uri.toString());
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//            // Read the input stream into a String
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuilder buffer = new StringBuilder();
//            if (inputStream == null) {
//                // Nothing to do.
//                return null;
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                // But it does make debugging a *lot* easier if you print out the completed
//                // buffer for debugging.
//                buffer.append(line + "\n");
//            }
//
//            if (buffer.length() == 0) {
//                // Stream was empty.  No point in parsing.
//                return null;
//            }
//            forecastJsonStr = buffer.toString();
//        } catch (IOException e) {
//            Log.e("mainActivity", "Error ", e);
//            return null;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e("mainActivity", "Error closing stream", e);
//                }
//            }
//        }
//        return forecastJsonStr;
//    }
//
//    @Override
//    protected void onPostExecute(String str) {
//        super.onPostExecute(str);
//        if (str == null || "".equals(str)) {
//            return;
//        }
//        try {
//            JSONObject obj = new JSONObject(str);
//            JSONArray ary = obj.optJSONArray("results");
//            ArrayList<MovieEntity> list = new ArrayList<>();
//            if (ary != null) {
//                for (int i = 0; i < ary.length(); i++) {
//                    JSONObject o = ary.optJSONObject(i);
//                    MovieEntity movie = new MovieEntity();
//                    movie.setBackdrop_path(o.optString("backdrop_path"));
//                    movie.setTitle(o.optString("title"));
//                    movie.setRelease_date(o.optString("release_date"));
//                    movie.setVote_average(o.optDouble("vote_average"));
//                    movie.setOverview(o.optString("overview"));
//                    list.add(movie);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
