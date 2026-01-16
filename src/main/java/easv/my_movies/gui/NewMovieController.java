package easv.my_movies.gui;

import easv.my_movies.be.Movie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewMovieController implements Initializable, OtherWindow {

    @FXML
    private TextField titleField;

    @FXML
    private TextField ratingField;

    @FXML
    private TextField timeField;

    @FXML
    private TextField filePathField;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button cancelButton;

    private MainController mainController;
    private MediaPlayer durationPlayer;
    private File selectedFile;
    private Movie obj;
    private int duration;
    private String type;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        return;
    }

    @FXML
    private void onChooseFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose MP4 or mpeg4 File");

        FileChooser.ExtensionFilter mp4Filter =
                new FileChooser.ExtensionFilter("MP4 Files (*.mp4)", "*.mp4", ".mpeg4");
        fileChooser.getExtensionFilters().add(mp4Filter);

        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getName());
            calculateAndSetDuration();
        }
    }

    public void getType(String type) {
        this.type = type;
        checkType();
    }

    private void checkType() {
        if (type.equals("Edit")) {
            chooseFileButton.setDisable(true);
            //setEditTime();
        }
    }

    public void getObject(Object obj) {
        this.obj = (Movie) obj;
        if (type.equals("Edit")) {
            fillFields();
        }
    }

    public void getMainController(MainController controller){
        this.mainController = controller;
    }

    private void calculateAndSetDuration() {
        if (selectedFile == null) {
            return;
        }

        try {
            Media media = new Media(selectedFile.toURI().toString());
            durationPlayer = new MediaPlayer(media);

            durationPlayer.setOnReady(() -> {
                int durationInSeconds = (int) durationPlayer.getTotalDuration().toSeconds();
                duration = durationInSeconds;
                int minutes = durationInSeconds / 60;
                int seconds = durationInSeconds % 60;

                String duration = String.format("%d:%02d", minutes, seconds);

                Platform.runLater(() -> timeField.setText(duration));
                //timeField.setText(duration);

                durationPlayer.stop();
                durationPlayer.dispose();
                durationPlayer = null;
            });
            durationPlayer.play();
        } catch (Exception e) {
            System.err.println("Error reading MP4 file duration: " + e.getMessage());
            e.printStackTrace();
            timeField.setText("Unknown");
        }
    }

    @FXML
    private void onSaveClick() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        Integer rating;
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a title.");
            return;
        }

        if (ratingField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a rating");
            return;
        }
        try{
            rating = Integer.parseInt(ratingField.getText());
            if (rating < 0 || rating > 10) {
                showAlert("Validation Error", "Rating must be between 0 and 10");
                return;
            }
        }
        catch (NumberFormatException e){
            showAlert("Validation Error", "Rating must be an integer");
            return;
        }


        if (selectedFile == null && type.equals("New")) {
            showAlert("Validation Error", "Please choose an MP4 or mpeg4 file.");
            return;
        }


        if (type.equals("New")) {
            mainController.getNewMovieData(titleField.getText(), this.duration, rating, selectedFile);
            closeWindow();
        }
        else {
            mainController.getEditMovieData(titleField.getText(), rating, obj);
            closeWindow();
        }
    }

    private void fillFields() {
        titleField.setText(obj.getName());
        timeField.setText(obj.getTime());
        filePathField.setText(obj.getPath());
        ratingField.setText(String.valueOf(obj.getRating()));
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

