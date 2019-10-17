package com.hadar.assignment3;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;

public class AddMovieActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText scoreEditText;
    private EditText actorsEditText;
    private EditText urlEditText;
    private Button createButton;
    public static final String MOVIE_NAME = "movie_name";
    public static final String MOVIE_SCORE = "movie_score";
    public static final String MOVIE_ACTORS = "movie_actors";
    public static final String MOVIE_URL = "movie_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        setTitle("Create a new movie");

        nameEditText = findViewById(R.id.nameEditText);
        scoreEditText = findViewById(R.id.scoreEditText);
        actorsEditText = findViewById(R.id.actorsEditText);
        urlEditText = findViewById(R.id.urlEditText);
        createButton = findViewById(R.id.create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie newMovie = new Movie(
                        nameEditText.getText().toString(),
                        Integer.valueOf(scoreEditText.getText().toString()),
                        actorsEditText.getText().toString(),
                        urlEditText.getText().toString());

                addMovieToService(newMovie);
                addMovieToAdapter(newMovie);

                finish();
            }
        });
    }

    private void addMovieToService(final Movie newMovie) {

        Gson gson = new Gson();

        String bodyJson = gson.toJson(newMovie);

        OkHttpClient client = new OkHttpClient();
        String url = "https://assignment3herokunew.herokuapp.com/movie";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                        Log.e("qwe", "onResponse: body = " + response.body().string() + newMovie.getName());
                if (response.isSuccessful()) {
//                            Log.e("qwe", "onResponse: " + "SUCCEED" + newMovie.getName());
                } else {
//                            Log.e("qwe", "onResponse: " + "FAILED" + newMovie.getName());
                }
            }
        });
    }

    private void addMovieToAdapter(Movie newMovie) {
        Intent i = new Intent();
        i.putExtra(MOVIE_NAME, newMovie.getName());
        i.putExtra(MOVIE_SCORE, newMovie.getScore());
        i.putExtra(MOVIE_ACTORS, newMovie.getActors());
        i.putExtra(MOVIE_URL, newMovie.getImageURL());
        setResult(RESULT_OK, i);
    }
}
