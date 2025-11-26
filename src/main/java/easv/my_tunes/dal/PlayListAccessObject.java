package easv.my_tunes.dal;

import easv.my_tunes.be.Playlist;
import easv.my_tunes.be.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayListAccessObject {

    public List<Playlist> getPlaylists() {
        HashMap<Integer, Playlist> playlists = new HashMap<>();
        try (Connection con = ConnectionManager.getConnection()) {
            String sqlPrompt = "SELECT playlists.id AS playlist_id, playlists.name AS playlist_name, " +
                    "playlist_songs.song_id AS song_id, songs.title AS song_title, " +
                    "songs.artist AS song_artist, songs.category AS song_category, " +
                    "songs.time AS song_time, songs.path AS song_path " +
                    "FROM playlists " +
                    "LEFT JOIN playlist_songs ON playlists.id = playlist_songs.playlist_id " +
                    "LEFT JOIN songs ON playlist_songs.song_id = songs.id";

            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int playlistID = rs.getInt("playlist_id");
                Playlist playlist = playlists.get(playlistID);

                if (playlist == null) {
                    String playlistName = rs.getString("playlist_name");
                    playlist = new Playlist(playlistID, playlistName);
                    playlists.put(playlistID, playlist);
                }

                Integer songId = rs.getObject("song_id", Integer.class);
                if (songId != null) {
                    Song song = new Song(
                            songId,
                            rs.getString("song_title"),
                            rs.getString("song_artist"),
                            rs.getString("song_category"),
                            rs.getInt("song_time"),
                            rs.getString("song_path")
                    );
                    playlist.addSong(song);
                }
            }
            return new ArrayList<>(playlists.values());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void savePlayList(String name){
        try (Connection con = ConnectionManager.getConnection()){
            String sqlPrompt = "Insert Into playlists (name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setString(1, name);
            ps.execute();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void editPlaylist(String name, Playlist obj) {
        try(Connection con = ConnectionManager.getConnection()){
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
    public void deletePlaylist(Playlist playlist) {
        try (Connection con = ConnectionManager.getConnection()) {
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