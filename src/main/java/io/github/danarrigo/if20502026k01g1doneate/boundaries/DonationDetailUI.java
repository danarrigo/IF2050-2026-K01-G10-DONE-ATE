package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.UUID;

public class DonationDetailUI extends UI {

    private static boolean jfxInitialized = false;
    private Donation donation;

    private final String DARK_GREEN = "#0F5B21";
    private final String LIGHT_GREEN = "#D2F4D6";
    private final String TEXT_GRAY = "#757575";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BG_COLOR = "#F5F5F5";
    private final String RED = "#C0392B";

    public DonationDetailUI(User user, Donation donation) {
        super(user);
        this.donation = donation;
    }

    public static void main(String[] args) {
        // Mock data for testing
        Dish mockDish = new Dish("Chicken & Avocado Fresh Salad", "src/main/resources/mock_salad.jpg");
        Donation mockDonation = new Donation(mockDish, LocalDateTime.now(), LocalDateTime.now().minusHours(2),
                "Tersedia", null);

        // Test with a Recipient user to see the Claim Button
        Recipient mockRecipient = new Recipient();
        mockRecipient.setUsername("RecipientAlice");

        DonationDetailUI ui = new DonationDetailUI(mockRecipient, mockDonation);
        ui.showUI();
    }

    @Override
    public void showUI() {
        Platform.runLater(this::createAndShowStage);
    }

    private void createAndShowStage() {
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Detail Donasi");
        stage.setFullScreen(true);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white;");

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

        // Profile placeholder
        Circle profileCircle = new Circle(18, Color.web("#E0E0E0"));

        topBar.getChildren().addAll(backArrow, logoLabel, spacer, profileCircle);

        // --- CONTENT AREA (SCROLLABLE) ---
        VBox scrollContent = new VBox();
        scrollContent.setAlignment(Pos.TOP_CENTER);

        // Hero Image Section
        StackPane heroSection = new StackPane();
        heroSection.setPrefHeight(300);
        heroSection.setMaxHeight(300);

        ImageView foodImg = new ImageView();
        foodImg.setFitWidth(800); // Fixed width for mobile-like feel or adjust as needed
        foodImg.setFitHeight(300);
        foodImg.setPreserveRatio(false);

        if (donation != null && donation.getDish() != null && donation.getDish().getImagePath() != null) {
            try {
                File file = new File(donation.getDish().getImagePath());
                if (file.exists()) {
                    foodImg.setImage(new Image(file.toURI().toString()));
                }
            } catch (Exception e) {
                // Fallback or ignore
            }
        }

        // Status Badge Overlay
        HBox statusBadge = new HBox(5);
        statusBadge.setAlignment(Pos.CENTER);
        statusBadge.setPadding(new Insets(5, 12, 5, 12));
        statusBadge.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-background-radius: 20px;");
        Label statusIcon = new Label("✓");
        statusIcon.setTextFill(Color.WHITE);
        Label statusText = new Label(donation != null ? donation.getStatus() : "Tersedia");
        statusText.setTextFill(Color.WHITE);
        statusText.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusBadge.getChildren().addAll(statusIcon, statusText);

        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(statusBadge, new Insets(20));

        heroSection.getChildren().addAll(foodImg, statusBadge);

        // Main Detail Card
        VBox card = new VBox(20);
        card.setMaxWidth(750);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20px 20px 0 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, -5);");
        VBox.setMargin(card, new Insets(-30, 0, 0, 0)); // Negative margin to overlap

        // Title and Badges
        VBox titleArea = new VBox(10);
        Label titleLabel = new Label(
                donation != null && donation.getDish() != null ? donation.getDish().getName() : "Nama Hidangan");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setWrapText(true);

        HBox badgeContainer = new HBox(10);
        badgeContainer.getChildren().addAll(
                createBadge("🍽 Perishable", "#E8F5E9", DARK_GREEN),
                createBadge("🌱 Vegetarian Option", "#E8F5E9", DARK_GREEN));

        titleArea.getChildren().addAll(titleLabel, badgeContainer);

        // Info Boxes
        HBox infoBoxes = new HBox(15);
        infoBoxes.setAlignment(Pos.CENTER);

        VBox locationBox = createInfoBox("LOKASI DONASI", "Jl. Tubagus Ismail no 2c", "2.4 km dari lokasi Anda", "📍");
        VBox expiryBox = createInfoBox("WAKTU KADALUARSA", "Berakhir dalam 3 jam", "Hingga 21:00 WIB Hari Ini", "⏲");
        HBox.setHgrow(locationBox, Priority.ALWAYS);
        HBox.setHgrow(expiryBox, Priority.ALWAYS);

