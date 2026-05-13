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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * ClaimDonationUI — Halaman detail donasi + konfirmasi klaim (UC3).
 * Menampilkan informasi lengkap donasi yang dipilih penerima dari RecipientCatalogUI,
 * kemudian penerima mengkonfirmasi klaim melalui tombol di halaman ini.
 */
public class ClaimDonationUI extends UI {

    private Donation donation;

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String TEXT_GRAY    = "#757575";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String BG_COLOR     = "#F5F5F5";
    private static final String BASE_URL     = "http://localhost:8080";

    public ClaimDonationUI(User user, Donation donation) {
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
        stage.setTitle("DONE-ATE - Klaim Donasi");
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

        // Top bar
        root.getChildren().add(buildTopBar(stage));

        // Scrollable content
        VBox scrollContent = new VBox(0);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.getChildren().add(buildHeroImage());
        scrollContent.getChildren().add(buildDetailCard(stage));

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.getChildren().add(scrollPane);

        playAnimation(scrollContent);
        return root;
    }

    // ─── Top Bar ───────────────────────────────────────────────────────────────

    private HBox buildTopBar(Stage stage) {
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

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

        Label pageTitle = new Label("Klaim Donasi");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        pageTitle.setTextFill(Color.web(TEXT_GRAY));

        topBar.getChildren().addAll(backArrow, logoLabel, spacer, pageTitle);
        return topBar;
    }

    // ─── Hero Image ────────────────────────────────────────────────────────────

    private StackPane buildHeroImage() {
        StackPane heroSection = new StackPane();
        heroSection.setPrefHeight(280);
        heroSection.setMaxHeight(280);
        heroSection.setStyle("-fx-background-color: " + LIGHT_GREEN + ";");

        ImageView foodImg = new ImageView();
        foodImg.setFitWidth(800);
        foodImg.setFitHeight(280);
        foodImg.setPreserveRatio(false);

        if (donation != null && donation.getDish() != null && donation.getDish().getImagePath() != null) {
            try {
                String path = donation.getDish().getImagePath();
                Image img;
                if (path.startsWith("http")) {
                    img = new Image(path, 800, 280, false, true, true);
                } else {
                    File file = new File(path);
                    if (file.exists()) {
                        img = new Image(file.toURI().toString());
                    } else {
                        img = null;
                    }
                }
                if (img != null && !img.isError()) {
                    foodImg.setImage(img);
                }
            } catch (Exception ignored) {}
        }

        // Placeholder jika tidak ada gambar
        if (foodImg.getImage() == null) {
            Label placeholder = new Label("🍽");
            placeholder.setStyle("-fx-font-size: 72px;");
            heroSection.getChildren().add(placeholder);
        } else {
            heroSection.getChildren().add(foodImg);
        }

        // Status badge
        HBox statusBadge = new HBox(5);
        statusBadge.setAlignment(Pos.CENTER);
        statusBadge.setPadding(new Insets(5, 12, 5, 12));
        statusBadge.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-background-radius: 20px;");
        Label statusText = new Label("Tersedia untuk Diklaim");
        statusText.setTextFill(Color.WHITE);
        statusText.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusBadge.getChildren().add(statusText);

        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(statusBadge, new Insets(16));
        heroSection.getChildren().add(statusBadge);

        return heroSection;
    }

    // ─── Detail Card ───────────────────────────────────────────────────────────

