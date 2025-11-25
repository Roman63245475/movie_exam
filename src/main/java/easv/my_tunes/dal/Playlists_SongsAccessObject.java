package easv.my_tunes.dal;

import easv.my_tunes.be.Playlist;
import easv.my_tunes.be.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Playlists_SongsAccessObject {

    public void addSongToPlaylist(Playlist playlist, Song song) {
        try (Connection con = ConnectionManager.getConnection()){
            String sqlPrompt = "Insert Into playlist_songs (playlist_id, song_id) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlPrompt);
            ps.setInt(1, playlist.getID());
            ps.setInt(2, song.getID());
            ps.execute();
        }
        catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }
}
