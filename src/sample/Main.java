package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Dimension primaryScreenBounds = Toolkit.getDefaultToolkit().getScreenSize();

        Parent root = FXMLLoader.load(getClass().getResource("FXMLMain.fxml"));

        root.getStylesheets().add("sample/Style.css");
        primaryStage.setMinHeight((61 * primaryScreenBounds.getHeight()) / 100);
        primaryStage.setMinWidth((54 * primaryScreenBounds.getWidth()) / 100);

        primaryStage.setTitle("Search Games");

        primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.show();


    }
}
