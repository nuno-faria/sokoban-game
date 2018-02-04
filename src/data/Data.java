package data;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Nuno on 04/02/2018.
 */
public class Data {

    public static void saveHighscore(HashMap<Integer, Integer> highscore){
        try {
            FileOutputStream fos = new FileOutputStream("score.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(highscore);
            oos.flush();
            oos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Integer, Integer> loadHighscore(){
        try {
            FileInputStream fis = new FileInputStream("score.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<Integer, Integer> highscore = (HashMap<Integer, Integer>) ois.readObject();
            ois.close();
            return highscore;
        }
        catch (Exception e) {
            System.err.println("No 'score.dat' found.");
        }
        return null;
    }
}
