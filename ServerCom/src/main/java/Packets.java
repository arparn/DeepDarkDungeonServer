import java.util.List;

public class Packets {

    public static class ConnectToGame{
        public List<String> characters;
        public int place;
    }

    public static class AllowToStart{
        public String allow;
    }
}
