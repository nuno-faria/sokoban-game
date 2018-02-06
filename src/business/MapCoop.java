package business;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nuno on 05/02/2018.
 */
public class MapCoop extends Map implements Serializable{

    private Point2D guestPlayer;
    private char guestPlayerDirection;

    public MapCoop(ArrayList<String> m) {
        super(m);
        guestPlayerDirection = 'R';
    }

    public Point2D getGuestPlayer(){
        return guestPlayer;
    }

    public char getGuestPlayerDirection() {
        return guestPlayerDirection;
    }

    public void setGuestPlayerDirection(char p){
        guestPlayerDirection = p;
    }

    @Override
    protected void parseCoords(ArrayList<String> coords){
        guestPlayer = new Point2D.Double();
        for (int i=0; i<coords.size(); i++){
            String coord[] = coords.get(i).split(" ");
            int x = Integer.parseInt(coord[0]);
            int y = Integer.parseInt(coord[1]);
            if (i == 0)
                player.setLocation(x, y);
            else if (i == 1)
                guestPlayer.setLocation(x, y);
            else boxes.add(new Point2D.Double(x, y));
        }
    }

    @Override
    protected boolean barrier(Point2D p) {
        char c = charAt(p);
        if (c == '#' || c == 'U' || c == 'D' || c == 'L' || c == 'R' || super.existsBox(p)
                || (c == '$' && !pressurePadActivated) || p.equals(player) || p.equals(guestPlayer))
            return true;
        else return false;
    }

    @Override
    protected void updatePressurePad() {
        if (player.equals(pressurePad) || guestPlayer.equals(pressurePad) || boxes.contains(pressurePad))
            pressurePadActivated = true;
        else pressurePadActivated = false;
    }

    @Override
    protected void addMove(){
        history.add(new State(player, guestPlayer, boxes));
    }

    @Override
    public boolean undo(){
        int size = history.size();
        if (size > 0) {
            guestPlayer = history.get(size-1).getGuestPlayer();
            return super.undo();
        }
        else return false;
    }

    @Override
    public boolean reset(){
        if (history.size() > 0) {
            guestPlayer = history.get(0).getGuestPlayer();
            return super.reset();
        }
        return false;
    }
}
