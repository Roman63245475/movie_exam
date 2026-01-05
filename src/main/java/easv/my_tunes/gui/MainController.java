package easv.my_tunes.gui;

import easv.my_tunes.be.Playlist;
import easv.my_tunes.be.Song;
import easv.my_tunes.bll.Logic;
import javafx.application.Platform;
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
import javafx.scene.input.SwipeEvent;
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
    private TableView<Song> songsTable;

    @FXML
    private TableColumn<Song, String> songTitle;

    @FXML
    private TableColumn<Song, String> songArtist;

    @FXML
    private TableColumn<Song, String> songCategory;

    @FXML
    private TableColumn<Song, String> songDuration;

    @FXML
    private Button newPlaylistButton;

    @FXML
    private Button controlButton;

    @FXML
    private Button EditPlaylistButton;

    @FXML
    private Button EditSongButton;

    private Logic logic;

    private Playlist selected_playlist;

    @FXML
    private TableView<Playlist> playListsTable;

    @FXML
    private ListView<Song> songsInPlaylistList;

    @FXML
    private TableColumn<Playlist, String> playListName;

    @FXML
    private TableColumn<Playlist, Integer> playListSongs;

    @FXML
    private TableColumn<Playlist, String> playListTime;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button newSongButton;

    @FXML
    private TextField filterTextField;

    @FXML
    private Label lblCurrentSong;

    private FilteredList<Song> filteredSongs;

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
        List<Song> songs = logic.loadSongs();
        List<Playlist> playlists = logic.loadPlaylists();
        displaySongs(songs);
        displayPlaylists(playlists);
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

    private void displaySongsInPlaylist(Playlist playlist) {
        ObservableList<Song> lst = FXCollections.observableArrayList();
        lst.addAll(logic.getSongsOnPlaylist(playlist));
        songsInPlaylistList.setItems(lst);
    }

    @FXML
    private void playMusic(Song song) {
        String path = song.getPath().replace("\\", "/");

        File file = new File(path);

        if (file.exists()) {
            if (lblCurrentSong != null) {
                lblCurrentSong.setText(song.getTitle() + " - " + song.getArtist());
            }

            String uriString = file.toURI().toString();
            Media media = new Media(uriString);

            if (player != null) {
                player.stop();
            }

            player = new MediaPlayer(media);
            player.setOnEndOfMedia(this::nextSong);
            player.play();
            controlButton.setText("| |");

            if (volumeSlider != null) {
                player.setVolume(volumeSlider.getValue() / 100.0);
            }
        } else {
            System.out.println("Soubor nebyl nalezen: " + path);
        }
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

        Song selectedSong;

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
        Song selectedSong;
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
        Song song = songsTable.getSelectionModel().getSelectedItem();
        Playlist playlist = playListsTable.getSelectionModel().getSelectedItem();
        if (song != null && playlist != null) {
            logic.addSongToPlaylist(playlist, song);
        }
        displayPlaylists(logic.loadPlaylists());
        displaySongsInPlaylist(playlist);
    }

    @FXML
    private void deleteSongFomPlaylist() {

        Song song = songsInPlaylistList.getSelectionModel().getSelectedItem();
        //Playlist playlist = playListsTable.getSelectionModel().getSelectedItem();
        if (song != null && selected_playlist != null) {
            player.stop();
            player = null;
            int id = selected_playlist.getID();
            logic.deleteSongFromPlaylist(song, selected_playlist);
            List<Playlist> playlists = logic.loadPlaylists();
            displayPlaylists(playlists);
            for (Playlist playlst : playlists) {
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

    private void displaySongs(List<Song> songs) {
        ObservableList<Song> songList = FXCollections.observableArrayList();
        songList.addAll(songs);

        filteredSongs = new FilteredList<>(songList, b -> true);

        if (filterTextField != null) {
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredSongs.setPredicate(song -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (song.getTitle() != null && song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (song.getArtist() != null && song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
        }

        SortedList<Song> sortedData = new SortedList<>(filteredSongs);
        sortedData.comparatorProperty().bind(songsTable.comparatorProperty());

        songTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        songCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("time"));

        songsTable.setItems(sortedData);
    }

    private void displayPlaylists(List<Playlist> playlists) {
        ObservableList<Playlist> playlistList = FXCollections.observableArrayList();
        playlistList.addAll(playlists);
        playListName.setCellValueFactory(new PropertyValueFactory<>("name"));
        playListSongs.setCellValueFactory(new PropertyValueFactory<>("songs"));
        playListTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        playListsTable.setItems(playlistList);
    }


    public void getNewSongData(String title, String artist, String category, int time, File file) {
        logic.saveSong(title, artist, category, time, file);
        displaySongs(logic.loadSongs());
    }


    public void getEditSongData(String title, String artist, String category, Song obj) {
        logic.editSong(title, artist, category,  obj);
        displaySongs(logic.loadSongs());
        String name = obj.getTitle();
        List<Playlist> playlists = logic.loadPlaylists();
        displayPlaylists(playlists);
        playListsTable.getSelectionModel().clearSelection();
        songsInPlaylistList.getItems().clear();

    }

    public void getEditPlaylistData(Playlist obj, String name) {
        logic.editPlaylist(name, obj);
        displayPlaylists(logic.loadPlaylists());
    }

    public void getNewPlayListData(String name) {
        logic.savePlayList(name);
        displayPlaylists(logic.loadPlaylists());
    }

    @FXML
    private void onDeleteSongClick() {
        Song selectedSong = songsTable.getSelectionModel().getSelectedItem();

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
            displaySongs(logic.loadSongs());
            displayPlaylists(logic.loadPlaylists());
            if (selected_playlist!=null){
                displaySongsInPlaylist(selected_playlist);
            }

        }
    }

    @FXML
    private void onDeletePlaylistClick() {
        Playlist selectedPlaylist = playListsTable.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            logic.deletePlaylist(selectedPlaylist);
            displayPlaylists(logic.loadPlaylists());
            songsInPlaylistList.getItems().clear();
        }
    }

    @FXML
    private void moveSongUp() {
        int index = songsInPlaylistList.getSelectionModel().getSelectedIndex();
        Playlist currentPlaylist = playListsTable.getSelectionModel().getSelectedItem();

        if (index > 0 && currentPlaylist != null) {
            List<Song> songs = currentPlaylist.getSongsList();
            Song temp = songs.get(index);
            songs.set(index, songs.get(index - 1));
            songs.set(index - 1, temp);

            ObservableList<Song> items = songsInPlaylistList.getItems();
            Song item = items.get(index);
            items.remove(index);
            items.add(index - 1, item);

            songsInPlaylistList.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void moveSongDown() {
        int index = songsInPlaylistList.getSelectionModel().getSelectedIndex();
        Playlist currentPlaylist = playListsTable.getSelectionModel().getSelectedItem();
        ObservableList<Song> items = songsInPlaylistList.getItems();

        if (index >= 0 && index < items.size() - 1 && currentPlaylist != null) {
            List<Song> songs = currentPlaylist.getSongsList();
            Song temp = songs.get(index);
            songs.set(index, songs.get(index + 1));
            songs.set(index + 1, temp);

            Song item = items.get(index);
            items.remove(index);
            items.add(index + 1, item);

            songsInPlaylistList.getSelectionModel().select(index + 1);
        }
    }

}
