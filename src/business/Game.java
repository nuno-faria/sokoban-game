package business;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Nuno on 02/02/2018.
 */
public class Game extends Observable {

    private ArrayList<Map> maps;
    private int currentMap;

    public Game(String filename){

        maps = new ArrayList<>();
        currentMap = 0;

        //parser
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = "";
            int i = 0;
            while (line != null) {
                ArrayList<String> m = new ArrayList<>();
                while ((line = in.readLine()) != null && !line.equals("MAPBREAK"))
                    m.add(line);

                Map map = new Map(m);
                maps.add(map);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map getCurrentMap(){
        return maps.get(currentMap);
    }
}
