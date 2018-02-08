package business;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nuno on 05/02/2018.
 */
public class Server implements Runnable{

    private Game game;
    private boolean player;
    private ServerSocket serverSocket;
    private Socket socketC;

    public Server(ServerSocket serverSocket, Game game){
        this.serverSocket = serverSocket;
        this.game = game;
        player = false;
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        try {
            while (true)
                new ServerWorker(serverSocket.accept());
        }
        catch (Exception e) {
        }
        finally {
            try {
                socketC.close();
            }
            catch (Exception e) {
            }
        }
    }

    public class ServerWorker implements Runnable, Observer{

        private BufferedReader in;
        private BufferedWriter out;
        private boolean confirmation;

        public ServerWorker(Socket socket) throws IOException {
            if (!player) {
                socketC = socket;
                game.addObserver(this);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                confirmation = true;
                (new Thread(this)).start();
            }
        }

        @Override
        public void run() {
            try {
                player = true;
                update();
                String message;
                while ((message = in.readLine()) != null && !message.equals("Exit")) {
                    String info[] = message.split(" ");

                    if (info[0].equals("Move")){
                        game.move(info[1].charAt(0), '2');
                    }

                    if (info[0].equals("Send"))
                        sendMapObject();

                    if (info[0].equals("Confirm"))
                        confirmation = true;
                }
            }
            catch(IOException e){
                System.err.println("Connection closed");
            }
            finally {
                player = false;
            }
        }

        public void sendMapObject(){
            try {
                game.lock();
                ObjectOutputStream oos = new ObjectOutputStream(socketC.getOutputStream());
                oos.writeObject(game.getCurrentMap());
                oos.flush();
                game.unlock();
            }
            catch (IOException e) {
            }
        }

        public void update(){
            try {
                if (confirmation) {
                    out.write("MAP " + game.getCurrentMapNumber() + " " +
                                          game.getNMoves() + " " +
                                          game.getCurrentMapHighscore());
                    out.newLine();
                    out.flush();
                    confirmation = false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            update();
        }
    }
}
