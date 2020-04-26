import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class MPServer {
    int udpC = 5200;
    int tcpC = 5201;
    String IPConnection = "193.40.255.16";
    Server server;
    ServerNetworkListener serverNetworkListener;

    public MPServer() throws IOException {
        server = new Server();
        server.bind(tcpC, udpC);
        serverNetworkListener = new ServerNetworkListener();
        server.addListener(serverNetworkListener);
        registerPackets();
        server.start();
    }

    private void registerPackets(){
        Kryo kryo = new Kryo();
        kryo.register(Packets.Packet01Message.class);
    }

    public static void main(String[] args) throws IOException {
        new MPServer();
    }
}
