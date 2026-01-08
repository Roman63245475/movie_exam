package easv.my_movies.be;

public class Movie {
    private int id;
    private String name;
    private int time;
    private String path;
    private int movie_category_id;
    private int rating;

    public Movie(int id, String name, int time, String path, int movie_category_id, String empty) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.path = path;
        this.movie_category_id = movie_category_id;
    }

    public int getCategory_Movie_id() {
        return movie_category_id;
    }
    public int getRating(){
        return this.rating;
    }

    public Movie(int id, String name, int time, String path, int rating) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.path = path;
        this.rating = rating;
    }

    public int getID() {
        return id;
    }

    public String getName(){
        return name;
    }
    public String getTime() {
        if (time < 0) {
            return "00:00";
        }
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public int getTimeInt(){
        return time;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getPath(){
        return path;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public void setTime(int time) {
        this.time = time;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
