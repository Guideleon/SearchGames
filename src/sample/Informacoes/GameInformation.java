package sample.Informacoes;

public class GameInformation {

    private int ID;
    private String name;

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public GameInformation(int ID, String name){
        this.ID = ID;
        this.name = name;
    }
}
