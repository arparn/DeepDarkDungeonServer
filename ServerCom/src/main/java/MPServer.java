import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class MPServer {
    int udpC = 5200;
    int tcpC = 5291;
    String IPConnection = "localhost";
    Server server;
    ServerNetworkListener serverNetworkListener;

    public MPServer() throws IOException {
        server = new Server();
        serverNetworkListener = new ServerNetworkListener();
        server.addListener(serverNetworkListener);
        server.bind(tcpC);
        registerPackets();
        server.start();
    }

    private void registerPackets(){
        Kryo kryo = server.getKryo();
        kryo.register(Packets.Packet01Message.class);
    }

    public static void main(String[] args) throws IOException {
        new MPServer();
    }
}
