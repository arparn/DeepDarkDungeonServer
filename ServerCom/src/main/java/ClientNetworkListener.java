
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClientNetworkListener extends Listener {


    boolean game = false;
    List<String> enemy;

    public void connected(Connection c) {
        System.out.println("[CLIENT] >> You have connected");

        //Prepare and send message to the server
        Packets.ConnectToGame firstMessage = new Packets.ConnectToGame();
        firstMessage.characters = new LinkedList<>(Arrays.asList("char1", "char2", "char2", "char3"));
        c.sendTCP(firstMessage);
    }

    public void disconnected(Connection c) {
        System.out.println("[CLIENT] >> You have disconnected");
    }

    public void received(Connection c, Object o) {

        if (o instanceof Packets.ConnectToGame) {
            Packets.AllowToStart allowToStart = new Packets.AllowToStart();
            allowToStart.gamer = ((Packets.ConnectToGame) o).place;
            c.sendTCP(allowToStart);
        } else if (o instanceof Packets.AllowToStart) {
            if (((Packets.AllowToStart) o).allow) {
                game = true;
                enemy = ((Packets.AllowToStart) o).anotherGamerCharacters;
            } else {
                c.sendTCP(o);
            }
        }
    }
    public static void main(String[] args) {
        MPClient gamer2 = new MPClient();
    }
}
