package easv.my_tunes.gui;

import easv.my_tunes.be.Playlist;
import easv.my_tunes.be.Song;
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

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    MediaPlayer player;

    @FXML
    private TableView<Song> songsTable;

    @FXML
    private TableColumn<Song,String> songTitle;

    @FXML
    private TableColumn<Song,String> songArtist;

    @FXML
    private TableColumn<Song,String> songCategory;

    @FXML
    private TableColumn<Song,String> songDuration;

    @FXML
    private Button newPlaylistButton;

    @FXML
    private Button EditPlaylistButton;

    @FXML
    private Button EditSongButton;

    private Logic logic;

    @FXML
    private TableView<Playlist> playListsTable;

    @FXML
    private ListView<Song> songsInPlaylistList;

    @FXML
    private TableColumn<Playlist,String> playListName;

    @FXML
    private TableColumn<Playlist, Integer> playListSongs;

    @FXML
    private TableColumn<Playlist,String> playListTime;
    
    @FXML
    private Slider volumeSlider;
    
    @FXML
    private Button newSongButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupVolumeSwipeGesture();
        this.logic = new Logic();
        songsTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> songsTable.requestFocus());
        playListsTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> playListsTable.requestFocus());
        List<Song> songs = logic.loadSongs();
        List<Playlist> playlists = logic.loadPlaylists();
        displaySongs(songs);
        displayPlaylists(playlists);
        setActionOnSelectedItemTableView();
        setActionOnSelectedItemListView();
    }

    private void setActionOnSelectedItemListView(){
        songsInPlaylistList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playMusic(newValue);
            }
        });
    }

    private void setActionOnSelectedItemTableView(){
        playListsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displaySongsInPlaylist(newValue);
            }
        });
    }

    private void displaySongsInPlaylist(Playlist playlist){
        if (playlist.getSongs() != 0) {
            ObservableList<Song> lst = FXCollections.observableArrayList();
            lst.addAll(playlist.getSongsList());
            songsInPlaylistList.setItems(lst);
        }
    }




    @FXML
    private void playMusic(Song song) {
        String uriString = new File(song.getPath()).toURI().toString();
        Media media = new Media(uriString);
        player =  new MediaPlayer(media);
        player.play();
    }

    @FXML
    private void stop() {
        player.stop();
    }
    
    @FXML
    private void onNewOrEditSongClick(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        String actionType = "";
        if (source == EditSongButton) {
            Object obj = songsTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                actionType = "Edit";
                newWindow("song", actionType, obj);
            }
        }
        else{
            actionType = "New";
            newWindow("song",  actionType, null);
        }

    }

    @FXML
    private void addNewOrEditPlaylist(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        String actionType = "";
        if (source == EditPlaylistButton) {
            Object obj = playListsTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                actionType = "Edit";
                newWindow("playlist", actionType, obj);
            }
        }
        else{
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
            controller.getType(actionType);
            controller.getObject(obj);
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void addSongToPlaylist(){
        Song song = songsTable.getSelectionModel().getSelectedItem();
        Playlist playlist = playListsTable.getSelectionModel().getSelectedItem();
        if (song != null && playlist != null) {
            logic.addSongToPlaylist(playlist, song);
        }
        displayPlaylists(logic.loadPlaylists());
    }

    private void setupVolumeSwipeGesture() {
        if (volumeSlider != null) {
            // Swipe doprava = zvýšení hlasitosti
            volumeSlider.setOnSwipeRight((SwipeEvent event) -> {
                double newValue = volumeSlider.getValue() + 10;
                if (newValue > volumeSlider.getMax()) {
                    newValue = volumeSlider.getMax();
                }
                volumeSlider.setValue(newValue);
                System.out.println("Volume UP: " + newValue);
            });
            
            // Swipe doleva = snížení hlasitosti
            volumeSlider.setOnSwipeLeft((SwipeEvent event) -> {
                double newValue = volumeSlider.getValue() - 10;
                if (newValue < volumeSlider.getMin()) {
                    newValue = volumeSlider.getMin();
                }
                volumeSlider.setValue(newValue);
                System.out.println("Volume DOWN: " + newValue);
            });
            
            // Listener pro změny hodnoty slideru
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("Volume changed: " + newValue.intValue());
                // Zde můžete přidat logiku pro skutečné změny hlasitosti přehrávače
            });
        }
    }

    private void displaySongs(List<Song> songs) {
        ObservableList<Song> songList = FXCollections.observableArrayList();
        songList.addAll(songs);
        songTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        songCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("time"));
        songsTable.setItems(songList);
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
        logic.saveSong(title,  artist, category, time, file);
        displaySongs(logic.loadSongs());
    }

    public void getEditSongData(String title, String artist, String category, int time, File file, Song obj) {
        logic.editSong(title, artist, category, time, file, obj);
        displaySongs(logic.loadSongs());
    }

    public void getEditPlaylistData(Playlist obj, String name) {
        logic.editPlaylist(name, obj);
        displayPlaylists(logic.loadPlaylists());
    }

    public void getNewPlayListData(String name){
        logic.savePlayList(name);
        displayPlaylists(logic.loadPlaylists());
    }

}
