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
                first = true;
                ((Packets.ConnectToGame) o).place = 1;
                c.sendTCP(o);
            } else if (players.size() == 1){
                players.add(((Packets.ConnectToGame) o).characters);
                second = true;
                ((Packets.ConnectToGame) o).place = 2;
                c.sendTCP(o);
            } else c.close();
        }else if (o instanceof Packets.AllowToStart){
            if (players.size() == 2){
                ((Packets.AllowToStart) o).allow = "yes";
            }
            c.sendTCP(o);
        }
    }
}
