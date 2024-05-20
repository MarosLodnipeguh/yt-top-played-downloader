package app;

import app.DataStructures.SystemOutPassThrough;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();

        MainController controller = fxmlLoader.getController();

        // Redirect System.out to statusText field in app.MainController
        PrintStream printStream = new PrintStream(new SystemOutPassThrough(controller.statusText));
//        System.setOut(printStream);

        Scene scene = new Scene(root, 1200, 812);
        primaryStage.setScene(scene);
        primaryStage.setTitle("YouTube & Music History Downloader");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
