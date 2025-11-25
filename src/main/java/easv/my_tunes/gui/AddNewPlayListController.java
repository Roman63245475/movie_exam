package easv.my_tunes.gui;

import easv.my_tunes.be.Playlist;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewPlayListController implements OtherWindow {

    private MainController mainController;
    private String type;

    @FXML
    private TextField nameField;

    private Playlist obj;

    @FXML
    private void onCancelButton(){
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public void getObject(Object obj){
        this.obj = (Playlist) obj;
    }

    public void getType(String type) {
        this.type = type;
    }

    @FXML
    private void onSaveButton(){
        String name = nameField.getText();
        if (!name.trim().isEmpty()){
            if (type.equals("New")){
                mainController.getNewPlayListData(name);
                onCancelButton();
            }
            else{
                mainController.getEditPlaylistData(obj, name);
                onCancelButton();
            }
        }
    }

    public void getMainController(MainController mainController){
        this.mainController = mainController;
    }


}
