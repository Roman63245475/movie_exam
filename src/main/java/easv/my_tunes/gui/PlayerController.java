package easv.my_tunes.gui;

import easv.my_tunes.be.Movie;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {
    @FXML private MediaView videoView;
    @FXML private Slider volumeSlider;
    @FXML private Slider videoSlider;
    private MediaPlayer player;
    private Duration duration;
    private List<Movie> movies;
    private int queue = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (player == null) return;
            player.setVolume(newValue.doubleValue() / 100.0);
        });

        videoSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (player == null) return;
            if (newValue) {
                player.pause();
            }else{
                player.seek(Duration.seconds(videoSlider.getValue()));
                player.play();
            }
        });
    }

    public void setVideo(MediaPlayer player) {
        if (player == null) return;
        this.player = player;
        videoView.setMediaPlayer(player);
        player.play();
    }

    public void updateMoviesList(List<Movie> movies) {
        this.movies = movies;
    }

    public void playNext(){
        if (queue < movies.size()){
            queue++;
        }else{
            return;
        }
        System.out.println(movies.get(queue));
    }

    public void playPrevious(){
        if (queue > 0) queue--;
        System.out.println(movies.get(queue));
    }

    public void shutdown(){
        if (player == null) return;
        player.stop();
        player.dispose();
        player = null;
    }
}
