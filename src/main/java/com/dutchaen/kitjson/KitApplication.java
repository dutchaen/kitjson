package com.dutchaen.kitjson;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KitApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(KitApplication.class.getResource("kit-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 589, 427);
        stage.setTitle("kitjson");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}