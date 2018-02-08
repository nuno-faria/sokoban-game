package business;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Map implements Serializable{

    private ArrayList<String> map;
    private ArrayList<String> reversedMap; //because Y is reversed
    private ArrayList<Point2D> pipes;
    private Point2D player;
    private ArrayList<Point2D> boxes;
    private ArrayList<State> history;
    private Point2D pressurePad;
    private boolean pressurePadActivated;
    private char playerDirection;

    private boolean coop;

    //coop vars
    private Point2D guestPlayer;
    private char guestPlayerDirection;


    public Map(ArrayList<String> m, boolean coop){
        map = new ArrayList<>();
        pipes = new ArrayList<>();
        history = new ArrayList<>();
        player = new Point2D.Double();
        boxes = new ArrayList<>();
        pressurePadActivated = false;
        playerDirection = 'R';
        this.coop = coop;
        guestPlayer = new Point2D.Double(-1, -1);
        guestPlayerDirection = 'R';

        ArrayList<String> coords = new ArrayList<>();
        boolean p = true;
        for (String s: m){

            if (s.startsWith("#") || s.startsWith("!"))
                map.add(s);

            else coords.add(s);
        }

        //parse coords
        if (!coop)
            parseCoords(coords);
        else parseCoordsCoop(coords);

        reversedMap = (ArrayList<String>) map.clone();
        Collections.reverse(reversedMap);

        //find the 2 pipes and pressure pad (if they exist)
        for (int i=0; i<map.size(); i++)
            for (int j=0; j<map.get(i).length(); j++) {
                Point2D point = new Point2D.Double(j, i);
                char c = charAt(point);
                if (c == 'U' || c == 'D' || c == 'L' || c == 'R')
                    pipes.add(point);
                else if (c == '&')
                    pressurePad = point;
            }

        updatePressurePad();
    }

    public ArrayList<String> getMap() {
        return map;
    }

    public Point2D getPlayer() {
        return player;
    }

    public ArrayList<Point2D> getBoxes() {
        return boxes;
    }

    public boolean isPressurePadActivated() {
        return pressurePadActivated;
    }

    public char getPlayerDirection(){
        return playerDirection;
    }

    public Point2D getGuestPlayer() {
        return guestPlayer;
    }

    public char getGuestPlayerDirection() {
        return guestPlayerDirection;
    }

    public boolean isCoop(){
        return coop;
    }

    private void parseCoords(ArrayList<String> coords){
        for (int i=0; i<coords.size(); i++){
            String coord[] = coords.get(i).split(" ");
            int x = Integer.parseInt(coord[0]);
            int y = Integer.parseInt(coord[1]);
            if (i == 0)
                player.setLocation(x, y);
            else boxes.add(new Point2D.Double(x, y));
        }
    }

    private void parseCoordsCoop(ArrayList<String> coords){
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

    public char charAt(Point2D p){
        return reversedMap.get((int) p.getY()).charAt((int) p.getX());
    }

    private boolean barrier(Point2D p){
        char c = charAt(p);
        if (c == '#' || c == 'U' || c == 'D' || c == 'L' || c == 'R' || existsBox(p)
                || (c == '$' && !pressurePadActivated) || (c == '%' && pressurePadActivated)
                || p.equals(player) || p.equals(guestPlayer))
            return true;
        else return false;
    }

    private boolean existsBox(Point2D p){
        for (Point2D coord: boxes)
            if (coord.equals(p))
                return true;
        return false;
    }

    public void moveBox(Point2D p, ArrayList<Point2D> boxes, int x, int y){
        for (Point2D coord: boxes)
            if (coord.equals(p)) {
                coord.setLocation(coord.getX() + x, coord.getY() + y);
                return;
            }
    }

    private void updatePressurePad(){
        if (player.equals(pressurePad) || boxes.contains(pressurePad) || guestPlayer.equals(pressurePad))
            pressurePadActivated = true;
        else pressurePadActivated = false;
    }

    private void addMove(){
        history.add(new State(player, guestPlayer, boxes));
    }

    private void updateDirection(char c, char p){
        if (p == '1')
            playerDirection = c;
        else guestPlayerDirection = c;
    }

    public boolean movePipe(Point2D nextPos, int x, int y, char p){
        char c = charAt(nextPos);
        int xp = 0, yp = 0;

        switch (c){
            case 'U': xp = 0; yp = -1; break;
            case 'D': xp = 0; yp = 1; break;
            case 'L': xp = 1; yp = 0; break;
            case 'R': xp = -1; yp = 0; break;
        }

        //find out if player 'entered' the pipe, and if yes, change player position
        if (x == xp && y == yp){
            Point2D newPos = null;
            for (Point2D pipe: pipes)
                if (!pipe.equals(nextPos)){
                    switch (charAt(pipe)){
                        case 'U' : newPos = new Point2D.Double(pipe.getX(), pipe.getY() + 1); break;
                        case 'D' : newPos = new Point2D.Double(pipe.getX(), pipe.getY() - 1); break;
                        case 'L' : newPos = new Point2D.Double(pipe.getX() - 1, pipe.getY()); break;
                        case 'R' : newPos = new Point2D.Double(pipe.getX() + 1, pipe.getY()); break;
                    }
                }
            if (charAt(newPos) == ' ' && !barrier(newPos)){
                addMove();
                if (p == '1')
                    player = newPos;
                else guestPlayer = newPos;
                updatePressurePad();
                return true;
            }
        }
        return false;
    }

    public boolean move(char c, char p){
        int x = 0;
        int y = 0;

        switch (c){
            case 'U': y = 1; break;
            case 'D': y = -1; break;
            case 'L': x = -1; break;
            case 'R': x = 1; break;
        }

        Point2D pl;
        if (p == '1') pl = player;
        else pl = guestPlayer;

        int px = (int) pl.getX() + x;
        int py = (int) pl.getY() + y;

        Point2D nextPos = new Point2D.Double(px, py);
        char nextPosChar = charAt(nextPos);

        //another player
        if (coop && (player.equals(nextPos) || guestPlayer.equals(nextPos)))
            return false;

        //wall
        if (nextPosChar == '#')
            return false;

        //box and barrier (wall, another box, ...)
        if (existsBox(nextPos)
                && barrier(new Point2D.Double(px + x, py + y)))
            return false;

        //gate and pressure pad deactivated
        if (charAt(nextPos) == '$' && !pressurePadActivated)
            return false;

        //negative gate a pressure pad activated
        if (charAt(nextPos) == '%' && pressurePadActivated)
            return false;

        //pipe
        if (nextPosChar == 'U' || nextPosChar == 'D' || nextPosChar == 'L' || nextPosChar == 'R')
            return movePipe(nextPos, x, y, p);


        //valid move

        addMove();
        moveBox(new Point2D.Double(px, py), boxes, x, y);
        pl.setLocation(px, py);
        updatePressurePad();
        updateDirection(c, p);

        return true;
    }

    public boolean undo(){
        int size = history.size();
        if (size > 0) {
            player = history.get(size-1).getPlayer();
            guestPlayer = history.get(size-1).getGuestPlayer();
            boxes = history.get(size-1).getBoxes();
            history.remove(size-1);
            updatePressurePad();
            return true;
        }
        else return false;
    }

    public boolean reset(){
        if (history.size() > 0) {
            player = history.get(0).getPlayer();
            guestPlayer = history.get(0).getGuestPlayer();
            boxes = history.get(0).getBoxes();
            history.clear();
            updatePressurePad();
            return true;
        }
        return false;
    }

    public boolean isLevelCompleted(){
        return boxes.stream()
                    .filter(b -> charAt(b) == '.')
                    .count() == boxes.size();
    }
}
