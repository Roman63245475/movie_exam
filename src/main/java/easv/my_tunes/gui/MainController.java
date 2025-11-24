package easv.my_tunes.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.SwipeEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;
    
    @FXML
    private Slider volumeSlider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupVolumeSwipeGesture();
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

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
