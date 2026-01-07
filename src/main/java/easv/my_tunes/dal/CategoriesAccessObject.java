package easv.my_tunes.dal;

import easv.my_tunes.be.Category;
import easv.my_tunes.be.Movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoriesAccessObject {
    private static ConnectionManager cm;

    static {
        try {
            cm = new ConnectionManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Category> getCategories() {
        HashMap<Integer, Category> categories = new HashMap<>();
        try (Connection con = cm.getConnection()) {
            String sqlPrompt = "SELECT category_table.id AS category_id, category_table.name AS category_name, " +
                    "movie_category.movie_id AS movie_id, movie_table.name AS movie_name, " +
                    "movie_table.duration AS movie_time, movie_table.path AS movie_path, movie_table.rating AS movie_rating " +
                    "FROM category_table " +
                    "LEFT JOIN movie_category ON category_table.id = movie_category.category_id " +
                    "LEFT JOIN movie_table ON movie_category.movie_id = movie_table.id";

            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int categoryID = rs.getInt("category_id");
                Category category = categories.get(categoryID);

                if (category == null) {
                    String categoryName = rs.getString("category_name");
                    category = new Category(categoryID, categoryName);
                    categories.put(categoryID, category);
                }

                Integer movieId = rs.getObject("movie_id", Integer.class);
                if (movieId != null) {
                    Movie movie = new Movie(
                            movieId,
                            rs.getString("movie_name"),
                            rs.getInt("movie_time"),
                            rs.getString("movie_path"),
                            rs.getInt("movie_rating")
                    );
                    category.addMovie(movie);
                }
            }
            return new ArrayList<>(categories.values());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveCategory(String name){
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "Insert Into category_table (name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, name);
            ps.execute();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void editPlaylist(String name, Category obj) throws IOException {
        cm = new ConnectionManager();
        try(Connection con = cm.getConnection()){
            String sqlPrompt =  "Update playlists set name = ? where id = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, name);
            ps.setInt(2, obj.getID());
            ps.execute();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void deletePlaylist(Category playlist) {
        try (Connection con = cm.getConnection()) {
            String sqlRel = "DELETE FROM playlist_songs WHERE playlist_id = ?";
            PreparedStatement psRel = con.prepareStatement(sqlRel);
            psRel.setInt(1, playlist.getID());
            psRel.execute();

            String sql = "DELETE FROM playlists WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, playlist.getID());
            ps.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
