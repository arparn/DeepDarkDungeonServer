import java.util.List;

public class Packets {

    // этот запрос отсылается для подключения. мы отправляем своих игроков на сервер, за которых будем играть,
    // так же получим место, какой ты по счету
    public static class ConnectToGame{
        public List<String> characters;
        public int place;
    }

    // этот запрос посылается когда мы не знаем можем ли мы начать игру (подключился ли к серверу второй игрок).
    // так же когда он утвердительный, он нам передаст игроков соперника
    public static class AllowToStart{
        public int gamer;
        public boolean allow = false;
        public List<String> anotherGamerCharacters;
    }

    // этот запрос отсылаем когда походили, где говорим кто кого ударил какой атакой и какой анимацией
    public static class GameInfo{
        public int gamer;
        public int characterWhoBeat;
        public int damagedCharacter;
        public int animation;
        public boolean sunstrike;
    }

    // этот запрос посылается когда мы не знаем можем ли мы ходить, ждем пока не получим GameInfo
    public static class AllowToAttack{
        public int gamer;
    }

    public static void main(String[] args) {
        MPClient gamer1 = new MPClient();
    }
}
