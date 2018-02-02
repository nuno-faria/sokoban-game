package business;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Map {

    private ArrayList<String> map;
    private Point2D player;
    private ArrayList<Point2D> boxes;
    private ArrayList<State> history;

    public Map(ArrayList<String> m){
        map = new ArrayList<>();
        history = new ArrayList<>();
        player = new Point2D.Float();
        boxes = new ArrayList<>();

        boolean p = true;
        for (String s: m){

            if (s.startsWith("#") || s.startsWith(" "))
                map.add(s);

            else{
                String coords[] = s.split(" ");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                if (p) {
                    player.setLocation(x, y);
                    p = false;
                }
                else
                    boxes.add(new Point2D.Double(x, y));
            }
        }
    }


    private boolean barrier(Point2D p, ArrayList<Point2D> boxes){
        Character c = map.get((int) p.getY()).charAt((int) p.getX());
        if (c == '#' || existsBox(p, boxes))
            return true;
        else return false;
    }

    private boolean existsBox(Point2D p, ArrayList<Point2D> boxes){
        for (Point2D coord: boxes)
            if (coord.equals(p))
                return true;
        return false;
    }

    private void moveBox(Point2D p, ArrayList<Point2D> boxes, int x, int y){
        for (Point2D coord: boxes)
            if (coord.equals(p)) {
                coord.setLocation(coord.getX() + x, coord.getY() + y);
                return;
            }
    }

    private boolean move(Character c){
        int x = 0;
        int y = 0;

        switch (c){
            case 'U': y = 1; break;
            case 'D': y = -1; break;
            case 'L': x = -1; break;
            case 'R': x = 1; break;
        }

        int px = (int) player.getX() + x;
        int py = (int) player.getY() + y;

        //wall
        if (map.get(py).charAt(px) == '#')
            return false;

        //box and barrier (wall, another box, ...)
        if (existsBox(new Point2D.Double(px, py), boxes)
                && barrier(new Point2D.Double(px + x, py + y), boxes))
            return false;

        moveBox(new Point2D.Double(px, py), boxes, x, y);
        player.setLocation(px, py);

        history.add(new State(player, boxes));

        return true;
    }

    private boolean undo(){
        int size = history.size();
        if (size > 0) {
            player = history.get(size-1).getPlayer();
            boxes = history.get(size-1).getBoxes();
            history.remove(size-1);
            return true;
        }
        else return false;
    }

    public void reset(){
        player = history.get(0).getPlayer();
        boxes = history.get(0).getBoxes();
        history.clear();
    }
}
