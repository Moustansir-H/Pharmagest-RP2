package mcci.businessschool.bts.sio.slam.pharmagest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PharmagestApplicationMain extends Application {
    private final static String LOGIN_PATH = "/login/Login.fxml";

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(PharmagestApplicationMain.class.getResource(LOGIN_PATH));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}