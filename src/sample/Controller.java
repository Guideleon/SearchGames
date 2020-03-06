package sample;

import animatefx.animation.FadeIn;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import sample.api.Request;
import sample.api.SearchedGame;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    static Screen tela;
    @FXML
    StackPane stackPane;
    @FXML
    BorderPane fundo;
    @FXML
    TextField txtPesquisa;
    @FXML
    FontAwesomeIconView voltarPagina;
    @FXML
    FontAwesomeIconView passarPagina;
    private Service<Void> backgroundService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voltarPagina.setVisible(false);
        passarPagina.setVisible(false);

        tela = new Screen(350);

        MenuGeneros menuGeneros = new MenuGeneros(tela.layoutSize);

        backgroundService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        tela.initScreen();
                        menuGeneros.loadMenu(tela);
                        return null;
                    }
                };
            }
        };

        backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                stackPane.getChildren().add(tela.getTelaPrincipal());

                fundo.setLeft(menuGeneros.getMenu());
                new FadeIn(menuGeneros.getMenu()).play();

                voltarPagina.setVisible(true);
                passarPagina.setVisible(true);
                txtPesquisa.setDisable(false);

                tela.completado = false;
            }
        });

        backgroundService.start();

        stackPane.getChildren().add(new ProgressIndicator());
    }


    @FXML
    public void passarPagina() {
        if (!tela.nextURL.isEmpty()) {
            SingleSelectionModel<Tab> telaSelecionada = tela.getTelaPrincipal().getSelectionModel();
            telaSelecionada.select(0);
            tela.mudarPagina(tela.nextURL);
        }
    }

    @FXML
    public void voltarPagina() {
        if (!tela.previousURL.isEmpty()) {
            SingleSelectionModel<Tab> telaSelecionada = tela.getTelaPrincipal().getSelectionModel();
            telaSelecionada.select(0);
            tela.mudarPagina(tela.previousURL);
        }
    }

    @FXML
    public void onEnter(ActionEvent ae) {
        String txt = txtPesquisa.getText().replace(" ", "%20");
        SingleSelectionModel<Tab> telaSelecionada = tela.getTelaPrincipal().getSelectionModel();
        telaSelecionada.select(0);
        tela.mudarPagina(Request.APISearch + "20" + "&search=" + txt + "&page_size=20");
    }

}
