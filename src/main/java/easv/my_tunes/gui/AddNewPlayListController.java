package easv.my_tunes.gui;

import easv.my_tunes.be.Category;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddNewPlayListController implements OtherWindow {

    private MainController mainController;
    private String type;

    @FXML
    private TextField nameField;

    private Category obj;

    @FXML
    private void onCancelButton(){
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public void getObject(Object obj){
        this.obj = (Category) obj;
    }

    public void getType(String type) {
        this.type = type;
    }

    @FXML
    private void onSaveButton() throws IOException {
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