    private VBox buildDetailCard(Stage stage) {
        VBox card = new VBox(20);
        card.setMaxWidth(750);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20px 20px 0 0;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, -5);"
        );
        VBox.setMargin(card, new Insets(-30, 0, 0, 0));

        // Nama makanan
        String dishName = (donation != null && donation.getDish() != null)
                ? donation.getDish().getName() : "Nama Hidangan";
        Label titleLabel = new Label(dishName);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setWrapText(true);

        // Badge kategori
        HBox badgeContainer = new HBox(10);
        badgeContainer.getChildren().addAll(
            createBadge("🍽 Perishable", "#E8F5E9", DARK_GREEN),
            createBadge("✓ Tersedia", LIGHT_GREEN, DARK_GREEN)
        );

        // Info boxes
        HBox infoBoxes = new HBox(15);
        infoBoxes.setAlignment(Pos.CENTER);

        String timeText = calculateTimeRemaining();
        VBox expiryBox = createInfoBox("WAKTU KEDALUWARSA", timeText, "Segera ambil sebelum habis", "⏱");
        VBox statusBox = createInfoBox("STATUS DONASI", "Tersedia", "Belum diklaim oleh siapa pun", "📋");
        HBox.setHgrow(expiryBox, Priority.ALWAYS);
        HBox.setHgrow(statusBox, Priority.ALWAYS);
        infoBoxes.getChildren().addAll(expiryBox, statusBox);

        // Info penerima
        VBox recipientInfo = new VBox(8);
        recipientInfo.setPadding(new Insets(16));
        recipientInfo.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12px;");
        Label recipientTitle = new Label("INFORMASI KLAIM");
        recipientTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        recipientTitle.setTextFill(Color.web(DARK_GREEN));
        String username = (getUser() != null && getUser().getUsername() != null)
                ? getUser().getUsername() : "Penerima";
        Label recipientDesc = new Label(
            "Donasi ini akan diklaim atas nama: " + username + "\n" +
            "Setelah diklaim, Anda perlu mengambil makanan dan memverifikasi dengan kode transaksi."
        );
        recipientDesc.setWrapText(true);
        recipientDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");
        recipientInfo.getChildren().addAll(recipientTitle, recipientDesc);

        // Tombol Konfirmasi Klaim
        Button claimBtn = new Button("✓ Konfirmasi Klaim Donasi");
        claimBtn.setMaxWidth(Double.MAX_VALUE);
        claimBtn.setPrefHeight(54);
        claimBtn.setStyle(
            "-fx-background-color: " + DARK_GREEN + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-background-radius: 12px;" +
            "-fx-cursor: hand;"
        );
        claimBtn.setOnMouseEntered(e -> claimBtn.setStyle(claimBtn.getStyle().replace(DARK_GREEN, "#0a4218")));
        claimBtn.setOnMouseExited(e -> claimBtn.setStyle(claimBtn.getStyle().replace("#0a4218", DARK_GREEN)));
        claimBtn.setOnAction(e -> handleClaim(stage, claimBtn));

        // Tombol Kembali
        Button backBtn = new Button("← Kembali ke Katalog");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(44);
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 12px;" +
            "-fx-text-fill: " + TEXT_GRAY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> stage.close());

        card.getChildren().addAll(titleLabel, badgeContainer, infoBoxes, recipientInfo, claimBtn, backBtn);
        return card;
    }

    // ─── Claim API Call ────────────────────────────────────────────────────────

    private void handleClaim(Stage stage, Button claimBtn) {
        if (donation == null || donation.getDonationId() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Data donasi tidak valid.");
            return;
        }

        String username = (getUser() != null) ? getUser().getUsername() : null;
        if (username == null) {
            username = SessionManager.getInstance().getUsername();
        }
        if (username == null || username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Username penerima tidak ditemukan.");
            return;
        }

        claimBtn.setDisable(true);
        claimBtn.setText("Memproses klaim...");

        final String recipientUsername = username;
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                String jsonPayload = String.format(
                    "{\"donationId\":\"%s\", \"recipientUsername\":\"%s\"}",
                    donation.getDonationId().toString(), recipientUsername
                );

                HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/claims"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload));

                if (token != null) builder.header("Authorization", "Bearer " + token);

                HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    claimBtn.setDisable(false);
                    claimBtn.setText("✓ Konfirmasi Klaim Donasi");
                    if (response.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Klaim Berhasil! 🎉",
                            "Donasi berhasil diklaim!\n\n" + response.body() +
                            "\n\nSimpan kode transaksi Anda untuk proses serah terima.");
                        stage.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Klaim Gagal", response.body());
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    claimBtn.setDisable(false);
                    claimBtn.setText("✓ Konfirmasi Klaim Donasi");
                    showAlert(Alert.AlertType.ERROR, "Koneksi Gagal",
                        "Pastikan Server Spring Boot berjalan di port 8080.\n" + ex.getMessage());
                });
            }
        }).start();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String calculateTimeRemaining() {
        if (donation == null || donation.getTimeCooked() == null || donation.getDish() == null) {
            return "Waktu tidak diketahui";
        }
        try {
            LocalDateTime cooked = donation.getTimeCooked();
            long expMins = donation.getDish().getExpiresIn().toMinutes();
            long remaining = ChronoUnit.MINUTES.between(LocalDateTime.now(), cooked.plusMinutes(expMins));
            if (remaining <= 0) return "Sudah kedaluwarsa";
            if (remaining < 60) return "Sisa " + remaining + " menit";
            long h = remaining / 60, m = remaining % 60;
            return m > 0 ? "Sisa " + h + " jam " + m + " menit" : "Sisa " + h + " jam";
        } catch (Exception e) {
            return "Waktu tidak diketahui";
        }
    }

    private Label createBadge(String text, String bgColor, String textColor) {
        Label badge = new Label(text);
        badge.setPadding(new Insets(4, 10, 4, 10));
        badge.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12px;");
        badge.setTextFill(Color.web(textColor));
        badge.setFont(Font.font("System", FontWeight.NORMAL, 12));
        return badge;
    }

    private VBox createInfoBox(String label, String value, String subValue, String icon) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: " + BORDER_COLOR +
                "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        HBox header = new HBox(5);
        Label iconLbl = new Label(icon);
        Label titleLbl = new Label(label);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 10));
        titleLbl.setTextFill(Color.web(TEXT_GRAY));
        header.getChildren().addAll(iconLbl, titleLbl);

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        valLbl.setWrapText(true);

        Label subLbl = new Label(subValue);
        subLbl.setFont(Font.font("System", 11));
        subLbl.setTextFill(Color.web(TEXT_GRAY));

        box.getChildren().addAll(header, valLbl, subLbl);
        return box;
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
