package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
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
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class CatalogUI extends UI {

    private static boolean jfxInitialized = false;

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String LIGHT_RED    = "#FADBD8";
    private static final String TEXT_GRAY    = "#555555";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String BG_COLOR     = "#FAFAFA";
    private static final String BASE_URL     = "http://localhost:8080";

    private VBox catalogList;
    private Label statusLabel;

    public CatalogUI(User user) {
        super(user);
    }

    @Override
    public void showUI() {
        if (!jfxInitialized) {
            try {
                Platform.startup(() -> {});
                jfxInitialized = true;
            } catch (IllegalStateException e) {
                jfxInitialized = true;
            }
        }
        Platform.runLater(this::createAndShowStage);
    }

    private void createAndShowStage() {
        String role = SessionManager.getInstance().getRole();
        if (!"DONATOR".equalsIgnoreCase(role)) {
            showAccessDenied();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Katalog Donasi");
        stage.setMaximized(true);
        showCatalogScene(stage);
        stage.show();
    }

    private void showAccessDenied() {
        Stage dialog = new Stage();
        dialog.setTitle("Akses Ditolak");

        VBox content = new VBox(16);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");
        content.setPrefWidth(380);

        Label icon = new Label("🚫");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Akses Ditolak");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #c62828;");

        Label msg = new Label("Halaman ini hanya dapat diakses oleh Donator.");
        msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        msg.setWrapText(true);
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button okBtn = new Button("Kembali ke Login");
        okBtn.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10 24 10 24;"
        );
        okBtn.setOnAction(e -> {
            dialog.close();
            SessionManager.getInstance().clearSession();
            new LoginUI().showUI();
        });

        content.getChildren().addAll(icon, title, msg, okBtn);
        dialog.setScene(new javafx.scene.Scene(content));
        dialog.show();
    }

    private void showCatalogScene(Stage stage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 80, 50, 80));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // Header
        Label title = new Label("Katalog Donasi Saya");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));

        String username = resolveUsername().isEmpty() ? "-" : resolveUsername();
        Label subtitle = new Label("Kelola daftar donasi yang telah Anda posting sebagai " + username + ".");
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        subtitle.setFont(Font.font("System", 16));
        subtitle.setWrapText(true);

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setVisible(false);

        VBox header = new VBox(10, title, subtitle, statusLabel);

        // Main layout
        HBox columns = new HBox(40);
        columns.setAlignment(Pos.TOP_LEFT);

        // Left: catalog list
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label listTitle = new Label("Donasi Aktif");
        listTitle.setFont(Font.font("System", FontWeight.BOLD, 20));

        catalogList = new VBox(12);
        leftCol.getChildren().addAll(listTitle, catalogList);

        // Right: actions panel
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(380);

        Button addBtn = new Button("+ Tambah Donasi Baru");
        addBtn.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );
        addBtn.setPrefWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(52);
        addBtn.setOnAction(e -> {
            InputDonationUI inputUI = new InputDonationUI(getUser());
            inputUI.showUI();
        });

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle(
                "-fx-background-color: " + LIGHT_GREEN + ";" +
                "-fx-background-radius: 8px;"
        );
        Label infoTitle = new Label("Status Donasi");
        infoTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        infoTitle.setTextFill(Color.web(DARK_GREEN));
        Label infoDesc = new Label(
                "QC Passed  — Donasi aktif dan bisa diklaim.\n" +
                "QC Pending — Menunggu pemeriksaan kualitas.\n" +
                "QC Failed  — Donasi tidak lolos QC.\n" +
                "Removed    — Donasi telah dihapus."
        );
        infoDesc.setFont(Font.font("System", 13));
        infoDesc.setWrapText(true);
        infoBox.getChildren().addAll(infoTitle, infoDesc);

        rightCol.getChildren().addAll(addBtn, infoBox);
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

        playAnimation(root);
        loadCatalog(username);
    }

    // ─── Load catalog from API ─────────────────────────────────────────────────

    private void loadCatalog(String username) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/donator/" + username))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    catalogList.getChildren().clear();
                    if (response.statusCode() == 200) {
                        parseAndRenderCatalog(response.body());
                    } else {
                        showStatus("Gagal memuat katalog.", true);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showStatus("Tidak dapat terhubung ke server.", true));
            }
        }).start();
    }

    private void parseAndRenderCatalog(String json) {
        // Minimal JSON parsing — split by donationId entries
        if (json.equals("[]") || json.isEmpty()) {
            Label empty = new Label("Belum ada donasi dalam katalog Anda.");
            empty.setTextFill(Color.web(TEXT_GRAY));
            empty.setFont(Font.font("System", 15));
            catalogList.getChildren().add(empty);
            return;
        }

        String[] entries = json.split("\\{\"donationId\":");
        for (int i = 1; i < entries.length; i++) {
            String entry = "{\"donationId\":" + entries[i];
            if (entry.endsWith(",")) entry = entry.substring(0, entry.length() - 1);
            if (entry.endsWith("]")) entry = entry.substring(0, entry.length() - 1);

            String donationId  = extractValue(entry, "donationId");
            String dishName    = extractValue(entry, "dishName");
            String status      = extractValue(entry, "status");
            String timeAdded   = extractValue(entry, "timeAdded");
            String expiresIn   = extractValue(entry, "expiresInMinutes");
            boolean taken      = entry.contains("\"taken\":true");

            catalogList.getChildren().add(
                    buildCatalogCard(donationId, dishName, status, timeAdded, expiresIn, taken)
            );
        }
    }

    private VBox buildCatalogCard(String donationId, String dishName, String status,
                                  String timeAdded, String expiresIn, boolean taken) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;"
        );

        Label nameLabel = new Label(dishName != null ? dishName : "-");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 17));

        String statusColor = switch (status != null ? status : "") {
            case "QC Passed"  -> "#1a7a1a";
            case "QC Failed"  -> "#c62828";
            case "Removed"    -> "#888";
            default           -> "#e67e00";
        };
        Label statusLabel = new Label("Status: " + (status != null ? status : "-"));
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

        Label timeLabel = new Label("Ditambahkan: " + (timeAdded != null ? timeAdded.replace("T", " ").substring(0, Math.min(16, timeAdded.length())) : "-"));
        timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");

        Label expiresLabel = new Label("Kedaluwarsa: " + expiresIn + " menit");
        expiresLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");

        Label takenLabel = new Label(taken ? "● Sudah diklaim" : "● Belum diklaim");
        takenLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (taken ? "#1a7a1a" : TEXT_GRAY) + ";");

        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = new Button("Edit");
        editBtn.setStyle(
                "-fx-background-color: " + LIGHT_GREEN + ";" +
                "-fx-text-fill: " + DARK_GREEN + ";" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 20 8 20;"
        );
        editBtn.setOnAction(e -> showEditDialog(donationId, dishName, expiresIn));

        Button removeBtn = new Button("Hapus");
        removeBtn.setStyle(
                "-fx-background-color: " + LIGHT_RED + ";" +
                "-fx-text-fill: #c62828;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 20 8 20;"
        );
        removeBtn.setOnAction(e -> removeDonation(donationId));

        actions.getChildren().addAll(editBtn, removeBtn);
        card.getChildren().addAll(nameLabel, statusLabel, timeLabel, expiresLabel, takenLabel, actions);
        return card;
    }

    // ─── Edit dialog ───────────────────────────────────────────────────────────

    private void showEditDialog(String donationId, String currentName, String currentExpires) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Donasi");

        VBox content = new VBox(16);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");
        content.setPrefWidth(420);

        Label title = new Label("Edit Informasi Donasi");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        TextField nameField = new TextField(currentName != null ? currentName : "");
        nameField.setPromptText("Nama Hidangan");
        styleTextField(nameField);

        TextField expiresField = new TextField(currentExpires != null ? currentExpires : "");
        expiresField.setPromptText("Kedaluwarsa (menit)");
        styleTextField(expiresField);

        TextField timeCookedField = new TextField();
        timeCookedField.setPromptText("Waktu Dimasak (yyyy-MM-dd HH:mm)");
        styleTextField(timeCookedField);

        Button nowBtn = new Button("Sekarang");
        nowBtn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        nowBtn.setOnAction(e -> {
            String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            timeCookedField.setText(now);
        });

        HBox timeCookedBox = new HBox(10, timeCookedField, nowBtn);
        HBox.setHgrow(timeCookedField, Priority.ALWAYS);

        Button saveBtn = new Button("Simpan Perubahan");
        saveBtn.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );
        saveBtn.setPrefWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(46);
        saveBtn.setOnAction(e -> {
            String name        = nameField.getText().trim();
            String expires     = expiresField.getText().trim();
            String timeCooked  = timeCookedField.getText().trim();

            if (name.isEmpty() || expires.isEmpty() || timeCooked.isEmpty()) {
                showStatus("Harap isi semua field.", true);
                dialog.close();
                return;
            }

            String timeCookedFormatted = timeCooked.replace(" ", "T");
            if (timeCookedFormatted.length() == 16) timeCookedFormatted += ":00";

            String body = String.format(
                    "{\"dishName\":\"%s\",\"imagePath\":\"\",\"expiresInMinutes\":%s,\"timeCooked\":\"%s\"}",
                    esc(name), expires, timeCookedFormatted
            );

            updateDonation(donationId, body);
            dialog.close();
        });

        content.getChildren().addAll(
                title,
                new Label("Nama Makanan"), nameField,
                new Label("Kedaluwarsa (menit)"), expiresField,
                new Label("Waktu Dimasak"), timeCookedBox,
                saveBtn
        );

        dialog.setScene(new Scene(content));
        dialog.show();
    }

    // ─── API calls ─────────────────────────────────────────────────────────────

    private void removeDonation(String donationId) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/" + donationId))
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        showStatus("Donasi berhasil dihapus dari katalog.", false);
                        String username = resolveUsername();
                        loadCatalog(username);
                    } else {
                        showStatus("Gagal menghapus donasi.", true);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showStatus("Tidak dapat terhubung ke server.", true));
            }
        }).start();
    }

    private void updateDonation(String donationId, String body) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/" + donationId))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        showStatus("Donasi berhasil diperbarui.", false);
                        String username = resolveUsername();
                        loadCatalog(username);
                    } else {
                        showStatus("Gagal memperbarui donasi.", true);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showStatus("Tidak dapat terhubung ke server.", true));
            }
        }).start();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String resolveUsername() {
        if (getUser() != null && getUser().getUsername() != null) return getUser().getUsername();
        String s = SessionManager.getInstance().getUsername();
        return s != null ? s : "";
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (isError ? "#c62828" : DARK_GREEN) + ";");
        statusLabel.setVisible(true);
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int start = idx + search.length();
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('}', start);
            return json.substring(start, end).trim();
        }
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void styleTextField(TextField tf) {
        tf.setStyle(
                "-fx-background-radius: 6px;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 6px;" +
                "-fx-padding: 12px;" +
                "-fx-font-size: 14px;"
        );
    }

    private void playAnimation(VBox root) {
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(600), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(600), root);
        tt.setFromY(30);
        tt.setToY(0);
        tt.play();
    }

    public static void main(String[] args) {
        CatalogUI ui = new CatalogUI(null);
        ui.showUI();
    }
}
