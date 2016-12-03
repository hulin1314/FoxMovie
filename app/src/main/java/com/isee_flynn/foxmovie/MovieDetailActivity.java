package com.isee_flynn.foxmovie;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mTitleTV;
    private ImageView mBackdropPathIV;
    private TextView mReleaseDateTV;
    private TextView mVoteAverageTV;
    private TextView mOverviewTV;
    private MovieEntity movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

//        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

//        actionBar.setTitle("电影详情");
        Bundle bundle = getIntent().getExtras();
        movie = (MovieEntity) bundle.getSerializable("movie");
        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initView() {
        mTitleTV = (TextView) findViewById(R.id.title);
        mBackdropPathIV = (ImageView) findViewById(R.id.backdrop_path);
        mReleaseDateTV = (TextView) findViewById(R.id.release_date);
        mVoteAverageTV = (TextView) findViewById(R.id.vote_average);
        mOverviewTV = (TextView) findViewById(R.id.overview);
        mTitleTV.setText(movie.getTitle());
        mReleaseDateTV.setText(movie.getRelease_date());
        mVoteAverageTV.setText(String.valueOf(movie.getVote_average()));
        mOverviewTV.setText(movie.getOverview());
        Picasso.with(this)
                .load(Consts.IMAGEURL + movie.getBackdrop_path())
                //                .transform(transformation)
                .placeholder(R.mipmap.ic_launcher)
                .into(mBackdropPathIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        //                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        mBackdropPathIV.setImageResource(R.mipmap.ic_launcher);
                    }
                });
    }
}
