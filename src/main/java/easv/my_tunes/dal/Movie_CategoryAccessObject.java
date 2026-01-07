package easv.my_tunes.dal;

import easv.my_tunes.be.Category;
import easv.my_tunes.be.Movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Movie_CategoryAccessObject {
    private static ConnectionManager cm;

    static {
        try {
            cm = new ConnectionManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSongToPlaylist(Category playlist, Movie song) {
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "Insert Into movie_category (category_id, movie_id) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, playlist.getID());
            ps.setInt(2, song.getID());
            ps.execute();
        }
        catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    public void deleteSong(Movie song, Category playlist) {
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "delete from movie_category where id=?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, song.getPlaylist_song_id());
            ps.execute();
        }
        catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    public List<Movie> getSongsOnPlaylist(Category playlist) {
        List<Movie> movies = new ArrayList<>();
        try (Connection con = cm.getConnection()){
            String sqlPrompt = "select movie_category.id as field_id, movie_table.id as movie_id, movie_table.name as movie_name, movie_table.duration as movie_time, movie_table.path as movie_path from movie_category INNER JOIN movie_table on movie_category.movie_id = movie_table.id where movie_category.category_id = ?";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, playlist.getID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int field_id = rs.getInt("field_id");
                int movie_id = rs.getInt("movie_id");
                String movieName = rs.getString("movie_name");
                int movie_time = rs.getInt("movie_time");
                String movie_path = rs.getString("movie_path");
                movies.add(new Movie(movie_id, movieName, movie_time, movie_path, field_id));
            }
            return movies;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
