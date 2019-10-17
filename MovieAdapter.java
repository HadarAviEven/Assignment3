package com.hadar.assignment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private Context mContext;
    private List<Movie> moviesList;

    public MovieAdapter(Context context, ArrayList<Movie> list) {
        super(context, 0, list);
        mContext = context;
        moviesList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate
                    (R.layout.oneitem_layout, parent, false);

        Movie currentMovie = moviesList.get(position);

        ImageView imageView = listItem.findViewById(R.id.movie_img);
        Picasso.get().load(currentMovie.getImageURL()).into(imageView);

        TextView nameTextView = listItem.findViewById(R.id.movie_name);
        nameTextView.setText(currentMovie.getName());

        TextView scoreEditText = listItem.findViewById(R.id.movie_score);
        scoreEditText.setText(String.valueOf(currentMovie.getScore()));

        TextView actorsEditText = listItem.findViewById(R.id.movie_actors);
        actorsEditText.setText(currentMovie.getActors());

        return listItem;
    }

    public void removeItem(String movieNameForDelete) {
        int indexToRemove = 0;
        for (int i = 0; i < moviesList.size(); i++) {
            if (moviesList.get(i).getName().equals(movieNameForDelete)) {
                indexToRemove = i;
                break;
            }
        }
        moviesList.remove(indexToRemove);
        notifyDataSetChanged();
    }

    public void updateItem(Movie movie) {
        for (int i = 0; i < moviesList.size(); i++) {
            if (movie.getName().equals(moviesList.get(i).getName())) {
                moviesList.get(i).setScore(movie.getScore());
                moviesList.get(i).setActors(movie.getActors());
            }
        }
        notifyDataSetChanged();
    }

    public void createItem(Movie movie) {
        moviesList.add(movie);
        notifyDataSetChanged();
    }
}
