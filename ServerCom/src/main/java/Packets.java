import java.util.List;

public class Packets {

    public static class ConnectToGame{
        public List<String> characters;
        public int place;
    }

    public static class AllowToStart{
        public int gamer;
        public boolean allow = false;
        public List<String> anotherGamerCharacters;
    }

    public static class GameInfo{
        public int gamer;
        public int characterWhoBeat;
        public int damagedCharacter;
        public int animation;
        public boolean sunstrike;
    }
}
