package Main;

import business.Client;
import business.Game;
import business.Server;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.GameMenu;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Main extends Application{

    public static Game game;
    public static Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {
        startSingle();
        //startHost();
        //startGuest();
    }

    public void startSingle(){
        String filename = "resources/maps.txt";
        game = new Game(filename, false);
        new GameMenu(false);
    }

    public void startHost(){
        String filename = "resources/maps-coop.txt";
        game = new Game(filename, true);
        new Server(12346, game);
        new GameMenu(false);
    }

    public void startGuest(){
        try {
            String filename = "resources/maps-coop.txt";
            game = new Game(filename, true);
            client = new Client("127.0.0.1", 12346);
            new GameMenu(true);
        }
        catch (Exception e){
            System.err.println("Server not found.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
