package sample.Informacoes;

public class Plataformas {

    private String name;
    private int ID;
    private int games_count;

    public Plataformas(String name, int ID, int games_count) {
        this.name = name;
        this.ID = ID;
        this.games_count = games_count;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public int getGames_count() {
        return games_count;
    }
}
