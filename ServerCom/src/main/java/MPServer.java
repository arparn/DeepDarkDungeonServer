import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MPServer {

    int udpC = 5200;
    int tcpC = 5201;
    Server server;
    private int gameNr = 0;
    private int nextPlayer = 0;
    private Map<Integer, Integer> turnMap = new HashMap<>();
    private Map<Integer, Packets.GameInfo> turnInfo = new HashMap<>();
    private List<List<String>> players = new LinkedList<>();
    private List<Connection> connections = new LinkedList<>();

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
                    if (nextPlayer == 2) {
                        nextPlayer = 0;
                        gameNr++;
                    }
                    if (nextPlayer == 1) {
                        turnMap.put(gameNr, ThreadLocalRandom.current().nextInt(0, 2));
                        turnInfo.put(gameNr, new Packets.GameInfo());
                    }
                    newO.place.add(gameNr);
                    newO.place.add(nextPlayer);
                    nextPlayer++;
                    newO.characters = ((Packets.ConnectToGame) o).characters;
                    c.sendTCP(newO);
                } else if (o instanceof Packets.AllowToStart) {
                    if ((((Packets.AllowToStart) o).gamer.get(0) * 2 + 2) <= players.size()) {
                        Packets.AllowToStart newO = new Packets.AllowToStart();
                        newO.allow = true;
                        newO.anotherGamerCharacters = players.get(((Packets.AllowToStart) o).gamer.get(0) * 2 + ((1 + ((Packets.AllowToStart) o).gamer.get(1)) % 2));
                        newO.gamer = ((Packets.AllowToStart) o).gamer;
                        c.sendTCP(newO);
                    } else {
                        Packets.AllowToStart newO = new Packets.AllowToStart();
                        newO.gamer = ((Packets.AllowToStart) o).gamer;
                        newO.anotherGamerCharacters = ((Packets.AllowToStart) o).anotherGamerCharacters;
                        newO.allow = false;
                        c.sendTCP(newO);
                    }
                } else if (o instanceof Packets.AllowToAttack) {
                    if (((Packets.AllowToAttack) o).gamer.get(1).equals(turnMap.get(((Packets.AllowToAttack) o).gamer.get(0)))) {
                        c.sendTCP(turnInfo.get(((Packets.AllowToAttack) o).gamer.get(0)));
                    }
                } else if (o instanceof Packets.GameInfo) {
                    if (((Packets.GameInfo) o).gamer.get(1).equals(turnMap.get(((Packets.GameInfo) o).gamer.get(0)))) {
                        turnInfo.replace(((Packets.GameInfo) o).gamer.get(0), (Packets.GameInfo) o);
                        if (turnMap.get(((Packets.GameInfo) o).gamer.get(0)) == 0) {
                            turnMap.replace(((Packets.GameInfo) o).gamer.get(0), 1);
                        } else {
                            turnMap.replace(((Packets.GameInfo) o).gamer.get(0), 0);
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
        kryo.register(LinkedList.class);
        kryo.register(Packets.ConnectToGame.class);
        kryo.register(Packets.AllowToStart.class);
        kryo.register(Packets.GameInfo.class);
        kryo.register(Packets.AllowToAttack.class);
    }
}
