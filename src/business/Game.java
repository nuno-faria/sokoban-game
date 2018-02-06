package business;

import data.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Game extends Observable implements Serializable{

    private ArrayList<Map> maps;
    private HashMap<Integer, Integer> highscore;
    private HashMap<Integer, Integer> currentNumberMoves;
    private int currentMap;

    public Game(String filename, boolean coop){

        maps = new ArrayList<>();
        currentMap = 0;

        highscore = Data.loadHighscore();
        if (highscore == null)
            highscore = new HashMap<>();

        //parser
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = "";
            int i = 0;
            while (line != null) {
                ArrayList<String> m = new ArrayList<>();
                while ((line = in.readLine()) != null && !line.equals("MAPBREAK"))
                    m.add(line);


                Map map;
                if (coop) map = new MapCoop(m);
                else map = new Map(m);

                maps.add(map);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        currentNumberMoves = new HashMap<>();
        for (int i=0; i<maps.size(); i++)
            currentNumberMoves.put(i, 0);
    }

    public Map getCurrentMap(){
        return maps.get(currentMap);
    }

    public void move(char c){
        move(c, '1');
    }

    public synchronized void move(char c, char p){
        if (maps.get(currentMap).move(c, p)) {
            if (maps.get(currentMap).isLevelCompleted()) {

                //save new highscore if its better than previous
                int hash = getCurrentMap().getMap().hashCode();
                int nMoves = currentNumberMoves.get(currentMap);
                if (!highscore.containsKey(hash) || nMoves < highscore.get(hash)) {
                    highscore.put(hash, nMoves);
                    Data.saveHighscore(highscore);
                }
                reset();
                currentMap++;
            }
            else currentNumberMoves.put(currentMap, currentNumberMoves.get(currentMap) + 1);
            setUpdated();
        }
    }

    public void undo(){
        if(maps.get(currentMap).undo()) {
            currentNumberMoves.put(currentMap, currentNumberMoves.get(currentMap) - 1);
            setUpdated();
        }
    }

    public void reset(){
        if (maps.get(currentMap).reset()) {
            currentNumberMoves.put(currentMap, 0);
            setUpdated();
        }
    }

    public void nextMap(){
        if (maps.size() - 1 > currentMap) {
            reset();
            currentMap++;
            setUpdated();
        }
    }

    public void prevMap(){
        if (currentMap > 0){
            reset();
            currentMap--;
            setUpdated();
        }
    }

    public int getNMoves(){
        return currentNumberMoves.get(currentMap);
    }

    public int getCurrentMapNumber(){
        return currentMap;
    }

    public String getCurrentMapHighscore(){
        int hash = getCurrentMap().getMap().hashCode();
        if (highscore.containsKey(hash))
            return highscore.get(hash).toString();
        else return "-";
    }

    public void updateMap(int num, Map map, int nMoves, int hscore){
        maps.set(num, map);
        currentMap = num;
        currentNumberMoves.put(nMoves, nMoves);
        highscore.put(num, hscore);
        setUpdated();
    }

    public void setUpdated(){
        setChanged();
        notifyObservers();
    }
}
