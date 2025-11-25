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

    public List<Playlist> getPlaylists(){
        HashMap<Integer, Playlist> playlists = new HashMap<>();
        try (Connection con = ConnectionManager.getConnection()) {
            String sqlPrompt = "select playlists.id as playlist_id, playlists.name as playlist_name, playlist_songs.song_id as song_id, songs.title as song_title, songs.artist as song_artist, songs.category as song_category, songs.time as song_time, songs.path as song_path from playlists LEFT JOIN playlist_songs ON playlists.id = playlist_songs.playlist_id LEFT JOIN songs on playlist_songs.song_id = songs.id";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int playlistID = rs.getInt("playlist_id");
                Playlist playlist = playlists.get(playlistID);
                if (playlist == null) {
                    String playlistName = rs.getString("playlist_name");
                    Song song = new Song(rs.getInt("song_id"), rs.getString("song_title"), rs.getString("song_artist"), rs.getString("song_category"), rs.getInt("song_time"), rs.getString("song_path"));
                    Playlist playlist1 = new Playlist(playlistID, playlistName);
                    playlist1.addSong(song);
                    playlists.put(playlistID, playlist1);
                }
                else {
                    Song song = new Song(rs.getInt("song_id"), rs.getString("song_title"), rs.getString("song_artist"), rs.getString("song_category"), rs.getInt("song_time"), rs.getString("song_path"));
                    playlist.addSong(song);
                }
            }
            return new ArrayList<>(playlists.values());
        }
        catch (SQLException ex) {
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


}