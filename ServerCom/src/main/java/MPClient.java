import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.util.LinkedList;

public class MPClient {

    int udpC = 5200;
    int tcpC = 5291;
    String IPConnection = "localhost";

    public Client client;
    private ClientNetworkListener clientNetworkListener;

    public MPClient() {
        client = new Client();
        new Thread(client).start();
        clientNetworkListener = new ClientNetworkListener();


        registerPackets();
        client.addListener(clientNetworkListener);

        try {
            client.connect(9999, IPConnection, tcpC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void sendMessage(String message) {
//        Packets.Packet01Message p1cords = new Packets.Packet01Message();
//        p1cords.message = message;
//        client.sendTCP(p1cords);
//    }

    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(Packets.ConnectToGame.class);
        kryo.register(Packets.AllowToStart.class);
        kryo.register(LinkedList.class);
    }

    public static void main(String[] args) {

        MPClient gamer1 = new MPClient();
        MPClient gamer2 = new MPClient();
    }
}
