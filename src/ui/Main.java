package ui;

import business.Game;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Main extends Application{

    public static Game game;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String filename = "resources/maps.txt";
        game = new Game(filename);
        new GameMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
