package easv.my_tunes.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewPlayListController implements OtherWindow {

    private MainController mainController;

    @FXML
    private TextField nameField;

    @FXML
    private void onCancelButton(){
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSaveButton(){
        String name = nameField.getText();
        if (!name.trim().isEmpty()){
            mainController.getNewPlayListData(name);
            onCancelButton();
        }
    }

    public void getMainController(MainController mainController){
        this.mainController = mainController;
    }


}
