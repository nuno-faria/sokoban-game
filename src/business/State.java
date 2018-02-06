package business;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Nuno on 02/02/2018.
 */
public class State implements Serializable {

    private Point2D player;
    private ArrayList<Point2D> boxes;
    private Point2D guestPlayer;

    public State(Point2D player, ArrayList<Point2D> boxes){
        this.player = (Point2D) player.clone();
        this.boxes = boxes.stream()
                          .map(p -> ((Point2D) p.clone()))
                          .collect(Collectors.toCollection(ArrayList::new));
    }

    public State(Point2D player, Point2D guestPlayer, ArrayList<Point2D> boxes){
        this.player = (Point2D) player.clone();
        this.guestPlayer = (Point2D) guestPlayer.clone();
        this.boxes = boxes.stream()
                          .map(p -> ((Point2D) p.clone()))
                          .collect(Collectors.toCollection(ArrayList::new));
    }

    public Point2D getPlayer() {
        return player;
    }

    public ArrayList<Point2D> getBoxes() {
        return boxes;
    }

    public Point2D getGuestPlayer(){
        return guestPlayer;
    }
}
