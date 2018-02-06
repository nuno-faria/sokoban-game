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
    protected Point2D player;
    protected ArrayList<Point2D> boxes;
    protected ArrayList<State> history;
    protected Point2D pressurePad;
    protected boolean pressurePadActivated;
    private char playerDirection;


    public Map(ArrayList<String> m){
        map = new ArrayList<>();
        pipes = new ArrayList<>();
        history = new ArrayList<>();
        player = new Point2D.Double();
        boxes = new ArrayList<>();
        pressurePadActivated = false;
        playerDirection = 'R';

        ArrayList<String> coords = new ArrayList<>();
        boolean p = true;
        for (String s: m){

            if (s.startsWith("#") || s.startsWith("!"))
                map.add(s);

            else coords.add(s);
        }

        parseCoords(coords);

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

    protected void parseCoords(ArrayList<String> coords){
        for (int i=0; i<coords.size(); i++){
            String coord[] = coords.get(i).split(" ");
            int x = Integer.parseInt(coord[0]);
            int y = Integer.parseInt(coord[1]);
            if (i == 0)
                player.setLocation(x, y);
            else boxes.add(new Point2D.Double(x, y));
        }
    }

    public char charAt(Point2D p){
        return reversedMap.get((int) p.getY()).charAt((int) p.getX());
    }

    protected boolean barrier(Point2D p){
        char c = charAt(p);
        if (c == '#' || c == 'U' || c == 'D' || c == 'L' || c == 'R' || existsBox(p)
                || (c == '$' && !pressurePadActivated))
            return true;
        else return false;
    }

    protected boolean existsBox(Point2D p){
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

    protected void updatePressurePad(){
        if (player.equals(pressurePad) || boxes.contains(pressurePad))
            pressurePadActivated = true;
        else pressurePadActivated = false;
    }

    protected void addMove(){
        history.add(new State(player, boxes));
    }

    protected void updateDirection(char c, char p){
        if (p == '1')
            playerDirection = c;
        else ((MapCoop) this).setGuestPlayerDirection(c);
    }

    public boolean movePipe(Point2D p, int x, int y){
        char c = charAt(p);
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
                if (!pipe.equals(p)){
                    switch (charAt(pipe)){
                        case 'U' : newPos = new Point2D.Double(pipe.getX(), pipe.getY() + 1); break;
                        case 'D' : newPos = new Point2D.Double(pipe.getX(), pipe.getY() - 1); break;
                        case 'L' : newPos = new Point2D.Double(pipe.getX() - 1, pipe.getY()); break;
                        case 'R' : newPos = new Point2D.Double(pipe.getX() + 1, pipe.getY()); break;
                    }
                }
            if (charAt(newPos) == ' ' && !barrier(newPos)){
                addMove();
                player = newPos;
                updatePressurePad();
                return true;
            }
        }
        return false;
    }

    protected boolean move(char c, char p){
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
        else pl = ((MapCoop) this).getGuestPlayer();

        int px = (int) pl.getX() + x;
        int py = (int) pl.getY() + y;

        Point2D nextPos = new Point2D.Double(px, py);
        char nextPosChar = charAt(nextPos);

        //another player
        if (this instanceof MapCoop)
            if (((MapCoop) this).getPlayer().equals(nextPos)
                    || ((MapCoop) this).getGuestPlayer().equals(nextPos))
                return false;

        //wall
        if (nextPosChar == '#')
            return false;

        //box and barrier (wall, another box, ...)
        if (existsBox(nextPos)
                && barrier(new Point2D.Double(px + x, py + y)))
            return false;

        if (charAt(nextPos) == '$' && !pressurePadActivated)
            return false;

        //pipe
        if (nextPosChar == 'U' || nextPosChar == 'D' || nextPosChar == 'L' || nextPosChar == 'R')
            return movePipe(nextPos, x, y);


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
