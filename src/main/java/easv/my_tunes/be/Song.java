package easv.my_tunes.be;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String category;
    private int time;
    private String path;
    private int playlist_song_id;

    public Song(int id, String title, String artist, String category, int time,  String path,  int playlist_song_id) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.path = path;
        this.playlist_song_id = playlist_song_id;
    }

    public int getPlaylist_song_id() {
        return playlist_song_id;
    }

    public Song(int id, String title, String artist, String category, int time,  String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.path = path;
    }

    public int getID() {
        return id;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getCategory(){
        return category;
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
        return title;
    }

    public String getPath(){
        return path;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public void setPath(String path) {
        this.path = path;
    }
}