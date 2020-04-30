
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientNetworkListener extends Listener {


    public void connected(Connection c){
        System.out.println("[CLIENT] >> You have connected");

        //Prepare and send message to the server
        Packets.Packet01Message firstMessage = new Packets.Packet01Message();
        firstMessage.message = "Hello, Server. How are you?";
        c.sendTCP(firstMessage);
    }

    public void disconnected(Connection c){
        System.out.println("[CLIENT] >> You have disconnected");
    }

    public void received(Connection c, Object o){

        if (o instanceof Packets.Packet01Message){
            Packets.Packet01Message p = (Packets.Packet01Message) o;
            System.out.println("[SERVER] >> " + p.message);
        }
    }
}
