package ui;

import Main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Nuno on 06/02/2018.
 */
public class Menu implements Initializable {

    @FXML private TextField ipInput;
    @FXML private Button singleButton;
    @FXML private Button hostButton;
    @FXML private Button guestButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        singleButton.setOnAction(event -> {
            Main.startSingle();
        });

        hostButton.setOnAction(event -> {
            Main.startHost();
        });

        guestButton.setOnAction(event -> {
            Main.startGuest(ipInput.getText());
        });
    }
}
