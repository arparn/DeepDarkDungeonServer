import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MPServer {
    int udpC = 5200;
    int tcpC = 5291;
    String IPConnection = "localhost";
    Server server;

    private int whoseTurnToAttack = 0;
    private List<List<String>> players = new LinkedList<>();
    private Packets.GameInfo turnInfo = new Packets.GameInfo();

    public MPServer() throws IOException {
        server = new Server();
        server.addListener(new Listener(){
            public void connected(Connection c) {
                System.out.println("Someone has connected");
            }

            public void disconnected(Connection c) {
                System.out.println("Someone has disconnected");
            }

            public void received(Connection c, Object o) {

                if (o instanceof Packets.ConnectToGame) {
                    if (players.size() == 0) {
                        players.add(((Packets.ConnectToGame) o).characters);
                        Packets.ConnectToGame newO = new Packets.ConnectToGame();
                        newO.place = 0;
                        newO.characters = ((Packets.ConnectToGame) o).characters;
                        c.sendTCP(newO);
                    } else if (players.size() == 1) {
                        players.add(((Packets.ConnectToGame) o).characters);
                        Packets.ConnectToGame newO = new Packets.ConnectToGame();
                        newO.place = 1;
                        newO.characters = ((Packets.ConnectToGame) o).characters;
                        c.sendTCP(newO);
                    } else c.close();


                } else if (o instanceof Packets.AllowToStart) {
                    if (players.size() == 2) {
                        Packets.AllowToStart newO = new Packets.AllowToStart();
                        newO.allow = true;
                        newO.anotherGamerCharacters = players.get((((Packets.AllowToStart) o).gamer + 1) % 2);
                        newO.gamer = ((Packets.AllowToStart) o).gamer;
                        c.sendTCP(newO);
                    } else {
                        ((Packets.AllowToStart) o).allow = false;
                        c.sendTCP(o);
                    }
                } else if (o instanceof Packets.AllowToAttack) {
                    if (((Packets.AllowToAttack) o).gamer == whoseTurnToAttack) {
                        c.sendTCP(turnInfo);
                    }
                } else if (o instanceof Packets.GameInfo) {
                    if (((Packets.GameInfo) o).gamer == whoseTurnToAttack) {
                        turnInfo = ((Packets.GameInfo) o);
                        whoseTurnToAttack = (whoseTurnToAttack + 1) % 2;
                    }
                }
            }
        });
        server.bind(tcpC);
        registerPackets();
        server.start();
    }

    private void registerPackets() {
        Kryo kryo = server.getKryo();
        kryo.register(Packets.ConnectToGame.class);
        kryo.register(Packets.AllowToStart.class);
        kryo.register(Packets.GameInfo.class);
        kryo.register(Packets.AllowToAttack.class);
        kryo.register(LinkedList.class);
    }

    public static void main(String[] args) throws IOException {
        new MPServer();
    }
}
