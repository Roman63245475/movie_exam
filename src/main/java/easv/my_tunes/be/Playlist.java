package easv.my_tunes.be;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int id;
    private String name;
    private List<Song> songs;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public int getID() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getSongs(){
        return songs.size();
    }
    public String getTime(){
        int time = 0;
        for (Song song : songs){
            time += song.getTimeInt();
        }
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        int second = time % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public ArrayList<String> getNamesOfSongs(){
        ArrayList<String> names = new ArrayList<>();
        for (Song song : songs){
            names.add(song.getTitle());
        }
        return names;
    }
}