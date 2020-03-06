package sample;

import animatefx.animation.FadeIn;
import animatefx.animation.Pulse;
import animatefx.animation.Wobble;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sample.api.Request;
import sample.api.SearchedGame;

import javax.swing.*;

public class Screen {

    public int layoutSize;
    public String nextURL;
    public String previousURL;
    public TabPane telaPrincipal;
    boolean completado;
    private String page = "1";

    public Screen(int layoutSize) {
        this.completado = false;
        this.layoutSize = layoutSize;
        telaPrincipal = new TabPane();

        this.nextURL = null;
        this.previousURL = null;
    }

    public TabPane getTelaPrincipal() {
        return telaPrincipal;
    }

    /*
     * Inicia os primeiros jogos a serem mostrados,
     * Este método é muito importante!
     *
     */
    public void initScreen() {

        TabPane layoutPrincipal = new TabPane();
        TilePane tilePane = new TilePane();

        try {
            SearchedGame[] jogos = Request.topTrendingGames();
            tilePane = getNodes(jogos, layoutSize);
        } catch (UnirestException e) {
            JOptionPane.showMessageDialog(null, "Algum erro ocorreu!", "Search Games", JOptionPane.ERROR_MESSAGE);
            tilePane.getChildren().add(new Label("Sem conexão com a internet"));
            e.printStackTrace();
        }

        ScrollPane scrollPane = new ScrollPane();
        Tab main = new Tab("Início #1");
        main.setClosable(false);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(tilePane);

        main.setContent(scrollPane);
        layoutPrincipal.getTabs().add(main);

        new FadeIn(scrollPane).play();

        this.telaPrincipal = layoutPrincipal;
        this.completado = true;
    }

    private ScrollPane reloadScreen(SearchedGame[] jogos) {
        TilePane tilePane = getNodes(jogos, layoutSize);
        ScrollPane scrollPane = new ScrollPane();

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(tilePane);

        return scrollPane;
    }

    public TilePane getNodes(SearchedGame[] jogos, int size) {
        TilePane tilePane = new TilePane();

        tilePane.setPadding(new Insets(5));
        tilePane.setVgap(20);
        tilePane.setHgap((15 * size) / 100);
        tilePane.setPrefColumns(4);
        tilePane.setStyle("-fx-background-color: #151515;");
        tilePane.prefTileWidthProperty().set(size);

        // Iniciando os nodes com a variável length.. Ou seja, nodes = 20;
        // 20 jogos irão aparecer.
        VBox[] nodes = new VBox[jogos.length];

        for (int i = 0; i < jogos.length; i++) {
            nodes[i] = new VBox();
            nodes[i].setSpacing(10);
            nodes[i].setStyle("-fx-background-color: #202020; -fx-background-radius: 4; -fx-border-color: black; -fx-border-radius: 4;");
            nodes[i].setAlignment(Pos.TOP_CENTER);

            ImageView img = getImage(jogos[i], size);
            img.setCursor(Cursor.HAND);
            int currentLoop = i;

            // Click event
            img.setOnMouseClicked(event -> {
                if (telaPrincipal.getTabs().size() < 10 && event.getButton().equals(MouseButton.PRIMARY)) {
                    SearchedGame target = jogos[currentLoop];

                    Tab windowDoJogo = new Tab(target.getName());
                    Service<Void> backgroundService;

                    GameTab newTab = new GameTab(jogos[currentLoop], Controller.tela, 200);

                    backgroundService = new Service<Void>() {
                        @Override
                        protected Task<Void> createTask() {
                            return new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    newTab.initMethod();
                                    return null;
                                }
                            };
                        }
                    };
                    backgroundService.setOnSucceeded(event1 -> {
                        windowDoJogo.setContent(newTab.getPane());
                        new FadeIn(newTab.getPane()).play();
                    });
                    telaPrincipal.getTabs().add(windowDoJogo);
                    new FadeIn(telaPrincipal).play();

                    backgroundService.start();
                }
            });

            // Mouse entered event
            Pulse pulseEffect = new Pulse(img);
            pulseEffect.setDelay(Duration.millis(25));
            pulseEffect.setResetOnFinished(false);

