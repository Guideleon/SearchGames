package sample.Informacoes;

public class Stores extends GameInformation {

    String URL;

    public Stores(int ID, String name, String URL) {
        super(ID, name);
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
