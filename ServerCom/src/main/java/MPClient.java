import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class MPClient {

    int udpC = 5200;
    int tcpC = 5201;
    String IPConnection = "193.40.255.16";

    public Client client;
    private ClientNetworkListener clientNetworkListener;

    public MPClient() {
        client = new Client();
        clientNetworkListener = new ClientNetworkListener();

        clientNetworkListener.init(client);
        registerPackets();
        client.addListener(clientNetworkListener);
        client.start();

        try {
            client.connect(10000, IPConnection, tcpC, udpC);
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
