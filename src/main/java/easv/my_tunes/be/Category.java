package easv.my_tunes.be;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private List<Movie> movies;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public int getID() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getAmount(){
        return movies.size();
    }
    public String getTime(){
        int time = 0;
        for (Movie song : movies){
            time += song.getTimeInt();
        }
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        int second = time % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public List<Movie> getSongsList(){
        return movies;
    }
}
