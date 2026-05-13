package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.util.Duration;
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

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DonationDetailUI extends UI {

    private Donation donation;
    private HBox topBar;
    private VBox infoCard;
    private boolean isEditMode = false;
    private Label statusLabel;

    private final String DARK_GREEN = "#0F5B21";
    private final String LIGHT_GREEN = "#D2F4D6";
    private final String TEXT_GRAY = "#757575";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BG_COLOR = "#F5F5F5";
    private final String BASE_URL = "http://localhost:8080";

    public DonationDetailUI(User user, Donation donation) {
        super(user);
        this.donation = donation;
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- TOP BAR ---
        topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        Label backArrow = new Label("←");
        backArrow.setFont(Font.font("System", FontWeight.BOLD, 22));
        backArrow.setTextFill(Color.web(DARK_GREEN));
        backArrow.setStyle("-fx-cursor: hand;");
        backArrow.setOnMouseClicked(e -> {
            if (SessionManager.getInstance().getRole().equalsIgnoreCase("DONATOR")) {
                Navigator.navigate(stage, new CatalogUI(getUser()));
            } else {
                Navigator.navigate(stage, new RecipientCatalogUI(getUser()));
            }
        });

        Label logoLabel = new Label("DONE-ATE");
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        logoLabel.setTextFill(Color.web(DARK_GREEN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String initials = getAvatarInitials();
        Label avatarLabel = new Label(initials);
        avatarLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane avatarPane = new StackPane(avatarLabel);
        avatarPane.setPrefSize(38, 38);
        avatarPane.setMinSize(38, 38);
        avatarPane.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-background-radius: 19px;");

        statusLabel = new Label();
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        topBar.getChildren().addAll(backArrow, logoLabel, statusLabel, spacer, avatarPane);

        // --- CONTENT AREA (SCROLLABLE) ---
        VBox scrollContent = new VBox();
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // Hero Image Section
        StackPane heroSection = new StackPane();
        heroSection.setPrefHeight(350);
        heroSection.setMaxHeight(350);
        heroSection.setStyle("-fx-background-color: " + LIGHT_GREEN + ";");

        ImageView foodImg = new ImageView();
        foodImg.setFitWidth(1000); 
        foodImg.setFitHeight(350);
        foodImg.setPreserveRatio(false);

        if (donation != null && donation.getDish() != null && donation.getDish().getImagePath() != null) {
            String path = donation.getDish().getImagePath();
            try {
                Image img = path.startsWith("http") 
                    ? new Image(path, 1000, 350, true, true, true)
                    : new Image("file:" + path, 1000, 350, true, true, true);
                if (!img.isError()) {
                    foodImg.setImage(img);
                }
            } catch (Exception e) {}
        }
        
        if (foodImg.getImage() == null) {
            Label ph = new Label("🍽");
            ph.setStyle("-fx-font-size: 80px;");
            heroSection.getChildren().add(ph);
        } else {
            heroSection.getChildren().add(foodImg);
        }

        // Info Card
        infoCard = new VBox(25);
        infoCard.setPadding(new Insets(40, 60, 40, 60));
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 30 30 0 0; -fx-translate-y: -30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, -5);");
        infoCard.setMaxWidth(1000);
        
        renderInfoCardContent(stage);

        scrollContent.getChildren().addAll(heroSection, infoCard);
        
        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");
        
        root.getChildren().addAll(topBar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return root;
    }

    private void renderInfoCardContent(Stage stage) {
        infoCard.getChildren().clear();

        String dishName = donation != null && donation.getDish() != null ? donation.getDish().getName() : "Donasi Makanan";
        String status = donation != null ? donation.getStatus() : "PENDING QC";
        
        if (isEditMode) {
            renderEditForm(stage);
        } else {
            renderViewDetails(stage, dishName, status);
        }
    }

    private void renderViewDetails(Stage stage, String dishName, String status) {
        Label title = new Label(dishName);
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setWrapText(true);

        String statusColor = switch (status.toUpperCase()) {
            case "QC PASSED", "SELESAI" -> "#1a7a1a";
            case "QC FAILED", "DIBATALKAN" -> "#c62828";
            default -> "#e67e00";
        };

        Label statusBadge = new Label(status.toUpperCase());
        statusBadge.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + statusColor + "; -fx-padding: 6 15; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 13;");

        HBox titleRow = new HBox(20, title, statusBadge);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label desc = new Label("Donasi ini dibagikan oleh " + (isUserDonatorOfThis() ? "Anda" : nvl(donation.getDonator() != null ? donation.getDonator().getUsername() : null, "Donator Terverifikasi")) + ". Makanan ini telah melewati pengecekan kualitas standar DONE-ATE untuk memastikan keamanan konsumsi.");
        desc.setFont(Font.font(16));
        desc.setTextFill(Color.web(TEXT_GRAY));
        desc.setWrapText(true);
        desc.setLineSpacing(5);

        // Details Grid
        GridPane grid = new GridPane();
        grid.setHgap(60);
        grid.setVgap(30);
        
        String timeCookedStr = donation.getTimeCooked() != null ? donation.getTimeCooked().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")) : "-";
        String timeLeft = computeTimeLeft();
        String location = donation.getDonator() != null ? nvl(donation.getDonator().getAddress(), "Lokasi tidak ditentukan") : "Lokasi tidak ditentukan";
        
        grid.add(detailItem("🕒 Waktu Masak", timeCookedStr), 0, 0);
        grid.add(detailItem("⌛ Kedaluwarsa", timeLeft), 1, 0);
        grid.add(detailItem("📍 Lokasi Penjemputan", location), 0, 1);
        grid.add(detailItem("📦 Status Klaim", donation.isTaken() ? "Sudah Diklaim" : "Tersedia"), 1, 1);

        Separator sep = new Separator();
        sep.setPadding(new Insets(15, 0, 15, 0));

        infoCard.getChildren().addAll(titleRow, desc, grid, sep);

        // Actions
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);

        if (SessionManager.getInstance().getRole().equalsIgnoreCase("RECIPIENT") && !donation.isTaken()) {
            Button claimBtn = new Button("Klaim Donasi Sekarang");
            claimBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 15 40;");
            claimBtn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(claimBtn, Priority.ALWAYS);
            claimBtn.setOnAction(e -> handleClaim(stage));
            actions.getChildren().add(claimBtn);
        } else if (isUserDonatorOfThis()) {
            Button editBtn = new Button("Edit Donasi");
            editBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 12 30;");
            editBtn.setOnAction(e -> {
                isEditMode = true;
                renderInfoCardContent(stage);
            });

            Button deleteBtn = new Button("Hapus Donasi");
            deleteBtn.setStyle("-fx-background-color: #FADBD8; -fx-text-fill: #C0392B; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 12 30;");
            deleteBtn.setOnAction(e -> handleDelete(stage));

            actions.getChildren().addAll(editBtn, deleteBtn);
        }

        infoCard.getChildren().add(actions);
    }

    private void renderEditForm(Stage stage) {
        Label title = new Label("Edit Informasi Donasi");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        VBox form = new VBox(20);
        
        TextField nameField = new TextField(donation.getDish().getName());
        nameField.setPromptText("Nama Hidangan");
        styleTextField(nameField);

        TextField expiresField = new TextField(String.valueOf(donation.getDish().getExpiresIn() != null ? donation.getDish().getExpiresIn().toMinutes() : ""));
        expiresField.setPromptText("Kedaluwarsa (menit)");
        styleTextField(expiresField);

        HBox timeCookedBox = new HBox(10);
        TextField timeField = new TextField(donation.getTimeCooked() != null ? donation.getTimeCooked().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
        timeField.setPromptText("yyyy-MM-dd HH:mm");
        styleTextField(timeField);
        HBox.setHgrow(timeField, Priority.ALWAYS);

        Button nowBtn = new Button("Sekarang");
        nowBtn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 15;");
        nowBtn.setOnAction(e -> timeField.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        timeCookedBox.getChildren().addAll(timeField, nowBtn);

        form.getChildren().addAll(
            new Label("Nama Makanan"), nameField,
            new Label("Kedaluwarsa (menit)"), expiresField,
            new Label("Waktu Dimasak"), timeCookedBox
        );

        HBox actions = new HBox(15);
        Button saveBtn = new Button("Simpan Perubahan");
        saveBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 12 30;");
        saveBtn.setOnAction(e -> handleUpdate(stage, nameField.getText(), expiresField.getText(), timeField.getText()));

        Button cancelBtn = new Button("Batal");
        cancelBtn.setStyle("-fx-background-color: #EEE; -fx-text-fill: #333; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 12 30;");
        cancelBtn.setOnAction(e -> {
            isEditMode = false;
            renderInfoCardContent(stage);
        });

        actions.getChildren().addAll(saveBtn, cancelBtn);
        infoCard.getChildren().addAll(title, form, actions);
    }

    private void handleUpdate(Stage stage, String name, String expires, String timeCooked) {
        if (name.isEmpty() || expires.isEmpty() || timeCooked.isEmpty()) {
            showStatus("Harap isi semua field.", true);
            return;
        }

        new Thread(() -> {
            try {
                String tempTime = timeCooked.replace(" ", "T");
                if (tempTime.length() == 16) tempTime += ":00";
                final String finalTime = tempTime;

                String body = String.format(
                    "{\"dishName\":\"%s\",\"expiresInMinutes\":%s,\"timeCooked\":\"%s\"}",
                    name.replace("\"", "\\\""), expires, finalTime
                );

                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/" + donation.getDonationId()))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        showStatus("Donasi berhasil diperbarui.", false);
                        isEditMode = false;
                        // Refresh data locally
                        donation.getDish().setName(name);
                        donation.setTimeCooked(LocalDateTime.parse(finalTime));
                        renderInfoCardContent(stage);
                    } else {
                        showStatus("Gagal memperbarui donasi.", true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showStatus("Terjadi kesalahan koneksi.", true));
            }
        }).start();
    }

    private void handleDelete(Stage stage) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Apakah Anda yakin ingin menghapus donasi ini?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        String token = SessionManager.getInstance().getToken();
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/api/catalog/" + donation.getDonationId()))
                                .header("Authorization", "Bearer " + token)
                                .DELETE()
                                .build();

                        HttpResponse<String> responseApi = client.send(request, HttpResponse.BodyHandlers.ofString());

                        Platform.runLater(() -> {
                            if (responseApi.statusCode() == 200) {
                                Navigator.navigate(stage, new CatalogUI(getUser()));
                            } else {
                                showStatus("Gagal menghapus donasi.", true);
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> showStatus("Terjadi kesalahan koneksi.", true));
                    }
                }).start();
            }
        });
    }

    private void handleClaim(Stage stage) {
        showStatus("Fitur klaim akan segera hadir.", false);
    }

    private VBox detailItem(String label, String value) {
        VBox box = new VBox(8);
        Label lbl = new Label(label);
        lbl.setFont(Font.font(13));
        lbl.setTextFill(Color.web(TEXT_GRAY));
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 17));
        val.setStyle("-fx-text-fill: #222;");
        box.getChildren().addAll(lbl, val);
        return box;
    }

    private String computeTimeLeft() {
        if (donation.getTimeCooked() == null || donation.getDish() == null || donation.getDish().getExpiresIn() == null) return "Waktu tidak diketahui";
        try {
            LocalDateTime expiry = donation.getTimeCooked().plus(donation.getDish().getExpiresIn());
            long remaining = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiry);
            if (remaining <= 0) return "Kedaluwarsa";
            if (remaining < 60) return "Sisa " + remaining + " Menit";
            long h = remaining / 60, m = remaining % 60;
            return m > 0 ? "Sisa " + h + " Jam " + m + " Mnt" : "Sisa " + h + " Jam";
        } catch (Exception e) {
            return "Waktu tidak diketahui";
        }
    }

    private boolean isUserDonatorOfThis() {
        if (donation.getDonator() == null) return false;
        String currentUsername = SessionManager.getInstance().getUsername();
        if (currentUsername == null && getUser() != null) currentUsername = getUser().getUsername();
        return donation.getDonator().getUsername().equals(currentUsername);
    }

    private String getAvatarInitials() {
        String u = SessionManager.getInstance().getUsername();
        if (u == null && getUser() != null) u = getUser().getUsername();
        if (u == null || u.isEmpty()) return "?";
        return u.length() >= 2 ? u.substring(0, 2).toUpperCase() : u.toUpperCase();
    }

    private String nvl(String s, String fallback) {
        return (s == null || s.isEmpty() || s.equals("null")) ? fallback : s;
    }

    private void styleTextField(TextField tf) {
        tf.setStyle("-fx-background-radius: 10; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10; -fx-padding: 15; -fx-font-size: 15;");
    }

    private void showStatus(String msg, boolean isError) {
        if (statusLabel == null) return;
        Platform.runLater(() -> {
            statusLabel.setText(msg);
            statusLabel.setStyle("-fx-background-color: " + (isError ? "#FFEBEE" : "#E8F5E9") + "; " +
                               "-fx-text-fill: " + (isError ? "#C62828" : "#2E7D32") + "; " +
                               "-fx-padding: 8 16; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 14px;");
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);

            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), statusLabel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            new Thread(() -> {
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> {
                    FadeTransition ftOut = new FadeTransition(Duration.seconds(0.5), statusLabel);
                    ftOut.setFromValue(1);
                    ftOut.setToValue(0);
                    ftOut.setOnFinished(ev -> {
                        statusLabel.setVisible(false);
                        statusLabel.setManaged(false);
                    });
                    ftOut.play();
                });
            }).start();
        });
    }



    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("DONE-ATE - Detail Donasi");
            Scene scene = new Scene(getSceneContent(stage));
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }
}

