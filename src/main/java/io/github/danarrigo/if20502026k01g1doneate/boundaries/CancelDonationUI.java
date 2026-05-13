package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * CancelDonationUI — Halaman pembatalan klaim donasi (UC4).
 * Menampilkan informasi donasi yang sudah diklaim dan formulir input kode transaksi
 * untuk membatalkan klaim. Dibuka dari tombol "Batal Klaim" di DonationDetailUI.
 */
public class CancelDonationUI extends UI {

    private Donation donation;

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String LIGHT_RED    = "#FADBD8";
    private static final String DARK_RED     = "#C0392B";
    private static final String TEXT_GRAY    = "#757575";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String BG_COLOR     = "#F5F5F5";
    private static final String BASE_URL     = "http://localhost:8080";

    public CancelDonationUI(User user, Donation donation) {
        super(user);
        this.donation = donation;
    }

    // ─── Standalone entry ──────────────────────────────────────────────────────

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(this::createAndShowStage);
    }

    private void createAndShowStage() {
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Batalkan Klaim");
        stage.setMaximized(true);
        Scene scene = new Scene(getSceneContent(stage));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    // ─── Navigator-compatible entry ────────────────────────────────────────────

    @Override
    public Parent getSceneContent(Stage stage) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        root.getChildren().add(buildTopBar(stage));

        VBox content = new VBox(24);
        content.setPadding(new Insets(40, 60, 40, 60));
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(buildDonationInfoCard(), buildCancelCard(stage));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        root.getChildren().add(scroll);

        playAnimation(content);
        return root;
    }

    // ─── Top Bar ───────────────────────────────────────────────────────────────

    private HBox buildTopBar(Stage stage) {
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-width: 0 0 1 0;");

        Label backArrow = new Label("←");
        backArrow.setFont(Font.font("System", FontWeight.BOLD, 20));
        backArrow.setTextFill(Color.web(DARK_GREEN));
        backArrow.setStyle("-fx-cursor: hand;");
        backArrow.setOnMouseClicked(e -> stage.close());

        Label logoLabel = new Label("DONE-ATE");
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        logoLabel.setTextFill(Color.web(DARK_GREEN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pageTitle = new Label("Batalkan Klaim");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        pageTitle.setTextFill(Color.web(DARK_RED));

        topBar.getChildren().addAll(backArrow, logoLabel, spacer, pageTitle);
        return topBar;
    }

    // ─── Donation Info Card ────────────────────────────────────────────────────

    private VBox buildDonationInfoCard() {
        VBox card = new VBox(12);
        card.setMaxWidth(600);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        Label cardTitle = new Label("Donasi yang Diklaim");
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cardTitle.setTextFill(Color.web(DARK_GREEN));

        String dishName = (donation != null && donation.getDish() != null)
                ? donation.getDish().getName() : "Makanan";
        Label nameLabel = new Label("🍽  " + dishName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);

        String status = (donation != null && donation.getStatus() != null)
                ? donation.getStatus() : "Diklaim";
        Label statusLabel = new Label("Status: " + status);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold;");

        card.getChildren().addAll(cardTitle, nameLabel, statusLabel);
        return card;
    }

    // ─── Cancel Form Card ──────────────────────────────────────────────────────

    private VBox buildCancelCard(Stage stage) {
        VBox card = new VBox(16);
        card.setMaxWidth(600);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: " + LIGHT_RED + ";" +
            "-fx-border-color: #F1948A;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );

        Label cancelTitle = new Label("⚠ Batalkan Klaim");
        cancelTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cancelTitle.setTextFill(Color.web(DARK_RED));

        Label cancelDesc = new Label(
            "Pembatalan hanya dapat dilakukan maksimal 1 jam sebelum batas waktu kedaluwarsa.\n" +
            "Masukkan kode transaksi 6-digit yang Anda terima saat klaim berhasil."
        );
        cancelDesc.setWrapText(true);
        cancelDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + DARK_RED + ";");

        Label codeLabel = new Label("Kode Transaksi");
        codeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        codeLabel.setTextFill(Color.web("#333"));

        TextField codeField = new TextField();
        codeField.setPromptText("Masukkan 6-digit kode transaksi");
        codeField.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 14px;" +
            "-fx-font-size: 16px;" +
            "-fx-alignment: center;"
        );

        Button cancelBtn = new Button("Batalkan Klaim Saya");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setPrefHeight(48);
        cancelBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + DARK_RED + ";" +
            "-fx-text-fill: " + DARK_RED + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15px;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> handleCancel(stage, codeField, cancelBtn));

        Button backBtn = new Button("← Kembali");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(40);
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_GRAY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> stage.close());

        card.getChildren().addAll(cancelTitle, cancelDesc, codeLabel, codeField, cancelBtn, backBtn);
        return card;
    }

    // ─── Cancel API Call ───────────────────────────────────────────────────────

    private void handleCancel(Stage stage, TextField codeField, Button cancelBtn) {
        String codeStr = codeField.getText().trim();
        if (codeStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Harap isi kode transaksi.");
            return;
        }

        int code;
        try {
            code = Integer.parseInt(codeStr);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Kode transaksi harus berupa angka.");
            return;
        }

        cancelBtn.setDisable(true);
        cancelBtn.setText("Memproses...");

        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/claims/cancel/" + code))
                    .DELETE();

                if (token != null) builder.header("Authorization", "Bearer " + token);

                HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    cancelBtn.setDisable(false);
                    cancelBtn.setText("Batalkan Klaim Saya");
                    if (response.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Pembatalan Berhasil",
                            "Klaim berhasil dibatalkan.\n" + response.body());
                        stage.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Pembatalan Gagal", response.body());
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    cancelBtn.setDisable(false);
                    cancelBtn.setText("Batalkan Klaim Saya");
                    showAlert(Alert.AlertType.ERROR, "Koneksi Gagal",
                        "Pastikan Server Spring Boot berjalan di port 8080.\n" + ex.getMessage());
                });
            }
        }).start();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void playAnimation(VBox root) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), root);
        tt.setFromY(20);
        tt.setToY(0);
        tt.play();
    }
}
