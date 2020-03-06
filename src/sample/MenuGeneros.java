package sample;

import animatefx.animation.Pulse;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import sample.Informacoes.Generos;
import sample.Informacoes.Plataformas;
import sample.api.Request;

import java.util.ArrayList;

public class MenuGeneros {


    private ScrollPane menu;

    public MenuGeneros(int layoutSize) {
        this.menu = new ScrollPane();


        this.menu.setMinViewportWidth((60 * layoutSize) / 100);
        this.menu.setFitToWidth(true);
    }

    public void loadMenu(Screen screen) {
        VBox generosVbox = new VBox();
        generosVbox.setFillWidth(true);

        generosVbox.setPadding(new Insets(5));
        generosVbox.setSpacing(10);
        generosVbox.setAlignment(Pos.BASELINE_CENTER);
        Label lblGenero = new Label("Generos");
        lblGenero.setStyle("-fx-font-size: 22;");
        generosVbox.getChildren().add(lblGenero);

        try {
            ArrayList<Generos> generos = Request.getPrincipaisGeneros();
            for (Generos g : generos) {
                Label generosName = new Label(g.getName());
                generosName.setStyle("-fx-font-size: 14;");

                generosName.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        new Pulse(generosName).play();
                    }
                });

                generosName.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        SingleSelectionModel<Tab> telaSelecionada = screen.getTelaPrincipal().getSelectionModel();
                        telaSelecionada.select(0);
                        screen.mudarPagina("https://api.rawg.io/api/games?genres=" + g.getID() + "&ordering=-added");
                        screen.setTelaPrincipalText(g.getName(), 0);

                    }
                });

                generosName.setCursor(Cursor.HAND);
                generosVbox.getChildren().add(generosName);

            }

            Label lblPlataforma = new Label("Plataformas");
            lblPlataforma.setStyle("-fx-font-size: 22;");
            generosVbox.getChildren().add(lblPlataforma);

            ArrayList<Plataformas> plataformas = Request.getPrincipaisPlataformas();
            for (Plataformas p : plataformas) {
                Label plataformaNome = new Label(p.getName());

                plataformaNome.setStyle("-fx-font-size: 14;");

                plataformaNome.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        new Pulse(plataformaNome).play();
                    }
                });

                plataformaNome.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        SingleSelectionModel<Tab> telaSelecionada = screen.getTelaPrincipal().getSelectionModel();
                        telaSelecionada.select(0);
                        screen.mudarPagina("https://api.rawg.io/api/games?platforms=" + p.getID() + "&ordering=-added");
                        screen.setTelaPrincipalText(p.getName(), 0);
                    }
                });

                plataformaNome.setCursor(Cursor.HAND);
                generosVbox.getChildren().add(plataformaNome);
            }

            this.menu.setContent(generosVbox);

        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    public ScrollPane getMenu() {
        return menu;
    }
}