            img.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    pulseEffect.play();
                }
            });

            img.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                }
            });

            nodes[i].getChildren().add(img);

            Label gameName = new Label(jogos[i].getName());

            if (gameName.getText().length() > 25) {
                gameName.setStyle("-fx-font-size: 14; -fx-text-fill: white");
            } else {
                gameName.setStyle("-fx-font-size: 18; -fx-text-fill: white");
            }

            nodes[i].getChildren().add(gameName);


            //    !! LEIA A LINHA 221 ANTES DE MEXER AQUI !!
            Tradutor tradutor = Tradutor.getInstance();

            String txt = "";
            if (jogos[i].getGenresName().size() > 0) {
                for (int j = 0; j < jogos[i].getGenresName().size(); j++) {
                    if (j == jogos[i].getGenresName().size() - 1) {
                        txt += jogos[i].getGenresName().get(j);
                    } else {
                        txt += jogos[i].getGenresName().get(j) + ", ";
                    }
                }
                /*
                    Parte do tradutor, remova os "//" caso queira traduzir as informações.
                    ATENÇÃO!
                    provavelmente ira aumentar o tempo de carregamento do aplicativo.
                    DRASTICAMENTE.
                */
                try {
                    Label generos = new Label(txt);
                    final String[] txtTraduzido = new String[1];
                    String finalTxt = txt;

                    boolean traduzir = false;

                    if (traduzir) {
                        Service<Void> backgroundService = new Service<Void>() {
                            @Override
                            protected Task<Void> createTask() {
                                return new Task<Void>() {
                                    @Override
                                    protected Void call() throws Exception {
                                        Tradutor tradutor = Tradutor.getInstance();
                                        txtTraduzido[0] = tradutor.traduzir(finalTxt, Tradutor.Linguagem.INGLES, Tradutor.Linguagem.PORTUGUES);
                                        txtTraduzido[0] = txtTraduzido[0].replace("LGF", "RPG");
                                        return null;
                                    }
                                };
                            }
                        };
                        backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                generos.setText(txtTraduzido[0]);
                            }
                        });
                        backgroundService.start();
                    }

                    nodes[i].getChildren().add(generos);

                } catch (Exception e) {
                    nodes[i].getChildren().add(new Label(jogos[i].getGenresName().get(0)));
                }
            }
            nodes[i].getChildren().add(new Label(jogos[i].getBrazilianReleaseDate()));

            if (jogos[i].getMetacritic() > 0) {
                Label lblMetacritic = new Label(String.valueOf(jogos[i].getMetacritic()));
                lblMetacritic.setStyle("-fx-font-size: 16; -fx-border-radius: 2; -fx-padding: 1 5 1 5;");
                if (jogos[i].getMetacritic() >= 70) {
                    lblMetacritic.setStyle(lblMetacritic.getStyle() + "-fx-border-color: green;");
                    lblMetacritic.setTextFill(Color.GREEN);
                } else if (jogos[i].getMetacritic() < 70 && jogos[i].getMetacritic() >= 50) {
                    lblMetacritic.setStyle(lblMetacritic.getStyle() + "-fx-border-color: yellow;");
                    lblMetacritic.setTextFill(Color.YELLOW);
                } else {
                    lblMetacritic.setStyle(lblMetacritic.getStyle() + "-fx-border-color: red;");
                    lblMetacritic.setTextFill(Color.RED);
                }
                nodes[i].getChildren().add(lblMetacritic);
            }

            // Like e Deslike icone
            HBox thumbsRegion = new HBox();
            Region filler = new Region();

            thumbsRegion.setHgrow(filler, Priority.ALWAYS);

            FontAwesomeIconView thumbUp = new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_UP);
            thumbUp.setGlyphSize(24);
            thumbUp.setFill(Color.WHITE);

            FontAwesomeIconView thumbDown = new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_DOWN);
            thumbDown.setGlyphSize(24);
            thumbDown.setFill(Color.WHITE);

            Wobble effectUp = new Wobble(thumbUp);
            effectUp.setDelay(Duration.millis(70));
            effectUp.setResetOnFinished(false);

            thumbUp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                boolean thumbUpClicked = false;

                @Override
                public void handle(MouseEvent event) {
                    if (!event.getButton().equals(MouseButton.PRIMARY)) return;
                    if (!thumbUpClicked) {
                        thumbUp.setFill(Color.web("#569bff"));
                        thumbDown.setFill(Color.WHITE);
                        thumbUpClicked = true;
                    } else {
                        thumbUp.setFill(Color.WHITE);
                        thumbUpClicked = false;
                    }
                }
            });

            thumbUp.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    effectUp.play();
                }
            });

            Wobble effectDown = new Wobble(thumbDown);
            effectDown.setDelay(Duration.millis(70));
            effectDown.setResetOnFinished(false);

            thumbDown.setOnMouseClicked(new EventHandler<MouseEvent>() {
                boolean thumbDownClicked = false;

                @Override
                public void handle(MouseEvent event) {
                    if (!event.getButton().equals(MouseButton.PRIMARY)) return;
                    if (!thumbDownClicked) {
                        thumbDown.setFill(Color.web("#ff5656"));
                        thumbUp.setFill(Color.WHITE);
                        thumbDownClicked = true;
                    } else {
                        thumbDown.setFill(Color.WHITE);
                        thumbDownClicked = false;
                    }
                }
            });

            thumbDown.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    effectDown.play();
                }
            });
            thumbsRegion.setPadding(new Insets(20));
            thumbsRegion.getChildren().addAll(thumbUp, filler, thumbDown);

            nodes[i].getChildren().add(thumbsRegion);
            nodes[i].setMaxHeight(10);

            tilePane.getChildren().add(nodes[i]);
        }

        if (jogos[0].nextURL.contains("suggested?")) {
        } else {
            this.nextURL = jogos[0].nextURL;
            this.previousURL = jogos[0].previousURL;

            if (this.nextURL.isEmpty()) {
                return tilePane;
            }

            int pos = this.nextURL.indexOf("page=");
            String novaPagina = "";
            for (int i = pos; i < this.nextURL.length(); i++) {
                if (this.nextURL.charAt(i) == '&') {
                    novaPagina = this.nextURL.substring(pos + 5, i);
                    break;
                } else if (i == this.nextURL.length() - 1) {
                    novaPagina = this.nextURL.substring(pos + 5, i + 1);
                    break;
                }
            }
            int pagina = Integer.parseInt(novaPagina) - 1;
            this.page = String.valueOf(pagina);
        }

        return tilePane;
    }


    public void mudarPagina(String URL) {
        Service<Void> backgroundService;

        final ScrollPane[] newPane = new ScrollPane[1];

        backgroundService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        newPane[0] = new ScrollPane();
                        try {
                            SearchedGame[] jogos = Request.requestPagina(URL);
                            newPane[0] = reloadScreen(jogos);

                        } catch (UnirestException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        };

        backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                telaPrincipal.getTabs().get(0).setContent(newPane[0]);
                passTelaPrincipalPage(0);
                new FadeIn(newPane[0]).play();
            }
        });

        backgroundService.start();
        telaPrincipal.getTabs().get(0).setContent(null);
    }

    public void setTelaPrincipalText(String txt, int index) {
        telaPrincipal.getTabs().get(index).setText(txt + " #" + page);
    }

    public void passTelaPrincipalPage(int index) {
        int pos = this.getTelaPrincipalText(index).indexOf("#");
        String txt = "";
        if (pos > 0) {
            txt = this.getTelaPrincipalText(index).substring(0, pos - 1);
        } else {
            txt = this.getTelaPrincipalText(index);
        }
        telaPrincipal.getTabs().get(index).setText(txt + " #" + page);
    }


    public String getTelaPrincipalText(int index) {
        return this.telaPrincipal.getTabs().get(index).getText();
    }


    public ImageView getImage(SearchedGame game, int imgSize) {
        ImageView imageView = new ImageView();

        imageView.setFitWidth(imgSize);
        imageView.setFitHeight(imgSize);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);

        Rectangle clip = new Rectangle(
                imageView.getFitWidth(), imageView.getFitHeight()
        );

        clip.setArcWidth(20);
        clip.setArcHeight(20);

        imageView.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            imageView.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.8)));
        }

        imageView.setClip(null);

        if (game.getImageURL().isEmpty()) {
            Image img = new Image("sample/imagens/noimage.jpg");
            imageView.setImage(img);
            return imageView;
        }

        Image image = new Image(game.getImageURL(), true);

        imageView.setImage(image);

        return imageView;
    }
}