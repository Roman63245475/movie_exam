package easv.my_tunes.gui;

import easv.my_tunes.be.Movie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class NewSongController implements Initializable, OtherWindow {

    @FXML
    private TextField titleField;

    private String type;

    @FXML
    private TextField artistField;

    @FXML
    private TextField ratingField;

    @FXML
    private ComboBox<String> categoryComboBox;

    private MainController mainController;

    @FXML
    private Button moreButton;

    @FXML
    private TextField timeField;

    @FXML
    private TextField filePathField;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button saveButton;

    private Movie obj;

    @FXML
    private Button cancelButton;

    private MediaPlayer durationPlayer;

    private int duration;


    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        return;
    }

    @FXML
    private void onChooseFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose MP4 or mpeg4 File");

        FileChooser.ExtensionFilter mp3Filter =
                new FileChooser.ExtensionFilter("MP4 Files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(mp3Filter);

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
            setEditTime();
        }
    }

    public void getObject(Object obj) {
        this.obj = (Movie) obj;
        if (type == "Edit") {
            fillFields();
        }

    }

    private void setEditTime(){
        timeField.setText(obj.getTime());
        filePathField.setText(obj.getPath());
    }

    public void getMainController(MainController controller){
        this.mainController = controller;
    }

    private void calculateAndSetDuration() {
        if (selectedFile == null) {
            return;
        }

        try {
            File file = selectedFile;

            Media media = new Media(selectedFile.toURI().toString());
            durationPlayer = new MediaPlayer(media);

            durationPlayer.setOnReady(() -> {
                int durationInSeconds = (int) durationPlayer.getTotalDuration().toSeconds();
                duration = durationInSeconds;
                int minutes = durationInSeconds / 60;
                int seconds = durationInSeconds % 60;

                String duration = String.format("%d:%02d", minutes, seconds);

                Platform.runLater(() -> timeField.setText(duration));

                // ⛔ сначала остановка, потом dispose
                durationPlayer.stop();
                durationPlayer.dispose();
                durationPlayer = null;
            });
            durationPlayer.play();
        } catch (Exception e) {
            System.err.println("Error reading MP3 file duration: " + e.getMessage());
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
            Media media = new Media(selectedFile.toURI().toString());
            int durationInSeconds = (int) media.getDuration().toSeconds();
            mainController.getNewSongData(titleField.getText(), this.duration, rating, selectedFile);
            closeWindow();
        }
        else {
            mainController.getEditSongData(titleField.getText(), artistField.getText(), categoryComboBox.getValue(), obj);
            closeWindow();
        }
    }

    private void fillFields() {
        titleField.setText(obj.getName());
        timeField.setText(obj.getTime());
        filePathField.setText(obj.getPath());
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

