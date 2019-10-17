package com.hadar.assignment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String keyForName = "KEY_FOR_NAME";
    public static final String keyForScore = "KEY_FOR_SCORE";
    public static final String keyForActors = "KEY_FOR_ACTORS";
    public static final String keyForImg = "KEY_FOR_IMG";
    public static final int REQUEST_CODE_FOR_UPDATE = 1;
    public static final int REQUEST_CODE_FOR_CREATE = 2;
    public ListView lv;
    public static ArrayList<Movie> myMoviesList = new ArrayList<>();
    public static List<HashMap<String, String>> fullDictionary = new ArrayList<>();
    public static Context context;
    public MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddMovieActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_CREATE);
            }
        });

        context = this;
        initializeMoviesList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void shortClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int itemPosition, long l) {
                HashMap<String, String> hashMap = fullDictionary.get(itemPosition);

                Intent intent = new Intent(getBaseContext(), InformationActivity.class);
                intent.putExtra(keyForName, hashMap.get(keyForName));
                intent.putExtra(keyForScore, Integer.valueOf(hashMap.get(keyForScore)));
                intent.putExtra(keyForActors, hashMap.get(keyForActors));
                intent.putExtra(keyForImg, String.valueOf(hashMap.get(keyForImg)));

                startActivityForResult(intent, REQUEST_CODE_FOR_UPDATE);
            }
        });
    }

    public void longClick() {
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,
                                           View view, final int itemPosition, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete '" + fullDictionary.get
                                (itemPosition).get(keyForName) + "'")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteFromService(fullDictionary.get(itemPosition)
                                        .get(keyForName));
                                deleteFromAdapter(fullDictionary.get(itemPosition)
                                        .get(keyForName));
                                fullDictionary.remove(itemPosition);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                return true;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_UPDATE) {
            if (resultCode == RESULT_OK) {

                String currentMovie = data.getStringExtra(InformationActivity.MOVIE_NAME);
                int scoreUpdate = data.getIntExtra(InformationActivity.NEW_SCORE, -1);
                String actorsUpdate = data.getStringExtra(InformationActivity.NEW_ACTORS);

                for (HashMap<String, String> movie : fullDictionary) {
                    if (movie.get(keyForName).equals(currentMovie)) {
                        movie.put(keyForScore, String.valueOf(scoreUpdate));
                        movie.put(keyForActors, actorsUpdate);

                        mAdapter.updateItem(new Movie(currentMovie, scoreUpdate,
                                actorsUpdate, ""));
                    }
                }
            }
        }

        if (requestCode == REQUEST_CODE_FOR_CREATE) {
            if (resultCode == RESULT_OK) {

                String movieName = data.getStringExtra(AddMovieActivity.MOVIE_NAME);
                int movieScore = data.getIntExtra(AddMovieActivity.MOVIE_SCORE, -1);
                String movieActors = data.getStringExtra(AddMovieActivity.MOVIE_ACTORS);
                String movieUrl = data.getStringExtra(AddMovieActivity.MOVIE_URL);

                HashMap<String, String> dic = new HashMap<>();
                dic.put(keyForName, movieName);
                dic.put(keyForScore, String.valueOf(movieScore));
                dic.put(keyForActors, movieActors);
                dic.put(keyForImg, movieUrl);
                fullDictionary.add(dic);

                mAdapter.createItem(new Movie(movieName, movieScore,
                        movieActors, movieUrl));
            }
        }
    }

    public void initializeMoviesList() {
        // create client
        OkHttpClient client = new OkHttpClient();

        // create a request object
        String url = "https://assignment3herokunew.herokuapp.com/movie";

        Request request = new Request.Builder().url(url).build();

        // enqueue - add item to the request queue
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            // SUCCESS!!!
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createList(myResponse);
                        }
                    });
                }
            }
        });
    }

    public void createList(String myResponse) {

        myMoviesList = new ArrayList<>();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Movie[] moviesArray = gson.fromJson(myResponse, Movie[].class);

        for (int i = 0; i < moviesArray.length; i++) {

            myMoviesList.add(new Movie(
                    moviesArray[i].getName(),
                    moviesArray[i].getScore(),
                    moviesArray[i].getActors(),
                    moviesArray[i].getImageURL()));
        }

        for (Movie oneMovie : myMoviesList) {
            HashMap<String, String> dic = new HashMap<>();
            dic.put(keyForName, oneMovie.getName());
            dic.put(keyForScore, String.valueOf(oneMovie.getScore()));
            dic.put(keyForActors, oneMovie.getActors());
            dic.put(keyForImg, oneMovie.getImageURL());
            fullDictionary.add(dic);
        }

        lv = findViewById(R.id.moviesList);

        mAdapter = new MovieAdapter(this, myMoviesList);
        lv.setAdapter(mAdapter);

        shortClick();
        longClick();
    }

    public void deleteFromService(String movieNameForDelete) {

        final String name = movieNameForDelete;

        OkHttpClient client = new OkHttpClient();
        String url = "https://assignment3herokunew.herokuapp.com/movie/" + name;

        Request request = new Request.Builder().url(url).delete().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("qwe", "onResponse: body = " + response.body().string() + name);
                if (response.isSuccessful()) {
//                    Log.e("qwe", "onResponse: " + "SUCCEED " + name);
                } else {
//                    Log.e("qwe", "onResponse: " + "FAILED " + name);
                }
            }
        });
    }

    public void deleteFromAdapter(String movieNameForDelete) {
        mAdapter.removeItem(movieNameForDelete);
    }
}
