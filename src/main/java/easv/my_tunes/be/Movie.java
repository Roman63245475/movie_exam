package easv.my_tunes.be;

public class Movie {
    private int id;
    private String name;
    private int time;
    private String path;
    private int movie_category_id;

    public Movie(int id, String name, int time, String path, int movie_category_id) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.path = path;
        this.movie_category_id = movie_category_id;
    }

    public int getPlaylist_song_id() {
        return movie_category_id;
    }

    public Movie(int id, String name, int time, String path) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.path = path;
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
