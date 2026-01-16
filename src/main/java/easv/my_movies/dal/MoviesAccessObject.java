package easv.my_movies.dal;

import easv.my_movies.be.Movie;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoviesAccessObject {
    private static ConnectionManager cm;

    static {
        try {
            cm = new ConnectionManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Movie> getMovies(){
        List<Movie> movies = new ArrayList<>();
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "SELECT * FROM movie_table";
            PreparedStatement pst = con.prepareStatement(sqlPrompt);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int time = rs.getInt("duration");
                String path = rs.getString("path");
                int rating = rs.getInt("rating");
                int imbdRating = rs.getInt("imbdRating");
                movies.add(new Movie(id, name, time, path, rating, imbdRating));
            }
            return movies;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMovie(String title, int time, int rating, int imbdRating, Path targetPath) {
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "Insert Into movie_table (name, duration, path, rating, imbdRating) VALUES (?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sqlPrompt);
            pst.setString(1, title);
            pst.setInt(2, time);
            pst.setString(3, targetPath.toString());
            pst.setInt(4, rating);
            pst.setInt(5, imbdRating);
            pst.execute();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMovie(String title, int rating, Movie obj) {
        try(Connection con = cm.getConnection()){
            String sqlPrompt = "Update movie_table Set name=?, rating=? where id=?";
            PreparedStatement pst = con.prepareStatement(sqlPrompt);
            pst.setString(1, title);
            pst.setInt(2, rating);
            pst.setInt(3, obj.getID());
            pst.execute();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMovie(Movie movie) {
        try (Connection con = cm.getConnection()) {
            String sqlRel = "DELETE FROM movie_category WHERE movie_id = ?";
            PreparedStatement psRel = con.prepareStatement(sqlRel);
            psRel.setInt(1, movie.getID());
            psRel.execute();

            String sql = "DELETE FROM movie_table WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, movie.getID());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
