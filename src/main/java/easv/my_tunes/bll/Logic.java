package easv.my_tunes.bll;

import easv.my_tunes.be.Playlist;
import easv.my_tunes.be.Song;
import easv.my_tunes.dal.PlayListAccessObject;
import easv.my_tunes.dal.Playlists_SongsAccessObject;
import easv.my_tunes.dal.SongsAccessObject;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
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

    public void editSong(String title, String artist, String category, Song obj) {
        obj.setTitle(title);
        obj.setArtist(artist);
        obj.setCategory(category);
        //obj.setTime(time);
        songsAccessObject.editSong(title, artist, category, obj);
    }

    public void deleteSong(Song song) {
        File file = new File(song.getPath()).getAbsoluteFile();

        if (file.exists()) {
            // Даём время антивирусу и системе освободить файл
            boolean deleted = deleteWithRetry(file);

            if (!deleted) {
                System.out.println("Failed to delete: " + file.getAbsolutePath());
                // Помечаем для удаления при следующем запуске
                file.deleteOnExit();
            }
        }

        songsAccessObject.deleteSong(song);
    }

    private boolean deleteWithRetry(File file) {
        for (int i = 0; i < 3; i++) { // 3 попытки
            if (file.delete()) {
                return true;
            }

            // Ждём между попытками
            try {
                Thread.sleep(100 * (i + 1)); // 100ms, 200ms, 300ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }

            // Принудительно освобождаем ресурсы
            System.gc();
        }
        return false;
    }


    private boolean isFileLocked(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        try (FileChannel channel = FileChannel.open(file.toPath(),
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND)) {
            // Пытаемся получить эксклюзивную блокировку
            FileLock lock = channel.tryLock();
            if (lock != null) {
                lock.release(); // Сразу отпускаем
                return false; // Файл не заблокирован
            }
            return true; // Не удалось получить блокировку - файл занят
        } catch (IOException e) {
            // OverlappingFileLockException или другие IOException
            return true; // Файл заблокирован
        } catch (Exception e) {
            System.out.println("Error checking lock: " + e.getMessage());
            return true; // В случае ошибки считаем заблокированным
        }
    }

    public void deletePlaylist(Playlist playlist) {
        playListAccessObject.deletePlaylist(playlist);
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

    public void deleteSongFromPlaylist(Song song, Playlist playlist) {
        playLists_songs_AccessObject.deleteSong(song, playlist);
    }

    public List<Song> getSongsOnPlaylist(Playlist playlist){
        return playLists_songs_AccessObject.getSongsOnPlaylist(playlist);
    }
}