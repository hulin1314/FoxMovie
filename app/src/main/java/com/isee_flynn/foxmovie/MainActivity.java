package com.isee_flynn.foxmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView mGridView;
    private ArrayList<MovieEntity> mData;
    private MovieListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //        new HttpRequest().execute("popular");
        requestData();
        //        String str = BuildConfig.FOXMOVIE_API_KEY;
    }

    private void requestData() {
        if (!isOnline()) {
            Toast.makeText(this, "没有网络", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String str = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
        new HttpRequest().execute(str);
    }

    private void initView() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("info", "landscape");
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("info", "portrait");
        }

        mGridView = (GridView) findViewById(R.id.movie_gridView);
        mData = new ArrayList<>();
        mAdapter = new MovieListAdapter(this, mData);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie", mData.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void update(ArrayList<MovieEntity> list) {
        mData.clear();
        mData.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.refresh) {
            requestData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class HttpRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            if (params == null || params.length == 0) {
                return "";
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                String baseUrl = Consts.BASEURL + params[0] + "?";
                String apiKey = BuildConfig.FOXMOVIE_API_KEY;
                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("language", "zh")
                        .appendQueryParameter("api_key", apiKey).build();
                URL url = new URL(uri.toString());
                Log.i(Consts.LOGTAG, "flynn >>>   url = " + uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                char[] chars = new char[1024];
//                String line;
//                while ((line = reader.readLine()) != null) {
                int length;
                while ((length = reader.read(chars)) != -1) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
//                    buffer.append(line + "\n");
                    buffer.append(chars,0,length);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("mainActivity", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("mainActivity", "Error closing stream", e);
                    }
                }
            }
            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (str == null || "".equals(str)) {
                return;
            }
            try {
                JSONObject obj = new JSONObject(str);
                JSONArray ary = obj.optJSONArray("results");
                ArrayList<MovieEntity> list = new ArrayList<>();
                if (ary != null) {
                    for (int i = 0; i < ary.length(); i++) {
                        JSONObject o = ary.optJSONObject(i);
                        MovieEntity movie = new MovieEntity();
                        movie.setBackdrop_path(o.optString("backdrop_path"));
                        movie.setTitle(o.optString("title"));
                        movie.setRelease_date(o.optString("release_date"));
                        movie.setVote_average(o.optDouble("vote_average"));
                        movie.setOverview(o.optString("overview"));
                        list.add(movie);
                    }
                }
                update(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
