package easv.my_tunes.bll;

import easv.my_tunes.be.Playlist;
import easv.my_tunes.be.Song;
import easv.my_tunes.dal.PlayListAccessObject;
import easv.my_tunes.dal.Playlists_SongsAccessObject;
import easv.my_tunes.dal.SongsAccessObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class Logic {
    SongsAccessObject songsAccessObject;
    PlayListAccessObject playListAccessObject;
    Playlists_SongsAccessObject playLists_songs_AccessObject;

    public Logic() {
        songsAccessObject = new SongsAccessObject();
        playListAccessObject = new PlayListAccessObject();
        playLists_songs_AccessObject = new Playlists_SongsAccessObject();
    }

    public List<Song> loadSongs() {
        return songsAccessObject.getSongs();
    }

    public List<Playlist> loadPlaylists(){
        return playListAccessObject.getPlaylists();
    }

    public void saveSong(String title, String artist, String category, int time, File file) {
        Path targetPath = createFile(file);
        songsAccessObject.saveSong(title, artist, category, time, targetPath);
    }

    public void savePlayList(String name){
        playListAccessObject.savePlayList(name);
    }

    public void editPlaylist(String name, Playlist obj){
        playListAccessObject.editPlaylist(name, obj);
    }

    public void addSongToPlaylist(Playlist playlist, Song song) {
        playLists_songs_AccessObject.addSongToPlaylist(playlist, song);
    }

    public void editSong(String title, String artist, String category, int time, File file, Song obj) {
        Path targetPath = createFile(file);
        songsAccessObject.editSong(title, artist, category, time, targetPath, obj);
    }

    private Path createFile(File file) {
        Path dirPath = Path.of("src/main/resources/easv/my_tunes/audio");
        File dir = dirPath.toFile();
        dir.mkdirs();
        Path targetPath = dirPath.resolve(file.getName());
        try {
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}