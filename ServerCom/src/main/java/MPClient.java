import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class MPClient {

    int udpC = 5200;
    int tcpC = 51390;
    String IPConnection = "localhost";

    public Client client;
    private ClientNetworkListener clientNetworkListener;

    public MPClient() {
        client = new Client();
        client.start();
        clientNetworkListener = new ClientNetworkListener();

        clientNetworkListener.init(client);
        registerPackets();
        client.addListener(clientNetworkListener);

        try {
            client.connect(10000, IPConnection, tcpC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(Packets.Packet01Message.class);
    }

    public static void main(String[] args) {
        new MPClient();
    }
}
