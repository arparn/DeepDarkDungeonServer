import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.LinkedList;
import java.util.List;

public class ServerNetworkListener extends Listener {

    private boolean first = false;
    private boolean second = false;
    private List<List<String>> players = new LinkedList<>();
    public ServerNetworkListener(){

    }

    public void connected(Connection c){
        System.out.println("Someone has connected");
    }

    public void disconnected(Connection c){
        System.out.println("Someone has disconnected");
    }

    public void received(Connection c, Object o){

        if (o instanceof Packets.ConnectToGame){
            if (players.size() == 0){
                players.add(((Packets.ConnectToGame) o).characters);
                Packets.ConnectToGame newO = new Packets.ConnectToGame();
                newO.place = 0;
                newO.characters = ((Packets.ConnectToGame) o).characters;
                c.sendTCP(newO);
            } else if (players.size() == 1){
                players.add(((Packets.ConnectToGame) o).characters);
                Packets.ConnectToGame newO = new Packets.ConnectToGame();
                newO.place = 1;
                newO.characters = ((Packets.ConnectToGame) o).characters;
                c.sendTCP(newO);
            } else c.close();
        }else if (o instanceof Packets.AllowToStart){
            if (players.size() == 2){
                Packets.AllowToStart newO = new Packets.AllowToStart();
                newO.allow = true;
                newO.anotherGamerCharacters = players.get((((Packets.AllowToStart) o).gamer + 1) % 2);
                newO.gamer = ((Packets.AllowToStart) o).gamer;
                c.sendTCP(newO);
            }
            c.sendTCP(o);
        }
    }
}
