package easv.my_movies.bll;

import easv.my_movies.be.Category;
import easv.my_movies.be.Movie;
import easv.my_movies.dal.CategoriesAccessObject;
import easv.my_movies.dal.Movie_CategoryAccessObject;
import easv.my_movies.dal.MoviesAccessObject;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Logic {
    MoviesAccessObject MoviesAccessObject;
    CategoriesAccessObject CategoryAccessObject;
    Movie_CategoryAccessObject Categorys_Movies_AccessObject;

    public Logic() {
        MoviesAccessObject = new MoviesAccessObject();
        CategoryAccessObject = new CategoriesAccessObject();
        Categorys_Movies_AccessObject = new Movie_CategoryAccessObject();
    }

    public List<Movie> loadMovies() {
        return MoviesAccessObject.getMovies();
    }

    public List<Category> loadCategories(){
        return CategoryAccessObject.getCategories();
    }

    public void saveMovie(String title, int time, int rating, int imbdRating, File file) {
        Path targetPath = createFile(file);
        MoviesAccessObject.saveMovie(title, time, rating, imbdRating, targetPath);
    }

    public void saveCategory(String name){
        CategoryAccessObject.saveCategory(name);
    }

    public void editCategory(String name, Category obj) throws IOException {
        CategoryAccessObject.editCategory(name, obj);
    }

    public void addMovieToCategory(Category Category, Movie Movie) {
        Categorys_Movies_AccessObject.addMovieToCategory(Category, Movie);
    }

    public void editMovie(String title, int rating, Movie obj) {
        //obj.setTitle(title);
        //obj.setTime(time);
        MoviesAccessObject.editMovie(title, rating, obj);
    }

    public void deleteMovie(Movie Movie) {
        File file = new File(Movie.getPath()).getAbsoluteFile();

        if (file.exists()) {
            // Даём время антивирусу и системе освободить файл
            boolean deleted = deleteWithRetry(file);

            if (!deleted) {
                System.out.println("Failed to delete: " + file.getAbsolutePath());
                // Помечаем для удаления при следующем запуске
                file.deleteOnExit();
            }
        }

        MoviesAccessObject.deleteMovie(Movie);
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

    public void deleteCategory(Category Category) {
        CategoryAccessObject.deleteCategory(Category);
    }



    private Path createFile(File file) {
        Path dirPath = Path.of("src/main/resources/easv/my_movies/movies");
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

    public void deleteMovieFromCategory(Movie Movie, Category Category) {
        Categorys_Movies_AccessObject.deleteMovie(Movie, Category);
    }

    public List<Movie> getMoviesOnCategory(Category Category){
        return Categorys_Movies_AccessObject.getMoviesOnCategory(Category);
    }
}

