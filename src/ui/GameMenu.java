package ui;

import business.Map;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nuno on 02/02/2018.
 */
public class GameMenu implements Observer {

    private Stage stage;
    private int width = 1400;
    private int heigth = 900;
    private char direction;

    public GameMenu() {

        Main.game.addObserver(this);

        stage = new Stage();
        stage.setWidth(1500);
        stage.setHeight(900);
        stage.setResizable(false);
        stage.show();

        drawMap();

        //button handlers
    }

    public void drawMap(){
        int x = 0;
        int y = 0;
        int xStep = 50;
        int yStep = 50;

        Map m = Main.game.getCurrentMap();

        int mapWidth = m.getMap().get(0).length();
        int mapHeigth = m.getMap().size();

        int xBegin = (width - mapWidth * 50) / (2 * 50);
        int yBegin = (heigth - mapHeigth * 50) / (2 * 50);

        AnchorPane pane = new AnchorPane();

        //map
        for (String s: m.getMap()) {
            x = -1;
            for (char c : s.toCharArray()) {
                x++;
                ImageView img;

                if (c == '#')
                    img = new ImageView(new Image("file:resources/wall.bmp"));

                else if (c == ' ')
                    img = new ImageView(new Image("file:resources/floor.bmp"));

                else if (c == '.')
                    img = new ImageView(new Image("file:resources/goal.png"));

                else continue;

                img.relocate((x + xBegin) * xStep, (y + yBegin) * yStep);
                pane.getChildren().add(img);
            }
            y++;
        }

        //player
        ImageView player = new ImageView(new Image("file:resources/player_r.png"));
        Point2D p = m.getPlayer();
        player.relocate((p.getX() + xBegin) * xStep, (mapHeigth-1 - p.getY() + yBegin) * yStep);
        pane.getChildren().add(player);

        //boxes
        for (Point2D b: m.getBoxes()){
            ImageView box = new ImageView(new Image("file:resources/box.png"));
            box.relocate((b.getX() + xBegin) * xStep, (mapHeigth-1 - b.getY() + yBegin) * yStep);
            pane.getChildren().add(box);
        }

        pane.setStyle("-fx-background-color: #f8f8e0;");
        Scene scene = new Scene(pane);
        stage.setScene(scene);
    }

    @Override
    public void update(Observable o, Object arg) {
        drawMap();
    }
}
