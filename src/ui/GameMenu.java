package ui;

import Main.Main;
import business.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nuno on 02/02/2018.
 */
public class GameMenu implements Observer {

    private Stage stage;
    private Scene scene;
    private AnchorPane pane;
    private Game game;
    private int width = 1400;
    private int heigth = 900;
    private Client client;

    public GameMenu(boolean coop) {

        game = Main.game;
        client = Main.client;

        game.addObserver(this);

        stage = new Stage();
        stage.setWidth(1500);
        stage.setHeight(900);
        stage.setResizable(false);
        stage.getIcons().add(new Image("file:resources/box.png"));
        stage.show();
        stage.setOnCloseRequest(event -> {
            if (coop)
                client.sendMessage("Exit");
            else Main.closeServer();
        });

        pane = new AnchorPane();
        pane.setStyle("-fx-background-color: #f8f8e0;");

        scene = new Scene(pane);

        stage.setScene(scene);

        //key handler
        scene.setOnKeyPressed(key -> {
            if (!coop) {
                switch (key.getCode()) {
                    //movement
                    case W: game.move('U'); break;
                    case UP: game.move('U'); break;
                    case S: game.move('D'); break;
                    case DOWN: game.move('D'); break;
                    case A: game.move('L'); break;
                    case LEFT: game.move('L'); break;
                    case D: game.move('R'); break;
                    case RIGHT: game.move('R'); break;

                    //undo
                    case U: game.undo(); break;

                    //reset
                    case R: game.reset(); break;

                    //next map
                    case M: game.nextMap(); break;

                    //prev map
                    case N: game.prevMap(); break;
                }
            }
            else{
                switch (key.getCode()) {
                    case W: client.sendMessage("Move U"); break;
                    case UP: client.sendMessage("Move U"); break;
                    case S: client.sendMessage("Move D"); break;
                    case DOWN: client.sendMessage("Move D"); break;
                    case A: client.sendMessage("Move L"); break;
                    case LEFT: client.sendMessage("Move L"); break;
                    case D: client.sendMessage("Move R"); break;
                    case RIGHT: client.sendMessage("Move R"); break;

                    //exit
                    case ESCAPE: System.exit(0);
                }
            }
        });

        drawMap();
        drawInfo();
    }

    public void drawMap(){

        pane.getChildren().clear();

        int x = 0;
        int y = 0;
        int xStep = 50;
        int yStep = 50;

        Map m = game.getCurrentMap();

        int mapWidth = m.getMap().get(0).length();
        int mapHeigth = m.getMap().size();

        int xBegin = (width - mapWidth * 50) / (2 * 50) - 1;
        int yBegin = (heigth - mapHeigth * 50) / (2 * 50);

        //map
        for (String s: m.getMap()) {
            x = -1;
            for (char c : s.toCharArray()) {
                x++;
                String file;

                switch (c){
                    case '#': file = "file:resources/wall.bmp"; break;
                    case ' ': file = "file:resources/floor.bmp"; break;
                    case '.': file = "file:resources/goal.png"; break;
                    case 'U': file = "file:resources/pipeU.png"; break;
                    case 'D': file = "file:resources/pipeD.png"; break;
                    case 'L': file = "file:resources/pipeL.png"; break;
                    case 'R': file = "file:resources/pipeR.png"; break;
                    case '&': file = "file:resources/pressure_pad.png"; break;
                    case '$': if (!m.isPressurePadActivated())
                                file = "file:resources/gate.bmp";
                              else file = "file:resources/floor.bmp";
                              break;
                    case '%': if (m.isPressurePadActivated())
                                file = "file:resources/negativeGate.png";
                              else file = "file:resources/floor.bmp";
                              break;
                    default: continue;
                }

                ImageView img = new ImageView(new Image(file));
                img.relocate((x + xBegin) * xStep, (y + yBegin) * yStep);
                pane.getChildren().add(img);
            }
            y++;
        }

        //player
        ImageView player = new ImageView(new Image("file:resources/player" + m.getPlayerDirection() + ".png"));
        Point2D p = m.getPlayer();
        player.relocate((p.getX() + xBegin) * xStep, (mapHeigth-1 - p.getY() + yBegin) * yStep);
        pane.getChildren().add(player);

        //guest player
        if (m.isCoop()) {
            ImageView player2 = new ImageView(new Image("file:resources/player2" +
                                                m.getGuestPlayerDirection() + ".png"));
            Point2D p2 = m.getGuestPlayer();
            player2.relocate((p2.getX() + xBegin) * xStep, (mapHeigth - 1 - p2.getY() + yBegin) * yStep);
            pane.getChildren().add(player2);
        }

        //boxes
        for (Point2D b: m.getBoxes()){

            String name = "file:resources/box.png";
            switch (game.getCurrentMap().charAt(b)){
                case '.': name = "file:resources/boxO.png"; break;
                case '&': name = "file:resources/boxP.png"; break;
                case '$': if (!m.isPressurePadActivated())
                            name = "file:resources/box&gate.png";
                          break;
                case '%': if (m.isPressurePadActivated())
                            name = "file:resources/box&negativeGate.png";
                          break;
            }

            ImageView box = new ImageView(new Image(name));
            box.relocate((b.getX() + xBegin) * xStep, (mapHeigth-1 - b.getY() + yBegin) * yStep);
            pane.getChildren().add(box);
        }
    }

    public void drawInfo(){
        ImageView img = new ImageView(new Image("file:resources/frame.png"));
        img.relocate(1210, 200);

        //number moves
        Label info = new Label( "Map number: " + game.getCurrentMapNumber() +
                                "\nNumber moves: " + game.getNMoves() +
                                "\nHighscore: " + game.getCurrentMapHighscore());
        info.setFont(new Font("Consolas", 14));
        info.setTextFill(Color.LIME);
        info.relocate(1250, 300);

        //controls
        Label controls = new Label("\n\nWASD/keys : move" +
                                    "\nN/M : prev/next level" +
                                    "\nU/R : undo/reset" +
                                    "\nEsc : exit");
        controls.setFont(new Font("Consolas", 10));
        controls.setTextFill(Color.WHITE);
        controls.relocate(1250, 330);


        pane.getChildren().addAll(img, info, controls);
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater( () -> {
            drawMap();
            drawInfo();
        });
    }
}
