package easv.my_movies.gui;

import easv.my_movies.be.Category;
import easv.my_movies.be.Movie;
import easv.my_movies.bll.Logic;
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
    private TableView<Movie> moviesTable;

    @FXML
    private TableColumn<Movie, Integer> ratingColumn;

    @FXML
    private TableColumn<Movie, String> movieTitle;

    @FXML
    private TableColumn<Movie, String> movieDuration;

    @FXML
    private Button EditCategoryButton;

    @FXML
    private Button EditMovieButton;

    @FXML
    private TableView<Category> CategoriesTable;

    @FXML
    private ListView<Movie> moviesInCategoryList;

    @FXML
    private TableColumn<Category, String> categoryName;

    @FXML
    private TableColumn<Category, Integer> moviesAmount;

    @FXML
    private TextField filterTextField;

    private Logic logic;
    private Category selected_Category;
    private FilteredList<Movie> filteredMovies;
    private MediaPlayer player;
    //private boolean flag;

    @FXML
    private void onCloseClick(){
        return;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.logic = new Logic();
        moviesTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> moviesTable.requestFocus());
        CategoriesTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> CategoriesTable.requestFocus());
        List<Movie> Movies = logic.loadMovies();
        List<Category> categories = logic.loadCategories();
        displayMovies(Movies);
        displayCategories(categories);
        setActionOnSelectedItemTableView();
        setActionOnSelectedItemListView();
        setActionOnSelectedItemTableViewMovies();
    }

    private void setActionOnSelectedItemTableViewMovies() {
        moviesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //flag = true;
                moviesInCategoryList.getSelectionModel().clearSelection();
                Media media = new Media(new File(newValue.getPath()).toURI().toString());
                player = new MediaPlayer(media);
            }
        });
    }

    private void setActionOnSelectedItemListView() {
        moviesInCategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //flag = false;
                moviesTable.getSelectionModel().clearSelection();
                Media media = new Media(new File(newValue.getPath()).toURI().toString());
                player = new MediaPlayer(media);
            }
        });
    }

    private void setActionOnSelectedItemTableView() {
        CategoriesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selected_Category = newValue;
                displayMoviesInCategory(newValue);
            }
        });
    }

    private void displayMoviesInCategory(Category Category) {
        ObservableList<Movie> lst = FXCollections.observableArrayList();
        lst.addAll(logic.getMoviesOnCategory(Category));
        moviesInCategoryList.setItems(lst);
    }

    @FXML
    private void playMovie(MediaPlayer player) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("player-view.fxml"));
            Parent root = loader.load();
            PlayerController controller = loader.getController();
            controller.updateMoviesList(moviesTable.getItems());
            Scene s = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Kalivan Player");
            stage.setScene(s);
            controller.setVideo(player);
            stage.show();

            stage.setOnCloseRequest(e -> {
                controller.shutdown();
                moviesTable.getSelectionModel().clearSelection();
                nullPlayer();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void nullPlayer(){
        player = null;
    }

    @FXML
    private void launchVideo() {
        if (player != null) {
            playMovie(player);
        }
    }

    @FXML
    private void onNewOrEditMovieClick(ActionEvent actionEvent) {
        if (player != null) {
            player.stop();
            player = null;
        }
        Object source = actionEvent.getSource();
        String actionType = "";
        if (source == EditMovieButton) {
            Object obj = moviesTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                actionType = "Edit";
                newWindow("Movie", actionType, obj);
            }
        } else {
            actionType = "New";
            newWindow("Movie", actionType, null);
        }

    }

    @FXML
    private void addNewOrEditCategory(ActionEvent actionEvent) {
        if (player != null) {
            MediaPlayer.Status status = player.getStatus();
            if (status == MediaPlayer.Status.PLAYING ||
                    status == MediaPlayer.Status.PAUSED ||
                    status == MediaPlayer.Status.READY) {
                player.stop();
            }
            player.dispose();
            player = null;
            moviesTable.getSelectionModel().clearSelection();
        }

        Object source = actionEvent.getSource();
        String actionType = "";
        if (source == EditCategoryButton) {
            Object obj = CategoriesTable.getSelectionModel().getSelectedItem();
            if (obj != null) {
                actionType = "Edit";
                newWindow("Category", actionType, obj);
            }
        } else {
            actionType = "New";
            newWindow("Category", actionType, null);
        }

    }

    private void newWindow(String type, String actionType, Object obj) {
        String fileName = (type.equals("Category") ? "add-new-category.fxml" : "new-movie-window.fxml");
        String title = (type.equals("Category") ? "New/Edit Category" : "New/Edit Movie");
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
    private void addMovieToCategory() {
        Movie movie = moviesTable.getSelectionModel().getSelectedItem();
        Category category = CategoriesTable.getSelectionModel().getSelectedItem();
        if (movie != null && category != null) {
            logic.addMovieToCategory(category, movie);
            displayMoviesInCategory(category);
        }
        displayCategories(logic.loadCategories());
    }

    @FXML
    private void deleteMovieFomCategory() {

        Movie movie = moviesInCategoryList.getSelectionModel().getSelectedItem();
        //Category Category = CategoriesTable.getSelectionModel().getSelectedItem();
        if (movie != null && selected_Category != null) {
            player.stop();
            player = null;
            int id = selected_Category.getID();
            logic.deleteMovieFromCategory(movie, selected_Category);
            List<Category> Categorys = logic.loadCategories();
            displayCategories(Categorys);
            for (Category playlst : Categorys) {
                if (id == playlst.getID()) {
                    displayMoviesInCategory(playlst);
                    CategoriesTable.getSelectionModel().select(playlst);
                }
            }
        }
    }

    private void displayMovies(List<Movie> Movies) {
        ObservableList<Movie> movieList = FXCollections.observableArrayList();
        movieList.addAll(Movies);

        filteredMovies = new FilteredList<>(movieList, b -> true);

        if (filterTextField != null) {
            filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredMovies.setPredicate(Movie -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (Movie.getName() != null && Movie.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } //else if (Movie.getArtist() != null && Movie.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    //return true;
                    //}
                    return false;
                });
            });
        }

        SortedList<Movie> sortedData = new SortedList<>(filteredMovies);
        sortedData.comparatorProperty().bind(moviesTable.comparatorProperty());

        movieTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        movieDuration.setCellValueFactory(new PropertyValueFactory<>("time"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        moviesTable.setItems(sortedData);
    }

    private void displayCategories(List<Category> Categorys) {
        ObservableList<Category> categoriesList = FXCollections.observableArrayList();
        categoriesList.addAll(Categorys);
        categoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        moviesAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        CategoriesTable.setItems(categoriesList);
    }

    public void getNewMovieData(String title, int time, int rating, File file) {
        logic.saveMovie(title, time, rating, file);
        displayMovies(logic.loadMovies());
    }

    public void getEditMovieData(String title, int rating, Movie obj) {
        logic.editMovie(title, rating,  obj);
        displayMovies(logic.loadMovies());
        List<Category> Categorys = logic.loadCategories();
        displayCategories(Categorys);
        CategoriesTable.getSelectionModel().clearSelection();
        moviesInCategoryList.getItems().clear();
    }

    public void getEditCategoryData(Category obj, String name) throws IOException {
        logic.editCategory(name, obj);
        displayCategories(logic.loadCategories());
    }

    public void getNewCategoryData(String name) {
        logic.saveCategory(name);
        displayCategories(logic.loadCategories());
    }

    @FXML
    private void onDeleteMovieClick() {
        Movie selectedMovie = moviesTable.getSelectionModel().getSelectedItem();

//        if (selectedMovie != null && player != null && lblCurrentMovie.getText().contains(selectedMovie.getName())) {
//            player.stop();
//            player.dispose();
//            player = null;
//            lblCurrentMovie.setText("-");
//        }

        if (selectedMovie != null) {
            //Platform.runLater(() -> logic.deleteMovie(selectedMovie));
            logic.deleteMovie(selectedMovie);
            moviesTable.getSelectionModel().clearSelection();
            displayMovies(logic.loadMovies());
            displayCategories(logic.loadCategories());
            if (selected_Category!=null){
                displayMoviesInCategory(selected_Category);
            }

        }
    }

    @FXML
    private void onDeleteCategoryClick() {
        Category selectedCategory = CategoriesTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            logic.deleteCategory(selectedCategory);
            displayCategories(logic.loadCategories());
            moviesInCategoryList.getItems().clear();
        }
    }
}

