package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class VerificationSuccessUI extends VBox {

    public VerificationSuccessUI() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(50));
        this.setSpacing(20);
        this.setStyle("-fx-background-color: #F9FAFB;");
        initializeUI();
    }

    private void initializeUI() {
        // Green Checkmark Icon inside a Circle
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        Circle circle = new Circle(40, Color.web("#DCFCE7"));
        circle.setStroke(Color.web("#22C55E"));
        circle.setStrokeWidth(3);
        
        Label checkmark = new Label("✔");
        checkmark.setTextFill(Color.web("#16A34A"));
        checkmark.setFont(Font.font("System", FontWeight.BOLD, 40));
        
        javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane(circle, checkmark);
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
        btnHome.setOnAction(e -> {
            Stage stage = (Stage) this.getScene().getWindow();
            stage.close(); // For now, just close the window
        });

        this.getChildren().addAll(iconContainer, titleLabel, descText, btnHome);
    }
}
