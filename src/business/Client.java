package business;

import Main.Main;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nuno on 05/02/2018.
 */
public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Game game;

    public Client(String ip, int port) throws IOException {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            game = Main.game;
            new ClientWorker();
    }

    public void sendMessage(String message){
        try {
            out.write(message);
            out.newLine();
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientWorker implements Runnable{

        public ClientWorker(){
            (new Thread(this)).start();
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null){
                    String info[] = message.split(" ");

                    if (info[0].equals("MAP")){
                        int currentMap = Integer.parseInt(info[1]);
                        int nMoves = Integer.parseInt(info[2]);
                        int highscore = Integer.parseInt(info[3]);
                        sendMessage("Send");
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Map map = (MapCoop) ois.readObject();
                        game.updateMap(currentMap, map, nMoves, highscore);
                    }
                }
                socket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}