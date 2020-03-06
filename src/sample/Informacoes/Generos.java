package sample.Informacoes;

public class Generos {

    private String imageURL, name;
    private int ID;

    public Generos(String imageURL, String name, int ID) {
        this.imageURL = imageURL;
        this.name = name;
        this.ID = ID;

    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }
}