        infoBoxes.getChildren().addAll(locationBox, expiryBox);

        card.getChildren().addAll(titleArea, infoBoxes);

        // Verification Code Section (Only for Donators)
        if (getUser() instanceof Donator) {
            VBox verifCard = new VBox(10);
            verifCard.setAlignment(Pos.CENTER);
            verifCard.setPadding(new Insets(20));
            verifCard.setStyle("-fx-border-color: " + DARK_GREEN
                    + "; -fx-border-radius: 12px; -fx-background-color: #FAFAFA; -fx-background-radius: 12px;");

            Label verifHeader = new Label("KODE VERIFIKASI ANDA");
            verifHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
            verifHeader.setTextFill(Color.web(DARK_GREEN));

            Label codeLabel = new Label("D N 8 A X 2");
            codeLabel.setFont(Font.font("System", FontWeight.BOLD, 42));
            codeLabel.setStyle("-fx-letter-spacing: 5px;");

            Label verifDesc = new Label(
                    "Tunjukkan kode ini kepada penerima donasi saat pengambilan untuk memverifikasi transaksi.");
            verifDesc.setTextFill(Color.web(TEXT_GRAY));
            verifDesc.setFont(Font.font(12));
            verifDesc.setAlignment(Pos.CENTER);
            verifDesc.setWrapText(true);

            verifCard.getChildren().addAll(verifHeader, codeLabel, verifDesc);
            card.getChildren().add(verifCard);
        }

        // Action Buttons
        VBox actionArea = new VBox(15);

        // Claim Button (Only for Recipients and if status is "Tersedia")
        if (getUser() instanceof Recipient && donation != null && "Tersedia".equalsIgnoreCase(donation.getStatus())) {
            Button claimBtn = new Button("✓ Klaim Donasi");
            claimBtn.setMaxWidth(Double.MAX_VALUE);
            claimBtn.setPrefHeight(50);
            claimBtn.setStyle("-fx-background-color: " + DARK_GREEN
                    + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 8px; -fx-cursor: hand;");
            actionArea.getChildren().add(claimBtn);
        }

        Button cancelBtn = new Button("✕ Batal Klaim");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setPrefHeight(50);
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + RED
                + "; -fx-border-radius: 8px; -fx-text-fill: " + RED
                + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> handleRemoveDonation(stage));
        actionArea.getChildren().add(cancelBtn);

        if (!actionArea.getChildren().isEmpty()) {
            card.getChildren().add(actionArea);
        }

        scrollContent.getChildren().addAll(heroSection, card);

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");

        // --- BOTTOM NAVIGATION ---
        HBox bottomNav = new HBox();
        bottomNav.setAlignment(Pos.CENTER);
        bottomNav.setPadding(new Insets(10));
        bottomNav.setSpacing(40);
        bottomNav.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR
                + " transparent transparent transparent;");

        bottomNav.getChildren().addAll(
                createNavItem("🏠", "Home"),
                createNavItem("🍱", "Catalog", true),
                createNavItem("✉", "Inbox"),
                createNavItem("📜", "History"),
                createNavItem("👤", "Account"));

        root.getChildren().addAll(topBar, scrollPane, bottomNav);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 400, 800); // Mobile aspect ratio
        stage.setScene(scene);
        stage.show();
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
        box.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");

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

    private VBox createNavItem(String icon, String label) {
        return createNavItem(icon, label, false);
    }

    private VBox createNavItem(String icon, String label, boolean active) {
        VBox item = new VBox(2);
        item.setAlignment(Pos.CENTER);
        item.setPadding(new Insets(5, 10, 5, 10));

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(20));

        Label textLbl = new Label(label);
        textLbl.setFont(Font.font(10));

        if (active) {
            item.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 8px;");
            iconLbl.setTextFill(Color.web(DARK_GREEN));
            textLbl.setTextFill(Color.web(DARK_GREEN));
            textLbl.setFont(Font.font("System", FontWeight.BOLD, 10));
        } else {
            iconLbl.setTextFill(Color.web(TEXT_GRAY));
            textLbl.setTextFill(Color.web(TEXT_GRAY));
        }

        item.getChildren().addAll(iconLbl, textLbl);
        item.setStyle(item.getStyle() + "; -fx-cursor: hand;");
        return item;
    }

    private void handleRemoveDonation(Stage stage) {
        if (donation == null || donation.getDonationId() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID Donasi tidak ditemukan.");
            return;
        }

        String token = SessionManager.getInstance().getToken();
        UUID donationId = donation.getDonationId();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/donations/" + donationId + "/remove"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Donasi berhasil dibatalkan.");
                            stage.close();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal membatalkan donasi: " + response.body());
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan koneksi: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
