package easv.my_tunes.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import java.util.ResourceBundle;

public class NewSongController implements Initializable, OtherWindow {
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField artistField;
    
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
    
    @FXML
    private Button cancelButton;
    
    private File selectedFile;
    private ObservableList<String> genres;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genres = FXCollections.observableArrayList(
            "Pop",
            "Rock",
            "Jazz",
            "Classical",
            "Hip-Hop",
            "Electronic",
            "Country",
            "R&B",
            "Metal",
            "Folk"
        );
        categoryComboBox.setItems(genres);
    }
    
    @FXML
    private void onChooseFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose MP3 File");
        
        FileChooser.ExtensionFilter mp3Filter =
            new FileChooser.ExtensionFilter("MP3 Files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(mp3Filter);
        
        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getName());
            calculateAndSetDuration();
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
            AudioFile audioFile = AudioFileIO.read(selectedFile);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            
            int durationInSeconds = audioHeader.getTrackLength();
            
            int minutes = durationInSeconds / 60;
            int seconds = durationInSeconds % 60;
            
            String duration = String.format("%d:%02d", minutes, seconds);
            timeField.setText(duration);
            
        } catch (Exception e) {
            System.err.println("Error reading MP3 file duration: " + e.getMessage());
            e.printStackTrace();
            timeField.setText("Unknown");
        }
    }
    
    @FXML
    private void onMoreButtonClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Genre");
        dialog.setHeaderText("Add a new music genre");
        dialog.setContentText("Genre name:");
        
        dialog.showAndWait().ifPresent(genre -> {
            if (!genre.trim().isEmpty() && !genres.contains(genre)) {
                genres.add(genre);
                categoryComboBox.setValue(genre);
            }
        });
    }
    
    @FXML
    private void onSaveClick() throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a title.");
            return;
        }
        
        if (artistField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter an artist.");
            return;
        }
        
        if (categoryComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a category.");
            return;
        }
        
        if (selectedFile == null) {
            showAlert("Validation Error", "Please choose an MP3 file.");
            return;
        }
        AudioFile audioFile = AudioFileIO.read(selectedFile);
        AudioHeader audioHeader = audioFile.getAudioHeader();
        int durationInSeconds = audioHeader.getTrackLength();
        mainController.getNewSongData(titleField.getText(), artistField.getText(), categoryComboBox.getValue(), durationInSeconds, selectedFile);
//        System.out.println("Saving song:");
//        System.out.println("Title: " + titleField.getText());
//        System.out.println("Artist: " + artistField.getText());
//        System.out.println("Category: " + categoryComboBox.getValue());
//        System.out.println("Time: " + timeField.getText());
//        System.out.println("File: " + selectedFile.getAbsolutePath());

        closeWindow();
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
