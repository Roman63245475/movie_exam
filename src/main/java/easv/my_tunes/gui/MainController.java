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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;


    private MediaPlayer player;

    @FXML
    private TableView<Movie> moviesTable;

    @FXML
    private TableColumn<Movie, Integer> ratingColumn;

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
    private TableView<Category> CategoriesTable;

    @FXML
    private ListView<Movie> moviesInCategoryList;

    @FXML
    private TableColumn<Category, String> categoryName;

    @FXML
    private TableColumn<Category, Integer> moviesAmount;

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
        moviesTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> moviesTable.requestFocus());
        CategoriesTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> CategoriesTable.requestFocus());
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
        moviesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                flag = true;
                moviesInCategoryList.getSelectionModel().clearSelection();
                Media media = new Media(new File(newValue.getPath()).toURI().toString());
                player = new MediaPlayer(media);
            }
        });
    }

    private void setActionOnSelectedItemListView() {
        moviesInCategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                flag = false;
                moviesTable.getSelectionModel().clearSelection();
                Media media = new Media(new File(newValue.getPath()).toURI().toString());
                player = new MediaPlayer(media);
            }
        });
    }


    private void setActionOnSelectedItemTableView() {
        CategoriesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
        moviesInCategoryList.setItems(lst);
    }

    @FXML
    private void playMusic(MediaPlayer player) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("player-view.fxml"));
            Parent root = loader.load();
            PlayerController controller = loader.getController();
            controller.updateMoviesList(moviesTable.getItems());
            controller.setVideo(player);
            Scene s = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Kalivan Player");
            stage.setScene(s);
            stage.show();

            stage.setOnCloseRequest(e -> {
                controller.shutdown();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void newVideoWindow(){

    }
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
    //  return;

    @FXML
    private void continueOrStop() {
        if (player != null) {
            playMusic(player);
        }
    }

    @FXML
    private void nextSong(){
        if (flag && moviesTable.getItems().isEmpty()) return;
        if (!flag && moviesInCategoryList.getItems().isEmpty()) return;

        Movie selectedSong;


        if (flag){
            int index = moviesTable.getSelectionModel().getSelectedIndex();
            //Song selectedSong;
            if (index < moviesTable.getItems().size() - 1) {
                index++;
                moviesTable.getSelectionModel().select(index);
                selectedSong = moviesTable.getSelectionModel().getSelectedItem();
            }
            else {
                index = 0;
                moviesTable.getSelectionModel().select(index);
                selectedSong = moviesTable.getSelectionModel().getSelectedItem();
            }
            Media media = new Media(new File(selectedSong.getPath()).toURI().toString());
            player = new MediaPlayer(media);
        }
        else{
            int index = moviesInCategoryList.getSelectionModel().getSelectedIndex();
            //Song selectedSong;
            if (index < moviesInCategoryList.getItems().size() - 1) {
                index++;
                moviesInCategoryList.getSelectionModel().select(index);
                selectedSong = moviesInCategoryList.getSelectionModel().getSelectedItem();
            }
            else {
                index = 0;
                moviesInCategoryList.getSelectionModel().select(index);
                selectedSong = moviesInCategoryList.getSelectionModel().getSelectedItem();
            }
            Media media = new Media(new File(selectedSong.getPath()).toURI().toString());
            player = new MediaPlayer(media);
        }

        playMusic(player);
    }

    @FXML
    private void previousSong(){
        Movie selectedSong;
        if (flag){
            int index = moviesTable.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                index--;
                moviesTable.getSelectionModel().select(index);
                selectedSong = moviesTable.getSelectionModel().getSelectedItem();
            }
            else {
                index = moviesTable.getItems().size() - 1;
                moviesTable.getSelectionModel().select(index);
                selectedSong = moviesTable.getSelectionModel().getSelectedItem();
            }
            Media media = new Media(new File(selectedSong.getPath()).toURI().toString());
            player = new MediaPlayer(media);
        }
        else{
            int index = moviesInCategoryList.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                index--;
                moviesInCategoryList.getSelectionModel().select(index);
                selectedSong = moviesInCategoryList.getSelectionModel().getSelectedItem();
            }
            else {
                index = moviesInCategoryList.getItems().size() - 1;
                moviesInCategoryList.getSelectionModel().select(index);
                selectedSong = moviesInCategoryList.getSelectionModel().getSelectedItem();
            }
            Media media = new Media(new File(selectedSong.getPath()).toURI().toString());
            player = new MediaPlayer(media);
        }

        playMusic(player);
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
            Object obj = moviesTable.getSelectionModel().getSelectedItem();
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
            Object obj = CategoriesTable.getSelectionModel().getSelectedItem();
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
        Movie movie = moviesTable.getSelectionModel().getSelectedItem();
        Category category = CategoriesTable.getSelectionModel().getSelectedItem();
        if (movie != null && category != null) {
            logic.addSongToPlaylist(category, movie);
        }
        displayCategories(logic.loadCategories());
        displaySongsInPlaylist(category);
    }

    @FXML
    private void deleteSongFomPlaylist() {

        Movie movie = moviesInCategoryList.getSelectionModel().getSelectedItem();
        //Playlist playlist = CategoriesTable.getSelectionModel().getSelectedItem();
        if (movie != null && selected_playlist != null) {
            player.stop();
            player = null;
            int id = selected_playlist.getID();
            logic.deleteSongFromPlaylist(movie, selected_playlist);
            List<Category> playlists = logic.loadCategories();
            displayCategories(playlists);
            for (Category playlst : playlists) {
                if (id == playlst.getID()) {
                    displaySongsInPlaylist(playlst);
                    CategoriesTable.getSelectionModel().select(playlst);
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

                    if (song.getName() != null && song.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } //else if (song.getArtist() != null && song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    //return true;
                    //}
                    return false;
                });
            });
        }

        SortedList<Movie> sortedData = new SortedList<>(filteredSongs);
        sortedData.comparatorProperty().bind(moviesTable.comparatorProperty());

        movieTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        movieDuration.setCellValueFactory(new PropertyValueFactory<>("time"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        moviesTable.setItems(sortedData);
    }

    private void displayCategories(List<Category> playlists) {
        ObservableList<Category> categoriesList = FXCollections.observableArrayList();
        categoriesList.addAll(playlists);
        categoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        moviesAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        CategoriesTable.setItems(categoriesList);
    }


    public void getNewSongData(String title, int time, int rating, File file) {
        logic.saveSong(title, time, rating, file);
        displayMovies(logic.loadMovies());
    }



    public void getEditSongData(String title, String artist, String category, Movie obj) {
        logic.editSong(title, artist, category,  obj);
        displayMovies(logic.loadMovies());
        String name = obj.getName();
        List<Category> playlists = logic.loadCategories();
        displayCategories(playlists);
        CategoriesTable.getSelectionModel().clearSelection();
        moviesInCategoryList.getItems().clear();

    }

    public void getEditPlaylistData(Category obj, String name) throws IOException {
        logic.editPlaylist(name, obj);
        displayCategories(logic.loadCategories());
    }

    public void getNewPlayListData(String name) {
        logic.saveCategory(name);
        displayCategories(logic.loadCategories());
    }

    @FXML
    private void onDeleteSongClick() {
        Movie selectedSong = moviesTable.getSelectionModel().getSelectedItem();

//        if (selectedSong != null && player != null && lblCurrentSong.getText().contains(selectedSong.getName())) {
//            player.stop();
//            player.dispose();
//            player = null;
//            lblCurrentSong.setText("-");
//        }

        if (selectedSong != null) {
            //Platform.runLater(() -> logic.deleteSong(selectedSong));
            logic.deleteSong(selectedSong);
            moviesTable.getSelectionModel().clearSelection();
            displayMovies(logic.loadMovies());
            displayCategories(logic.loadCategories());
            if (selected_playlist!=null){
                displaySongsInPlaylist(selected_playlist);
            }

        }
    }

    @FXML
    private void onDeletePlaylistClick() {
        Category selectedPlaylist = CategoriesTable.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            logic.deletePlaylist(selectedPlaylist);
            displayCategories(logic.loadCategories());
            moviesInCategoryList.getItems().clear();
        }
    }

    @FXML
    private void moveSongUp() {
        int index = moviesInCategoryList.getSelectionModel().getSelectedIndex();
        Category currentPlaylist = CategoriesTable.getSelectionModel().getSelectedItem();

        if (index > 0 && currentPlaylist != null) {
            List<Movie> songs = currentPlaylist.getSongsList();
            Movie temp = songs.get(index);
            songs.set(index, songs.get(index - 1));
            songs.set(index - 1, temp);

            ObservableList<Movie> items = moviesInCategoryList.getItems();
            Movie item = items.get(index);
            items.remove(index);
            items.add(index - 1, item);

            moviesInCategoryList.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void moveSongDown() {
        int index = moviesInCategoryList.getSelectionModel().getSelectedIndex();
        Category currentPlaylist = CategoriesTable.getSelectionModel().getSelectedItem();
        ObservableList<Movie> items = moviesInCategoryList.getItems();

        if (index >= 0 && index < items.size() - 1 && currentPlaylist != null) {
            List<Movie> songs = currentPlaylist.getSongsList();
            Movie temp = songs.get(index);
            songs.set(index, songs.get(index + 1));
            songs.set(index + 1, temp);

            Movie item = items.get(index);
            items.remove(index);
            items.add(index + 1, item);

            moviesInCategoryList.getSelectionModel().select(index + 1);
        }
    }

}

