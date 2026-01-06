package easv.my_tunes.bll;

import easv.my_tunes.be.Category;
import easv.my_tunes.be.Movie;
import easv.my_tunes.dal.CategoriesAccessObject;
import easv.my_tunes.dal.Movie_CategoryAccessObject;
import easv.my_tunes.dal.MoviesAccessObject;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Logic {
    MoviesAccessObject songsAccessObject;
    CategoriesAccessObject playListAccessObject;
    Movie_CategoryAccessObject playLists_songs_AccessObject;

    public Logic() {
        songsAccessObject = new MoviesAccessObject();
        playListAccessObject = new CategoriesAccessObject();
        playLists_songs_AccessObject = new Movie_CategoryAccessObject();
    }

    public List<Movie> loadMovies() {
        return songsAccessObject.getMovies();
    }

    public List<Category> loadCategories(){
        return playListAccessObject.getCategories();
    }

    public void saveSong(String title, int time, File file) {
        Path targetPath = createFile(file);
        songsAccessObject.saveSong(title, time, targetPath);
    }

    public void saveCategory(String name){
        playListAccessObject.saveCategory(name);
    }

    public void editPlaylist(String name, Category obj) throws IOException {
        playListAccessObject.editPlaylist(name, obj);
    }

    public void addSongToPlaylist(Category playlist, Movie song) {
        playLists_songs_AccessObject.addSongToPlaylist(playlist, song);
    }

    public void editSong(String title, String artist, String category, Movie obj) {
        obj.setTitle(title);
        //obj.setTime(time);
        songsAccessObject.editSong(title, artist, category, obj);
    }

    public void deleteSong(Movie song) {
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

    public void deletePlaylist(Category playlist) {
        playListAccessObject.deletePlaylist(playlist);
    }



    private Path createFile(File file) {
        Path dirPath = Path.of("src/main/resources/easv/my_tunes/movies");
        dirPath.toFile().mkdirs();

        String baseName = file.getName();
        String name = baseName;
        int counter = 1;

        Path targetPath = dirPath.resolve(name);

        // Если файл существует — создаём новый уникальный вариант
        while (Files.exists(targetPath)) {
            String withoutExt = baseName.substring(0, baseName.lastIndexOf('.'));
            String ext = baseName.substring(baseName.lastIndexOf('.'));
            name = withoutExt + " (" + counter + ")" + ext;
            targetPath = dirPath.resolve(name);
            counter++;
        }

        try {
            Files.copy(file.toPath(), targetPath);
            return targetPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private Path createFile(File file) {
//        Path dirPath = Path.of("src/main/resources/easv/my_tunes/audio");
//        File dir = dirPath.toFile();
//        dir.mkdirs();
//        Path targetPath = dirPath.resolve(file.getName());
//
//        try {
//            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
//            return targetPath;
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void deleteSongFromPlaylist(Movie song, Category playlist) {
        playLists_songs_AccessObject.deleteSong(song, playlist);
    }

    public List<Movie> getSongsOnPlaylist(Category playlist){
        return playLists_songs_AccessObject.getSongsOnPlaylist(playlist);
    }
}

