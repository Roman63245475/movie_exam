package easv.my_tunes.be;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String category;
    private int time;
    private String path;

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
    public String getTime(){
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public int getTimeInt(){
        return time;
    }
}