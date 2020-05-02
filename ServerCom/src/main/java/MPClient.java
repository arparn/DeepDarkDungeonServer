import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MPClient {

    int udpC = 5200;
    int tcpC = 5291;
    String IPConnection = "localhost";

    int myPlace;
    boolean game = false;
    List<String> enemy;

    public Client client;

    public MPClient() {
        client = new Client();
        new Thread(client).start();

        registerPackets();
        client.addListener(new Listener() {
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
                    myPlace = ((Packets.ConnectToGame) o).place;
                    allowToStart.gamer = ((Packets.ConnectToGame) o).place;
                    c.sendTCP(allowToStart);
                } else if (o instanceof Packets.AllowToStart) {
                    if (((Packets.AllowToStart) o).allow) {
                        game = true;
                        // игра начало
                        enemy = ((Packets.AllowToStart) o).anotherGamerCharacters;
                        Packets.AllowToAttack allowToSend = new Packets.AllowToAttack();
                        allowToSend.gamer = myPlace;
                        System.out.println("started");
                        c.sendTCP(allowToSend);
                    } else {
                        c.sendTCP(o);
                    }
                } else if (o instanceof Packets.GameInfo) {
                    System.out.println("I'm the first!!!!");
                    // тут мы получаем информацию об ударе от соперника и ее воспроизвоодим у себя в игре.
                    // ПРИ ПЕРВОМ ПОЛУЧЕНИИ Packets.GameInfo У НЕГО ПАРАМЕТРЫ НЕ ИНИЦИАЛЕЗИРОВАНЫ.
                    // после мы делаем свой удар и шлем Packets.GameInfo на сервер, в ответ мы ничего не получаем.
                    // далее начинаем посылать Packets.AllowToAttack пока не получим в ответ Packets.GameInfo чтобы походить
                    // самим
                }
            }
        });

        try {
            client.connect(9999, IPConnection, tcpC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void askIfCanAttack() {
//        Packets.Packet01Message p1cords = new Packets.Packet01Message();
//        p1cords.message = message;
//        client.sendTCP(p1cords);
//    }

    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(Packets.ConnectToGame.class);
        kryo.register(Packets.AllowToStart.class);
        kryo.register(Packets.GameInfo.class);
        kryo.register(Packets.AllowToAttack.class);
        kryo.register(LinkedList.class);
    }

    public static void main(String[] args) {
        MPClient gamer1 = new MPClient();
    }
}
