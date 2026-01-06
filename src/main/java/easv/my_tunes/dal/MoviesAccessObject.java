package easv.my_tunes.dal;

import easv.my_tunes.be.Movie;

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
                movies.add(new Movie(id, name, time, path));
            }
            return movies;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSong(String title, int time, Path targetPath) {
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "Insert Into movie_table (name, duration, path) VALUES (?,?,?)";
            PreparedStatement pst = con.prepareStatement(sqlPrompt);
            pst.setString(1, title);
            pst.setInt(2, time);
            pst.setString(3, targetPath.toString());
            pst.execute();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editSong(String title, String artist, String category, Movie obj) {
        try(Connection con = cm.getConnection()){
            String sqlPrompt = "Update songs Set title=?, artist=?, category=? where id=?";
            PreparedStatement pst = con.prepareStatement(sqlPrompt);
            pst.setString(1, title);
            pst.setString(2, artist);
            pst.setString(3, category);
            pst.setInt(4, obj.getID());
            pst.execute();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSong(Movie song) {
        try (Connection con = cm.getConnection()) {
            String sqlRel = "DELETE FROM playlist_songs WHERE song_id = ?";
            PreparedStatement psRel = con.prepareStatement(sqlRel);
            psRel.setInt(1, song.getID());
            psRel.execute();

            String sql = "DELETE FROM songs WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, song.getID());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
