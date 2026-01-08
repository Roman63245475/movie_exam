package easv.my_movies.gui;

import easv.my_movies.be.Category;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddNewCategoryController implements OtherWindow {
    @FXML
    private TextField nameField;

    private MainController mainController;
    private String type;
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
                mainController.getNewCategoryData(name);
                onCancelButton();
            }
            else{
                mainController.getEditCategoryData(obj, name);
                onCancelButton();
            }
        }
    }

    public void getMainController(MainController mainController){
        this.mainController = mainController;
    }
}
