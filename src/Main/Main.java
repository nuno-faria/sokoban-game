package Main;

import business.Client;
import business.Game;
import business.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.GameMenu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Main extends Application{

    public static Game game;
    public static Client client;
    private static ServerSocket serverSocket;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("prism.lcdtext", "false");
        Parent parent = FXMLLoader.load(new URL("file:src/ui/Menu.fxml"));
        Scene scene = new Scene(parent);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("Sokoban");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(false);
        window.getIcons().add(new Image("file:resources/box.png"));
        window.show();
        window.setOnCloseRequest(event -> System.exit(0));
    }

    public static void startSingle(){
        String filename = "resources/maps.txt";
        game = new Game(filename, false);
        new GameMenu(false);
    }

    public static void startHost(){
        try {
            String filename = "resources/maps-coop.txt";
            game = new Game(filename, true);
            serverSocket = new ServerSocket(12346);
            new Server(serverSocket, game);
            new GameMenu(false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startGuest(String ip){
        try {
            String filename = "resources/maps-coop.txt";
            game = new Game(filename, true);
            client = new Client(ip, 12346);
            new GameMenu(true);
        }
        catch (Exception e){
            System.err.println("Server not found.");
        }
    }

    public static void closeServer(){
        try {
            if (serverSocket != null)
                serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
