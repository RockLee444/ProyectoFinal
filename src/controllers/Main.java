package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Main.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane anchorPane;
        anchorPane = FXMLLoader.load(Main.class.getResource("/views/AutomataView.fxml"));
        Scene scene = new Scene(anchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("VAC Compiler");
        primaryStage.show();
    }
}
