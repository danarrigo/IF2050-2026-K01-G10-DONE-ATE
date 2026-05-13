package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class VerificationSuccessUI extends UI {

    public VerificationSuccessUI(User user) {
        super(user);
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        return createContent(stage);
    }

    @Override
    public void showUI() {
        initJFX();
        javafx.application.Platform.runLater(() -> start(new Stage()));
    }

    public void start(Stage stage) {
        stage.setTitle("DONE-ATE - Transaksi Selesai");
        Scene scene = new Scene(createContent(stage), 1920, 1080);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public Parent createContent(Stage stage) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setSpacing(20);
        root.setStyle("-fx-background-color: #F9FAFB;");

        // Green Checkmark Icon inside a Circle
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        Circle circle = new Circle(40, Color.web("#DCFCE7"));
        circle.setStroke(Color.web("#22C55E"));
        circle.setStrokeWidth(3);
        
        Label checkmark = new Label("✔");
        checkmark.setTextFill(Color.web("#16A34A"));
        checkmark.setFont(Font.font("System", FontWeight.BOLD, 40));
        
        StackPane stack = new StackPane(circle, checkmark);
        iconContainer.getChildren().add(stack);

        // Success Title
        Label titleLabel = new Label("Transaksi Selesai!");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#111827"));

        // Success Message
        Text descText = new Text("Serah terima donasi berhasil diverifikasi. Terima kasih telah berpartisipasi dalam DONE-ATE!");
        descText.setFill(Color.web("#4B5563"));
        descText.setFont(Font.font("System", 16));
        descText.setWrappingWidth(400);
        descText.setTextAlignment(TextAlignment.CENTER);

        // Back to Home Button
        Button btnHome = new Button("Kembali ke Beranda");
        btnHome.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 30 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        btnHome.setOnAction(e -> Navigator.navigate(stage, new RecipientCatalogUI(getUser())));

        root.getChildren().addAll(iconContainer, titleLabel, descText, btnHome);
        return root;
    }

    public static void main(String[] args) {
        new VerificationSuccessUI(null).showUI();
    }
}
