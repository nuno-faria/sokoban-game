package business;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nuno on 05/02/2018.
 */
public class Server{

    private Game game;

    public Server(int port, Game game){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            new ServerWorker(serverSocket.accept(), game);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerWorker implements Runnable, Observer{

        private Socket socket;
        private Game game;
        private BufferedReader in;
        private BufferedWriter out;

        public ServerWorker(Socket socket, Game game) throws IOException {
            this.socket = socket;
            this.game = game;
            this.game.addObserver(this);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            (new Thread(this)).start();
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null){

                    String info[] = message.split(" ");

                    if (info[0].equals("Move"))
                        game.move(info[1].charAt(0), '2');

                    if (info[0].equals("Send"))
                        sendMapObject();
                }
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void sendMapObject(){
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(game.getCurrentMap());
                oos.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            try {
                out.write("MAP " + game.getCurrentMapNumber() + " " +
                                      game.getNMoves() + " " +
                                      game.getCurrentMapHighscore());
                out.newLine();
                out.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
