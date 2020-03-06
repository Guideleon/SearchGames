package sample;

import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import sample.api.ExactlyGame;
import sample.api.Request;
import sample.api.SearchedGame;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GameTab extends Tab {

    private ScrollPane telaPrincipal;
    private SearchedGame jogo;
    private Screen screen;
    private int size;

    private Service<Void> backgroundService;

    public GameTab(SearchedGame game, Screen screen, int size) {
        this.telaPrincipal = new ScrollPane();
        this.jogo = game;
        this.screen = screen;
        this.size = size;
    }


    public void initMethod() {
        telaPrincipal.setFitToWidth(true);
        telaPrincipal.setFitToHeight(true);

        telaPrincipal.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        telaPrincipal.setPrefWidth(Region.USE_COMPUTED_SIZE);
        telaPrincipal.setPrefHeight(Region.USE_COMPUTED_SIZE);


        BorderPane tela = new BorderPane();

        try {
            ExactlyGame jogoDetalhado = Request.searchExactly(this.jogo.getSlug());

            ImageView img = jogoDetalhado.imagemPronta(400);

            telaPrincipal.setStyle("-fx-background-color: #151515;");

            VBox top = new VBox();
            top.setAlignment(Pos.CENTER);
            top.setPadding(new Insets(30));
            top.setSpacing(10);

            Label gameNome = new Label(this.jogo.getName());
            gameNome.setWrapText(true);
            gameNome.setFont(Font.font("Century Gothic", FontWeight.BOLD, FontPosture.REGULAR, 34));

            top.getChildren().add(gameNome);
            top.getChildren().add(new Label("Rating: " + jogoDetalhado.getRating() + "/" + jogoDetalhado.getRating_top()));
            TilePane plataformas = new TilePane();
            plataformas.setMaxWidth(img.getFitWidth());
            plataformas.setVgap(10);
            plataformas.setHgap(20);
            plataformas.setAlignment(Pos.BASELINE_CENTER);
            for (int i = 0 ; i < jogoDetalhado.getGenres().length(); i ++){
                plataformas.getChildren().add(new Label(jogoDetalhado.getGenres().getJSONObject(i).getString("name")));

            }

            top.getChildren().add(img);
            top.getChildren().add(plataformas);

            tela.setTop(top);

            VBox right = new VBox();
            right.setPadding(new Insets(30));
            right.setSpacing(20);

            //Screen jogosIguaisPane = new Screen();

            SearchedGame[] jogosIguais = Request.requestGamesLikeThis(this.jogo.getSlug(), 5);

            TitledPane descricaoPane = new TitledPane();

            if (jogosIguais.length > 0) {
                TilePane teste = screen.getNodes(jogosIguais, size);
                ScrollPane titledScrollPane = new ScrollPane();
                titledScrollPane.setContent(teste);
                descricaoPane.setText("Jogos sugeridos");
                descricaoPane.setContent(titledScrollPane);
            }



            // CENTRO //


            VBox centro = new VBox();
            centro.setSpacing(15);
            centro.setAlignment(Pos.CENTER);

            HBox hBoxSobre = new HBox();
            centro.getChildren().add(hBoxSobre);

            hBoxSobre.setAlignment(Pos.CENTER);
            hBoxSobre.setPadding(new Insets(15, 15, 15, 15));
            hBoxSobre.setSpacing(10);
            hBoxSobre.setFillHeight(true);
            hBoxSobre.setStyle("-fx-background-color: #202020;");

            Label lblSobre = new Label("Informações");

            lblSobre.setStyle("-fx-border-color: white; -fx-border-style: segments(5, 10, 10, 10)  line-cap round; -fx-padding:  4 11 4 11;" +
                    "-fx-font-size: 16;");

            hBoxSobre.getChildren().add(lblSobre);

            VBox vBoxText = new VBox();
            vBoxText.setStyle("-fx-border-radius: 10;");
            vBoxText.setPadding(new Insets(15));

            TitledPane descricaoTitledPane = new TitledPane();
            descricaoTitledPane.setText("Descrição");


            TextFlow textFlow = new TextFlow();

            textFlow.setStyle("-fx-background-color: #202020;");

            Text descricao = new Text("Nenhuma");
            descricao.setStyle("-fx-font-size: 13;");
            descricao.setFontSmoothingType(FontSmoothingType.LCD);
            descricao.setFill(Color.WHITE);

            if (!jogoDetalhado.getDescription().isEmpty()) {
                descricao.setText(jogoDetalhado.getDescription());

                final String[] txtTraduzido = new String[0];


                backgroundService = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                Tradutor tradutor = Tradutor.getInstance();
                                System.out.println("Teste");
                                txtTraduzido[0] = tradutor.traduzir("Apple", Tradutor.Linguagem.INGLES, Tradutor.Linguagem.PORTUGUES);
                                return null;
                            }
                        };
                    }
                };

                this.setOnClosed(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        System.out.println("Saindo");
                        backgroundService.cancel();
                    }
                });

                backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        descricao.setText(txtTraduzido[0]);
                    }
                });

                //backgroundService.start();
            }

            textFlow.getChildren().add(descricao);
            descricaoTitledPane.setContent(textFlow);
            vBoxText.getChildren().add(descricaoTitledPane);

            centro.getChildren().add(vBoxText);

            VBox vBoxOndeComprar = new VBox();

            //vBoxOndeComprar.setPadding(new Insets(15));

            VBox vBoxlblOndeComprar = new VBox();
            vBoxlblOndeComprar.setStyle("-fx-background-color: #202020;");

            Label lblComprar = new Label("Onde comprar?");
            lblComprar.setStyle("-fx-font-size: 18;");
            lblComprar.setTextFill(Color.WHITE);

            Label lblCliqueEmUmLink = new Label("Clique em um Link");
            lblCliqueEmUmLink.setTextFill(Color.web("#a1a1a1"));

            vBoxlblOndeComprar.setPadding(new Insets(15));
            vBoxlblOndeComprar.setSpacing(5);
            vBoxlblOndeComprar.getChildren().addAll(lblComprar, lblCliqueEmUmLink);

            vBoxOndeComprar.getChildren().add(vBoxlblOndeComprar);

            if (jogoDetalhado.getStores().size() > 0) {
                Hyperlink[] hyperlink = new Hyperlink[jogoDetalhado.getStores().size()];

                VBox vBoxLinks = new VBox();
                vBoxLinks.setPadding(new Insets(8));
                vBoxLinks.setSpacing(8);

                for (int i = 0; i < hyperlink.length; i++) {
                    hyperlink[i] = new Hyperlink(jogoDetalhado.getStores().get(i).getName());

                    int finalI = i;
                    hyperlink[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                Desktop.getDesktop().browse(new URI(jogoDetalhado.getStores().get(finalI).getURL()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    vBoxLinks.getChildren().add(hyperlink[i]);
                }
                vBoxOndeComprar.getChildren().add(vBoxLinks);
            } else {

                vBoxOndeComprar.getChildren().add(new Label("Nenhum site disponível"));

            }

            centro.getChildren().add(vBoxOndeComprar);

            tela.setCenter(centro);

            Region freeSpace = new Region();
            freeSpace.setPrefHeight(100);

            tela.setBottom(freeSpace);

            telaPrincipal.setContent(tela);
            telaPrincipal.requestLayout();

        } catch (UnirestException e) {
            tela.setStyle("-fx-background-color: #151515;");

            tela.setCenter(new Label("Ocorreu um erro, não sera possível obter informações desse jogo"));

            telaPrincipal.setContent(tela);
            telaPrincipal.requestLayout();
        }


    }

    public ScrollPane getPane() {
        return telaPrincipal;
    }
}
