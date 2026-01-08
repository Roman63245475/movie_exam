package easv.my_movies.gui;

import easv.my_movies.be.Movie;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {
    @FXML private MediaView videoView;
    @FXML private Slider volumeSlider;
    @FXML private Slider videoSlider;
    @FXML private Button playerBtn;

    private MediaPlayer player;
    private List<Movie> movies;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerBtn.setText("||");
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
    @FXML
    private void continueOrStop(){
        if (player != null){
            if (playerBtn.getText().equals("||")){
                player.pause();
                playerBtn.setText("▶");
            }
            else{
                playerBtn.setText("||");
                player.play();
            }
        }
    }

    public void updateMoviesList(List<Movie> movies) {
        this.movies = movies;
    }

    public void shutdown(){
        if (player == null) return;
        player.stop();
        player.dispose();
        player = null;
    }
}
