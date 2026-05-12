package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VerificationUIPreview extends Application {
    @Override
    public void start(Stage primaryStage) {
        // We pass null for the TransactionService since this is just a preview of the UI.
        // Clicking "Konfirmasi" will throw a NullPointerException in the console, but the UI will render!
        VerificationUI ui = new VerificationUI(null);
        
        Scene scene = new Scene(ui, 900, 600);
        primaryStage.setTitle("VerificationUI Preview");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
