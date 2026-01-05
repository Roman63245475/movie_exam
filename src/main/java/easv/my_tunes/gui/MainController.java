package easv.my_tunes.gui;

import easv.my_tunes.be.Category;
import easv.my_tunes.be.Movie;
import easv.my_tunes.bll.Logic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;


    private MediaPlayer player;

    @FXML
    private TableView<Movie> songsTable;

    @FXML
    private TableColumn<Movie, String> movieTitle;

    @FXML
    private TableColumn<Movie, String> movieDuration;

    @FXML
    private Button newPlaylistButton;

    @FXML
    private Button controlButton;

    @FXML
    private Button EditPlaylistButton;

    @FXML
    private Button EditSongButton;

    private Logic logic;

    private Category selected_playlist;

    @FXML
    private TableView<Category> playListsTable;

    @FXML
    private ListView<Movie> songsInPlaylistList;

    @FXML
    private TableColumn<Category, String> playListName;

    @FXML
    private TableColumn<Category, Integer> movieAmount;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button newSongButton;

    @FXML
    private TextField filterTextField;

    @FXML
    private Label lblCurrentSong;

    private FilteredList<Movie> filteredSongs;

    private boolean flag;

    @FXML
    private void onCloseClick(){
        return;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setupVolumeSwipeGesture();
        this.logic = new Logic();
        songsTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> songsTable.requestFocus());
        playListsTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> playListsTable.requestFocus());
        List<Movie> songs = logic.loadMovies();
        List<Category> categories = logic.loadCategories();
        displayMovies(songs);
        displayCategories(categories);
        setActionOnSelectedItemTableView();
        setActionOnSelectedItemListView();
        setActionOnSelectedItemTableViewSongs();
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setupVolumeSwipeGesture(newValue.doubleValue());
        });
    }

    private void setActionOnSelectedItemTableViewSongs() {
        songsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                flag = true;
                songsInPlaylistList.getSelectionModel().clearSelection();
                playMusic(newValue);
            }
        });
    }

    private void setActionOnSelectedItemListView() {
        songsInPlaylistList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                flag = false;
                songsTable.getSelectionModel().clearSelection();
                playMusic(newValue);
            }
        });
    }

    private void setActionOnSelectedItemTableView() {
        playListsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_playlist = newValue;
                System.out.println(selected_playlist);
                displaySongsInPlaylist(newValue);
            }
        });
    }

    private void displaySongsInPlaylist(Category playlist) {
        ObservableList<Movie> lst = FXCollections.observableArrayList();
        lst.addAll(logic.getSongsOnPlaylist(playlist));
        songsInPlaylistList.setItems(lst);
    }

    @FXML
    private void playMusic(Movie song) {
//        String path = song.getPath().replace("\\", "/");
//
//        File file = new File(path);
//
//        if (file.exists()) {
//            if (lblCurrentSong != null) {
//                lblCurrentSong.setText(song.getTitle() + " - " + song.getArtist());
//            }
//
//            String uriString = file.toURI().toString();
//            Media media = new Media(uriString);
//
//            if (player != null) {
//                player.stop();
//            }
//
//            player = new MediaPlayer(media);
//            player.setOnEndOfMedia(this::nextSong);
//            player.play();
//            controlButton.setText("| |");
//
//            if (volumeSlider != null) {
//                player.setVolume(volumeSlider.getValue() / 100.0);
//            }
//        } else {
//            System.out.println("Soubor nebyl nalezen: " + path);
//        }
        return;
    }

    @FXML
    private void continueOrStop() {
        if (player != null) {
            if (controlButton.getText().equals("| |")) {
                controlButton.setText("▶");
                player.pause();
            }
            else {
                controlButton.setText("| |");
                player.play();
            }
        }
    }

    @FXML
    private void nextSong(){
        if (flag && songsTable.getItems().isEmpty()) return;
        if (!flag && songsInPlaylistList.getItems().isEmpty()) return;

        Movie selectedSong;

        if (flag){
            int index = songsTable.getSelectionModel().getSelectedIndex();
            //Song selectedSong;
            if (index < songsTable.getItems().size() - 1) {
                index++;
                songsTable.getSelectionModel().select(index);
                selectedSong = songsTable.getSelectionModel().getSelectedItem();
            }
            else {
                index = 0;
                songsTable.getSelectionModel().select(index);
                selectedSong = songsTable.getSelectionModel().getSelectedItem();
            }
        }
        else{
            int index = songsInPlaylistList.getSelectionModel().getSelectedIndex();
            //Song selectedSong;
            if (index < songsInPlaylistList.getItems().size() - 1) {
                index++;
                songsInPlaylistList.getSelectionModel().select(index);
                selectedSong = songsInPlaylistList.getSelectionModel().getSelectedItem();
            }
            else {
                index = 0;
                songsInPlaylistList.getSelectionModel().select(index);
                selectedSong = songsInPlaylistList.getSelectionModel().getSelectedItem();
            }
        }

        playMusic(selectedSong);
    }

    @FXML
    private void previousSong(){
        Movie selectedSong;
        if (flag){
            int index = songsTable.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                index--;
                songsTable.getSelectionModel().select(index);
                selectedSong = songsTable.getSelectionModel().getSelectedItem();
            }
            else {
                index = songsTable.getItems().size() - 1;
                songsTable.getSelectionModel().select(index);
                selectedSong = songsTable.getSelectionModel().getSelectedItem();
            }
        }
        else{
            int index = songsInPlaylistList.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                index--;
                songsInPlaylistList.getSelectionModel().select(index);
                selectedSong = songsInPlaylistList.getSelectionModel().getSelectedItem();
            }
            else {
                index = songsInPlaylistList.getItems().size() - 1;
                songsInPlaylistList.getSelectionModel().select(index);
                selectedSong = songsInPlaylistList.getSelectionModel().getSelectedItem();
            }
        }

        playMusic(selectedSong);
    }

    @FXML
    private void onNewOrEditSongClick(ActionEvent actionEvent) {
        if (player != null) {
            player.stop();
            player = null;
        }
        Object source = actionEvent.getSource();
        String actionType = "";
        if (source == EditSongButton) {
            Object obj = songsTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                actionType = "Edit";
                newWindow("song", actionType, obj);
            }
        } else {
            actionType = "New";
            newWindow("song", actionType, null);
        }

    }

    @FXML
    private void addNewOrEditPlaylist(ActionEvent actionEvent) {
        if (player != null) {
            player.stop();
        }
        Object source = actionEvent.getSource();
        String actionType = "";
        if (source == EditPlaylistButton) {
            Object obj = playListsTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                actionType = "Edit";
                newWindow("playlist", actionType, obj);
            }
        } else {
            actionType = "New";
            newWindow("playlist", actionType, null);
        }

    }

    private void newWindow(String type, String actionType, Object obj) {
        String fileName = (type.equals("playlist") ? "add-new-playlist.fxml" : "new-song-window.fxml");
        String title = (type.equals("playlist") ? "New/Edit Playlist" : "New/Edit Song");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            OtherWindow controller = loader.getController();
            controller.getMainController(this);
            controller.getObject(obj);
            controller.getType(actionType);
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void addSongToPlaylist() {
        Movie song = songsTable.getSelectionModel().getSelectedItem();
        Category playlist = playListsTable.getSelectionModel().getSelectedItem();
        if (song != null && playlist != null) {
            logic.addSongToPlaylist(playlist, song);
        }
        displayCategories(logic.loadCategories());
        displaySongsInPlaylist(playlist);
    }

    @FXML
    private void deleteSongFomPlaylist() {

        Movie song = songsInPlaylistList.getSelectionModel().getSelectedItem();
        //Playlist playlist = playListsTable.getSelectionModel().getSelectedItem();
        if (song != null && selected_playlist != null) {
            player.stop();
            player = null;
            int id = selected_playlist.getID();
            logic.deleteSongFromPlaylist(song, selected_playlist);
            List<Category> playlists = logic.loadCategories();
            displayCategories(playlists);
            for (Category playlst : playlists) {
                if (id == playlst.getID()) {
                    displaySongsInPlaylist(playlst);
                    playListsTable.getSelectionModel().select(playlst);
                }
            }
        }
    }

    private void setupVolumeSwipeGesture(double newvalue) {
        if (player != null) {
            player.setVolume(newvalue / 100.0);
        }
    }

    private void displayMovies(List<Movie> songs) {
        ObservableList<Movie> movieList = FXCollections.observableArrayList();
        movieList.addAll(songs);

        filteredSongs = new FilteredList<>(movieList, b -> true);

        if (filterTextField != null) {
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredSongs.setPredicate(song -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (song.getTitle() != null && song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } //else if (song.getArtist() != null && song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                        //return true;
                    //}
                    return false;
                });
            });
        }

        SortedList<Movie> sortedData = new SortedList<>(filteredSongs);
        sortedData.comparatorProperty().bind(songsTable.comparatorProperty());

        movieTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        movieDuration.setCellValueFactory(new PropertyValueFactory<>("time"));

        songsTable.setItems(sortedData);
    }

    private void displayCategories(List<Category> playlists) {
        ObservableList<Category> categoriesList = FXCollections.observableArrayList();
        categoriesList.addAll(playlists);
        playListName.setCellValueFactory(new PropertyValueFactory<>("name"));
        movieAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        playListsTable.setItems(categoriesList);
    }


    public void getNewSongData(String title, String artist, String category, int time, File file) {
        logic.saveSong(title, artist, category, time, file);
        displayMovies(logic.loadMovies());
    }


    public void getEditSongData(String title, String artist, String category, Movie obj) {
        logic.editSong(title, artist, category,  obj);
        displayMovies(logic.loadMovies());
        String name = obj.getTitle();
        List<Category> playlists = logic.loadCategories();
        displayCategories(playlists);
        playListsTable.getSelectionModel().clearSelection();
        songsInPlaylistList.getItems().clear();

    }

    public void getEditPlaylistData(Category obj, String name) {
        logic.editPlaylist(name, obj);
        displayCategories(logic.loadCategories());
    }

    public void getNewPlayListData(String name) {
        logic.savePlayList(name);
        displayCategories(logic.loadCategories());
    }

    @FXML
    private void onDeleteSongClick() {
        Movie selectedSong = songsTable.getSelectionModel().getSelectedItem();

        if (selectedSong != null && player != null && lblCurrentSong.getText().contains(selectedSong.getTitle())) {
            player.stop();
            player.dispose();
            player = null;
            lblCurrentSong.setText("-");
        }

        if (selectedSong != null) {
            //Platform.runLater(() -> logic.deleteSong(selectedSong));
            logic.deleteSong(selectedSong);
            songsTable.getSelectionModel().clearSelection();
            displayMovies(logic.loadMovies());
            displayCategories(logic.loadCategories());
            if (selected_playlist!=null){
                displaySongsInPlaylist(selected_playlist);
            }

        }
    }

    @FXML
    private void onDeletePlaylistClick() {
        Category selectedPlaylist = playListsTable.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            logic.deletePlaylist(selectedPlaylist);
            displayCategories(logic.loadCategories());
            songsInPlaylistList.getItems().clear();
        }
    }

    @FXML
    private void moveSongUp() {
        int index = songsInPlaylistList.getSelectionModel().getSelectedIndex();
        Category currentPlaylist = playListsTable.getSelectionModel().getSelectedItem();

        if (index > 0 && currentPlaylist != null) {
            List<Movie> songs = currentPlaylist.getSongsList();
            Movie temp = songs.get(index);
            songs.set(index, songs.get(index - 1));
            songs.set(index - 1, temp);

            ObservableList<Movie> items = songsInPlaylistList.getItems();
            Movie item = items.get(index);
            items.remove(index);
            items.add(index - 1, item);

            songsInPlaylistList.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void moveSongDown() {
        int index = songsInPlaylistList.getSelectionModel().getSelectedIndex();
        Category currentPlaylist = playListsTable.getSelectionModel().getSelectedItem();
        ObservableList<Movie> items = songsInPlaylistList.getItems();

        if (index >= 0 && index < items.size() - 1 && currentPlaylist != null) {
            List<Movie> songs = currentPlaylist.getSongsList();
            Movie temp = songs.get(index);
            songs.set(index, songs.get(index + 1));
            songs.set(index + 1, temp);

            Movie item = items.get(index);
            items.remove(index);
            items.add(index + 1, item);

            songsInPlaylistList.getSelectionModel().select(index + 1);
        }
    }

}
