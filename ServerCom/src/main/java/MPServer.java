import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MPServer {
    int udpC = 5200;
    int tcpC = 5291;
    String IPConnection = "localhost";
    Server server;
    private int nextPlayer = 0;
    private Map<Integer, Integer> turnMap = new HashMap<>();
    private Map<Integer, Packets.GameInfo> turnInfo = new HashMap<>();
    private List<List<String>> players = new LinkedList<>();

    public MPServer() throws IOException {
        server = new Server();
        server.addListener(new Listener() {
            public void connected(Connection c) {
                System.out.println("Someone has connected");
            }

            public void disconnected(Connection c) {
                System.out.println("Someone has disconnected");
            }

            public void received(Connection c, Object o) {

                if (o instanceof Packets.ConnectToGame) {
                    players.add(((Packets.ConnectToGame) o).characters);
                    Packets.ConnectToGame newO = new Packets.ConnectToGame();
                    newO.place = nextPlayer;
                    nextPlayer++;
                    newO.characters = ((Packets.ConnectToGame) o).characters;
                    c.sendTCP(newO);
                } else if (o instanceof Packets.AllowToStart) {
                    if (((Packets.AllowToStart) o).gamer % 2 == 1 || players.size() > (((Packets.AllowToStart) o).gamer + 1)) {
                        Packets.AllowToStart newO = new Packets.AllowToStart();
                        newO.allow = true;
                        newO.anotherGamerCharacters = players.get((((Packets.AllowToStart) o).gamer + 1) % 2);
                        newO.gamer = ((Packets.AllowToStart) o).gamer;
                        if (!turnMap.containsKey(((Packets.AllowToStart) o).gamer / 2)) {
                            turnMap.put(((Packets.AllowToStart) o).gamer / 2, ((Packets.AllowToStart) o).gamer);
                            turnInfo.put(((Packets.AllowToStart) o).gamer / 2, new Packets.GameInfo());
                        }
                        c.sendTCP(newO);
                    } else {
                        ((Packets.AllowToStart) o).allow = false;
                        c.sendTCP(o);
                    }
                } else if (o instanceof Packets.AllowToAttack) {
                    if (((Packets.AllowToAttack) o).gamer == turnMap.get(((Packets.AllowToAttack) o).gamer / 2)) {
                        c.sendTCP(turnInfo.get(((Packets.AllowToAttack) o).gamer / 2));
                    }
                } else if (o instanceof Packets.GameInfo) {
                    if (((Packets.GameInfo) o).gamer == turnMap.get(((Packets.GameInfo) o).gamer / 2)) {
                        turnInfo.replace(((Packets.GameInfo) o).gamer / 2, (Packets.GameInfo) o);
                        if (turnMap.get(((Packets.GameInfo) o).gamer / 2) % 2 == 0) {
                            turnMap.replace(((Packets.GameInfo) o).gamer / 2, turnMap.get(((Packets.GameInfo) o).gamer / 2) + 1);
                        } else {
                            turnMap.replace(((Packets.GameInfo) o).gamer / 2, turnMap.get(((Packets.GameInfo) o).gamer / 2) - 1);
                        }
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
