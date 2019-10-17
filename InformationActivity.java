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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class InformationActivity extends AppCompatActivity {

    private TextView nameTextView;
    private EditText scoreEditText;
    private EditText actorsEditText;
    private Button updateButton;
    private ImageView img;
    public static final String MOVIE_NAME = "movie_name";
    public static final String NEW_SCORE = "new_score";
    public static final String NEW_ACTORS = "new_actors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        setTitle("Edit data movie");

        nameTextView = findViewById(R.id.nameTextView);
        scoreEditText = findViewById(R.id.scoreEditText);
        actorsEditText = findViewById(R.id.actorsEditText);
        updateButton = findViewById(R.id.update_button);
        img = findViewById(R.id.image);

        initViews();
        update();
    }

    private void initViews() {
        nameTextView.setText(getName() != null ? getName() : "");
        scoreEditText.setText(String.valueOf(getScore()));
        actorsEditText.setText(getActors() != null ? getActors() : "");
        Picasso.get().load(getImageURL()).into(img);
    }

    private String getName() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForName);
        }
        return null;
    }

    private int getScore() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getIntExtra(MainActivity.keyForScore, 0);
        }
        return 0;
    }

    private String getActors() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForActors);
        }
        return null;
    }

    private String getImageURL() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getStringExtra(MainActivity.keyForImg);
        }
        return null;
    }

    private void update() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameTextView.getText().toString();
                int score = Integer.valueOf(scoreEditText.getText().toString());
                String actors = actorsEditText.getText().toString();

                updateService(name, score, actors);
                updateAdapter(name, score, actors);

                finish();
            }
        });
    }

    private void updateService(String name, int score, String actors) {

        Movie updatedMovie = new Movie(name, score, actors, getImageURL());

        Gson gson = new Gson();

        String bodyJson = gson.toJson(updatedMovie);

        OkHttpClient client = new OkHttpClient();
        String url = "https://assignment3herokunew.herokuapp.com/movie/" + name;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder().url(url).put(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
//                            Log.e("qwe", "onResponse: " + "SUCCEED" );
                } else {
//                            Log.e("qwe", "onResponse: " + "FAILED" );
                }
            }
        });
    }

    private void updateAdapter(String name, int score, String actors) {
        Intent i = new Intent();
        i.putExtra(MOVIE_NAME, name);
        i.putExtra(NEW_SCORE, score);
        i.putExtra(NEW_ACTORS, actors);
        setResult(RESULT_OK, i);
    }
}
