package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClaimDonationUI extends UI {

    private static boolean jfxInitialized = false;

    // Design Tokens matching team's template
    private final String DARK_GREEN = "#0F5B21";
    private final String LIGHT_GREEN = "#D2F4D6";
    private final String LIGHT_RED = "#FADBD8";
    private final String DARK_RED = "#C0392B";
    private final String TEXT_GRAY = "#555555";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BG_COLOR = "#FAFAFA";

    public ClaimDonationUI(User user) {
        super(user);
    }

    public static void main(String[] args) {
        ClaimDonationUI ui = new ClaimDonationUI(null);
        ui.showUI();
    }

    @Override
    public void showUI() {
        if (!jfxInitialized) {
            try {
                Platform.startup(() -> {
                });
                jfxInitialized = true;
            } catch (IllegalStateException e) {
                jfxInitialized = true;
            }
        }
        Platform.runLater(this::createAndShowStage);
    }

    private void createAndShowStage() {
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Klaim & Batal Donasi");
        stage.setMaximized(true);
        showMainScene(stage);
        stage.show();
    }

    private void showMainScene(Stage stage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 80, 50, 80));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label backBtn = new Label("<- Kembali ke Dashboard");
        backBtn.setTextFill(Color.web(DARK_GREEN));
        backBtn.setStyle("-fx-cursor: hand;");

        Label title = new Label("Klaim Makanan");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));

        Label subtitle = new Label(
                "Ambil donasi makanan yang tersedia atau batalkan klaim Anda sebelumnya jika berhalangan.");
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        subtitle.setFont(Font.font("System", 16));
        subtitle.setWrapText(true);

        VBox header = new VBox(10, backBtn, title, subtitle);

        HBox columns = new HBox(50);
        columns.setAlignment(Pos.TOP_LEFT);

        // LEFT COLUMN - USE CASE 3 (CLAIM)
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        VBox claimCard = createCard("Formulir Klaim Donasi");
        
        TextField donationIdField = new TextField();
        donationIdField.setPromptText("Masukkan ID Donasi (UUID)");
        styleTextField(donationIdField);

        TextField recipientUsernameField = new TextField();
        recipientUsernameField.setPromptText("Username Anda (Penerima)");
        styleTextField(recipientUsernameField);

        Button claimBtn = new Button("Klaim Donasi Sekarang");
        claimBtn.setStyle("-fx-background-color: " + DARK_GREEN
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        claimBtn.setMaxWidth(Double.MAX_VALUE);
        claimBtn.setPrefHeight(50);
        
        claimBtn.setOnAction(e -> {
            String donationId = donationIdField.getText();
            String username = recipientUsernameField.getText();
            if (donationId.isEmpty() || username.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Harap isi ID Donasi dan Username.");
                return;
            }
            processClaim(donationId, username);
        });

        claimCard.getChildren().addAll(
            new Label("ID Donasi"), donationIdField,
            new Label("Username Penerima"), recipientUsernameField,
            claimBtn
        );

        leftCol.getChildren().add(claimCard);

        // RIGHT COLUMN - USE CASE 4 (CANCEL)
        VBox rightCol = new VBox(30);
        rightCol.setPrefWidth(450);

        VBox cancelCard = new VBox(15);
        cancelCard.setPadding(new Insets(25));
        cancelCard.setStyle("-fx-border-color: #F1948A; -fx-border-radius: 8px; -fx-background-color: " + LIGHT_RED + "; -fx-background-radius: 8px;");
        
        Label cancelTitle = new Label("Batalkan Klaim");
        cancelTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cancelTitle.setTextFill(Color.web(DARK_RED));
        
        Label cancelSubtitle = new Label("Hanya dapat dibatalkan maksimal 1 jam sebelum batas waktu kedaluwarsa.");
        cancelSubtitle.setFont(Font.font("System", 13));
        cancelSubtitle.setTextFill(Color.web(DARK_RED));
        cancelSubtitle.setWrapText(true);

        TextField transactionCodeField = new TextField();
        transactionCodeField.setPromptText("Kode Transaksi (6-digit)");
        styleTextField(transactionCodeField);

        Button cancelBtn = new Button("Batalkan Klaim Saya");
        cancelBtn.setStyle("-fx-background-color: white; -fx-border-color: " + DARK_RED + "; -fx-text-fill: " + DARK_RED
                + "; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 6px; -fx-border-radius: 6px; -fx-cursor: hand;");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setPrefHeight(45);
        
        cancelBtn.setOnAction(e -> {
            String codeStr = transactionCodeField.getText();
            if (codeStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Harap isi Kode Transaksi.");
                return;
            }
            try {
                int code = Integer.parseInt(codeStr);
                processCancel(code);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Kesalahan Format", "Kode Transaksi harus berupa angka.");
            }
        });

        cancelCard.getChildren().addAll(cancelTitle, cancelSubtitle, new Label("Kode Transaksi"), transactionCodeField, cancelBtn);

        rightCol.getChildren().addAll(cancelCard);

        columns.getChildren().addAll(leftCol, rightCol);
        root.getChildren().addAll(header, columns);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(scroll);
            stage.setScene(scene);
            stage.setMaximized(true);
        } else {
            scene.setRoot(scroll);
        }

        // Animations matching template
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(600), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(600), root);
        tt.setFromY(30);
        tt.setToY(0);
        tt.play();
    }

    private void processClaim(String donationId, String recipientUsername) {
        try {
            String token = SessionManager.getInstance().getToken();
            HttpClient client = HttpClient.newHttpClient();
            String jsonPayload = String.format("{\"donationId\":\"%s\", \"recipientUsername\":\"%s\"}", donationId, recipientUsername);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/claims"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert(Alert.AlertType.INFORMATION, "Klaim Berhasil", response.body());
            } else {
                showAlert(Alert.AlertType.ERROR, "Klaim Gagal", response.body());
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Koneksi Gagal", "Pastikan Server Spring Boot berjalan di port 8080.\n" + ex.getMessage());
        }
    }

    private void processCancel(int transactionCode) {
        try {
            String token = SessionManager.getInstance().getToken();
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/claims/cancel/" + transactionCode))
                    .header("Authorization", "Bearer " + token)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert(Alert.AlertType.INFORMATION, "Pembatalan Berhasil", response.body());
            } else {
                showAlert(Alert.AlertType.ERROR, "Pembatalan Gagal", response.body());
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Koneksi Gagal", "Pastikan Server Spring Boot berjalan di port 8080.\n" + ex.getMessage());
        }
    }

    private VBox createCard(String title) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-background-color: white; -fx-background-radius: 8px;");
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.web(DARK_GREEN));
        card.getChildren().add(lblTitle);
        return card;
    }

    private void styleTextField(TextField tf) {
        tf.setStyle("-fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 6px; -fx-padding: 12px; -fx-font-size: 14px;");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
