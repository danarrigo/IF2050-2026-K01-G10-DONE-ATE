package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.dtos.QCFormData;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.UUID;

public class InputDonationUI extends UI {

    private static boolean jfxInitialized = false;

    // Colors matching Figma
    private final String DARK_GREEN = "#0F5B21";
    private final String LIGHT_GREEN = "#D2F4D6";
    private final String LIGHT_RED = "#FADBD8";
    private final String TEXT_GRAY = "#555555";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BG_COLOR = "#FAFAFA";

    public InputDonationUI(User user) {
        super(user);
    }

    public static void main(String[] args) {
        InputDonationUI ui = new InputDonationUI(null);
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
        stage.setTitle("DONE-ATE - Input Donasi");
        stage.setMaximized(true); // Fit to fullscreen
        showInputDonationScene(stage);
        stage.show();
    }

    private void showInputDonationScene(Stage stage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 80, 50, 80));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // Header
        Label backBtn = new Label("<- Kembali");
        backBtn.setTextFill(Color.web(DARK_GREEN));
        backBtn.setStyle("-fx-cursor: hand;");

        Label title = new Label("Donasi Baru");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));

        Label subtitle = new Label(
                "Bagikan kelebihan makanan Anda dengan aman. Pastikan semua informasi akurat untuk mempermudah proses kurasi kualitas oleh tim kami.");
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        subtitle.setFont(Font.font("System", 16));
        subtitle.setWrapText(true);

        VBox header = new VBox(10, backBtn, title, subtitle);

        // Layout: Two Columns centered and growing
        HBox columns = new HBox(50);
        columns.setAlignment(Pos.TOP_LEFT);

        // --- Left Column ---
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS); // Grow to fill left side

        // Card 1: Informasi Makanan (Mapped strictly to backend entities)
        VBox card1 = createCard("Informasi Makanan");

        TextField nameField = new TextField();
        nameField.setPromptText("Nama Hidangan");
        styleTextField(nameField);

        HBox imageInputBox = new HBox(10);
        imageInputBox.setAlignment(Pos.CENTER_LEFT);

        TextField imagePathField = new TextField();
        imagePathField.setPromptText("Belum ada gambar yang dipilih...");
        imagePathField.setEditable(false);
        styleTextField(imagePathField);
        HBox.setHgrow(imagePathField, Priority.ALWAYS);

        Button browseBtn = new Button("Pilih File");
        browseBtn.setStyle("-fx-background-color: " + DARK_GREEN
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        browseBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Pilih Gambar Makanan");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            java.io.File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                imagePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        imageInputBox.getChildren().addAll(imagePathField, browseBtn);

        TextField expiresField = new TextField();
        expiresField.setPromptText("Kedaluwarsa dalam (contoh: 24)");
        styleTextField(expiresField);

        HBox timeCookedBox = new HBox(10);
        timeCookedBox.setAlignment(Pos.CENTER_LEFT);

        TextField timeCookedField = new TextField();
        timeCookedField.setPromptText("Waktu Dimasak (contoh: 2026-05-12 18:00)");
        styleTextField(timeCookedField);
        HBox.setHgrow(timeCookedField, Priority.ALWAYS);

        Button nowBtn = new Button("Sekarang");
        nowBtn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + DARK_GREEN
                + "; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        nowBtn.setOnAction(e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm");
            timeCookedField.setText(now.format(formatter));
        });

        timeCookedBox.getChildren().addAll(timeCookedField, nowBtn);

        card1.getChildren().addAll(
                new Label("Nama Makanan"), nameField,
                new Label("Gambar Makanan"), imageInputBox,
                new Label("Kedaluwarsa dalam (jam)"), expiresField,
                new Label("Waktu Dimasak (yyyy-MM-dd HH:mm)"), timeCookedBox);

        leftCol.getChildren().add(card1);

        // --- Right Column ---
        VBox rightCol = new VBox(30);
        rightCol.setPrefWidth(450); // Fixed width for right side panel

        // Info Box
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(25));
        infoBox.setStyle(
                "-fx-background-color: " + LIGHT_GREEN + "; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        Label infoTitle = new Label("Apa itu Digital QC?");
        infoTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        infoTitle.setTextFill(Color.web(DARK_GREEN));
        Label infoDesc = new Label(
                "Tahap selanjutnya melibatkan pemeriksaan visual melalui foto AI untuk memastikan makanan layak dikonsumsi sesuai standar DONE-ATE.");
        infoDesc.setWrapText(true);
        infoDesc.setFont(Font.font(14));
        infoBox.getChildren().addAll(infoTitle, infoDesc);

        // Button
        Button nextBtn = new Button("Lanjut ke Digital QC ->");
        nextBtn.setStyle("-fx-background-color: " + DARK_GREEN
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 8px; -fx-cursor: hand;");
        nextBtn.setPrefWidth(Double.MAX_VALUE);
        nextBtn.setPrefHeight(60);
        nextBtn.setOnAction(e -> {
            String dishName = nameField.getText();
            String imagePath = imagePathField.getText();
            String expiresIn = expiresField.getText();
            String timeCooked = timeCookedField.getText();

            if (dishName == null || dishName.trim().isEmpty() ||
                    imagePath == null || imagePath.trim().isEmpty() ||
                    expiresIn == null || expiresIn.trim().isEmpty() ||
                    timeCooked == null || timeCooked.trim().isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validasi Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Harap lengkapi semua field (termasuk gambar) sebelum melanjutkan ke Digital QC.");
                alert.showAndWait();
                return;
            }

            showDigitalQCScene(stage, dishName, imagePath, expiresIn, timeCooked);
        });

        rightCol.getChildren().addAll(infoBox, nextBtn);

        columns.getChildren().addAll(leftCol, rightCol);
        root.getChildren().addAll(header, columns);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        // Replace Scene root to prevent exiting maximized state
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(scroll);
            stage.setScene(scene);
            stage.setMaximized(true);
        } else {
            scene.setRoot(scroll);
        }

        // Animations
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(600),
                root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(600), root);
        tt.setFromY(30);
        tt.setToY(0);
        tt.play();
    }

    private void showDigitalQCScene(Stage stage, String dishName, String imagePath, String expiresIn,
            String timeCooked) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(50, 80, 50, 80));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // Header
        Label title = new Label("✔ QC DIGITAL");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(DARK_GREEN));

        Label subtitle1 = new Label("Validasi Kelayakan");
        subtitle1.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label subtitle2 = new Label("Pastikan donasi Anda memenuhi standar kualitas sebelum diproses oleh kurir kami.");
        subtitle2.setTextFill(Color.web(TEXT_GRAY));
        subtitle2.setFont(Font.font("System", 16));
        subtitle2.setWrapText(true);

        VBox header = new VBox(5, title, subtitle1, subtitle2);

        // Layout: Two Columns
        HBox columns = new HBox(50);
        columns.setAlignment(Pos.TOP_LEFT);

        // --- Left Column ---
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        VBox card1 = createCard("Daftar Periksa Keamanan");

        // Match backend fields exactly
        CheckBox[] checkBoxes = new CheckBox[10];
        String[] descriptions = {
                "Apakah hidangan berbau segar dan normal?",
                "Apakah makanan bebas dari tanda-tanda basi atau rusak?",
                "Apakah hidangan dimasak/disiapkan dengan benar?",
                "Apakah ada bahan kedaluwarsa yang digunakan? (Pilih jika ADA - akan menggagalkan QC)",
                "Apakah hidangan bebas dari benda asing (rambut, plastik, serangga, dll.)?",
                "Apakah makanan disimpan pada suhu yang aman?",
                "Apakah warna hidangan terlihat normal dan layak?",
                "Apakah tekstur makanan masih layak untuk dikonsumsi?",
                "Apakah hidangan aman untuk dimakan?",
                "Apakah hidangan layak disajikan kepada pelanggan?"
        };

        for (int i = 0; i < 10; i++) {
            checkBoxes[i] = new CheckBox(descriptions[i]);
            checkBoxes[i].setWrapText(true);
            checkBoxes[i].setFont(Font.font("System", 14));

            VBox cbContainer = new VBox(checkBoxes[i]);
            cbContainer.setPadding(new Insets(15));
            cbContainer.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 6px;");
            card1.getChildren().add(cbContainer);
        }

        // Error Box (Hidden by default)
        VBox errorBox = new VBox(5);
        errorBox.setPadding(new Insets(20));
        errorBox.setStyle("-fx-background-color: " + LIGHT_RED
                + "; -fx-border-color: #F1948A; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        Label errorTitle = new Label("! Peringatan QC Gagal");
        errorTitle.setTextFill(Color.web("#C0392B"));
        errorTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        Label errorDesc = new Label(
                "Beberapa poin kriteria belum terpenuhi. Donasi tidak dapat disubmit sebelum semua checklist divalidasi.");
        errorDesc.setTextFill(Color.web("#C0392B"));
        errorDesc.setWrapText(true);
        errorDesc.setFont(Font.font("System", 13));
        errorBox.getChildren().addAll(errorTitle, errorDesc);
        errorBox.setVisible(false);

        leftCol.getChildren().addAll(card1, errorBox);

        // --- Right Column ---
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(450);

        VBox card2 = createCard("Bukti Visual");

        StackPane imgPlaceholder = new StackPane();
        imgPlaceholder.setPrefHeight(200);
        imgPlaceholder.setStyle("-fx-background-color: #EAEAEA; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        try {
            java.io.File imgFile = new java.io.File(imagePath);
            if (imgFile.exists()) {
                String imageUri = imgFile.toURI().toString();
                javafx.scene.image.Image img = new javafx.scene.image.Image(imageUri);
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitHeight(180);
                imgView.setPreserveRatio(true);
                imgPlaceholder.getChildren().add(imgView);
                imgPlaceholder.setStyle("-fx-background-color: transparent;");
            }
        } catch (Exception ex) {
            System.out.println("Gagal memuat gambar QC: " + ex.getMessage());
        }

        Label soonLabel = new Label("Bukti Foto Yang Diunggah");
        soonLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        soonLabel.setTextFill(Color.web(TEXT_GRAY));
        soonLabel.setAlignment(Pos.CENTER);
        soonLabel.setMaxWidth(Double.MAX_VALUE);

        card2.getChildren().addAll(imgPlaceholder, soonLabel);

        Button submitBtn = new Button("Submit Donasi ->");
        submitBtn.setStyle("-fx-background-color: " + DARK_GREEN
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 8px; -fx-cursor: hand;");
        submitBtn.setPrefWidth(Double.MAX_VALUE);
        submitBtn.setPrefHeight(60);
        submitBtn.setOnAction(e -> {
            QCFormData form = new QCFormData();
            form.setDishId(UUID.randomUUID()); // Sementara UUID random

            form.setFreshScent(checkBoxes[0].isSelected());
            form.setNoSpoilage(checkBoxes[1].isSelected());
            form.setProperlyCooked(checkBoxes[2].isSelected());
            form.setHasExpiredIngredients(checkBoxes[3].isSelected());
            form.setNoForeignObjects(checkBoxes[4].isSelected());
            form.setSafeTemperature(checkBoxes[5].isSelected());
            form.setNormalColor(checkBoxes[6].isSelected());
            form.setGoodTexture(checkBoxes[7].isSelected());
            form.setSafeToEat(checkBoxes[8].isSelected());
            form.setPresentable(checkBoxes[9].isSelected());

            if (form.isPassed()) {
                errorBox.setVisible(false);

                // --- CARA MAP KE API ---
                // Menembak endpoint lokal Spring Boot via REST API
                try {
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

                    // 1. Create Dish
                    // Escape characters for valid JSON
                    String safeImagePath = imagePath.replace("\\", "\\\\").replace("\"", "\\\"");
                    String safeDishName = dishName.replace("\\", "\\\\").replace("\"", "\\\"");

                    // Convert "24" to "PT24H" for Duration
                    String dishJson = "{\"name\":\"" + safeDishName + "\", \"imagePath\":\"" + safeImagePath
                            + "\", \"expiresIn\":\"PT" + expiresIn.trim() + "H\"}";
                    java.net.http.HttpRequest dishReq = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8080/api/dishes"))
                            .header("Content-Type", "application/json")
                            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(dishJson))
                            .build();
                    java.net.http.HttpResponse<String> dishRes = client.send(dishReq,
                            java.net.http.HttpResponse.BodyHandlers.ofString());
                    if (dishRes.statusCode() >= 400)
                        throw new RuntimeException("Gagal buat Dish: " + dishRes.body());

                    // Extract dishId from the newly created Dish
                    String body = dishRes.body();
                    String dishIdStr = "";
                    int idx = body.indexOf("\"dishId\":\"");
                    if (idx != -1) {
                        dishIdStr = body.substring(idx + 10, body.indexOf("\"", idx + 10));
                    }
                    form.setDishId(java.util.UUID.fromString(dishIdStr));

                    // 2. Create Donation
                    // Convert "2026-05-12 18:00" to "2026-05-12T18:00"
                    String timeCookedFormatted = timeCooked.trim().replace(" ", "T");
                    if (timeCookedFormatted.length() == 16) {
                        timeCookedFormatted += ":00";
                    }
                    String timeAddedFormatted = java.time.LocalDateTime.now().toString();
                    if (timeAddedFormatted.length() > 19) {
                        timeAddedFormatted = timeAddedFormatted.substring(0, 19);
                    }
                    String donationJson = "{"
                            + "\"dish\":{\"dishId\":\"" + dishIdStr + "\"},"
                            + "\"timeCooked\":\"" + timeCookedFormatted + "\","
                            + "\"timeAdded\":\"" + timeAddedFormatted + "\","
                            + "\"status\":\"Pending QC\""
                            + "}";
                    java.net.http.HttpRequest donReq = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8080/api/donations"))
                            .header("Content-Type", "application/json")
                            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(donationJson))
                            .build();
                    java.net.http.HttpResponse<String> donRes = client.send(donReq,
                            java.net.http.HttpResponse.BodyHandlers.ofString());
                    if (donRes.statusCode() >= 400)
                        throw new RuntimeException("Gagal buat Donation: " + donRes.body());

                    // 3. Submit QC Form
                    String qcJson = "{"
                            + "\"dishId\":\"" + dishIdStr + "\","
                            + "\"freshScent\":" + form.isFreshScent() + ","
                            + "\"noSpoilage\":" + form.isNoSpoilage() + ","
                            + "\"properlyCooked\":" + form.isProperlyCooked() + ","
                            + "\"hasExpiredIngredients\":" + form.isHasExpiredIngredients() + ","
                            + "\"noForeignObjects\":" + form.isNoForeignObjects() + ","
                            + "\"safeTemperature\":" + form.isSafeTemperature() + ","
                            + "\"normalColor\":" + form.isNormalColor() + ","
                            + "\"goodTexture\":" + form.isGoodTexture() + ","
                            + "\"safeToEat\":" + form.isSafeToEat() + ","
                            + "\"presentable\":" + form.isPresentable()
                            + "}";
                    java.net.http.HttpRequest qcReq = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8080/api/dishes/submit-qc-form"))
                            .header("Content-Type", "application/json")
                            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(qcJson))
                            .build();
                    java.net.http.HttpResponse<String> qcRes = client.send(qcReq,
                            java.net.http.HttpResponse.BodyHandlers.ofString());
                    if (qcRes.statusCode() >= 400)
                        throw new RuntimeException("Gagal submit QC: " + qcRes.body());

                    System.out.println("Memproses Donasi: " + dishName + " | Waktu: " + timeCooked);
                    success("Donasi berhasil disubmit ke server! (QC Lulus)");
                    stage.close();
                } catch (Exception ex) {
                    error("Gagal terhubung ke API: " + ex.toString());
                    ex.printStackTrace();
                }

            } else {
                errorBox.setVisible(true);
            }
        });

        rightCol.getChildren().addAll(card2, submitBtn);

        columns.getChildren().addAll(leftCol, rightCol);
        root.getChildren().addAll(header, columns);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        // Swap root instead of setting new Scene to maintain Fullscreen state
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(scroll);
            stage.setScene(scene);
            stage.setMaximized(true);
        } else {
            scene.setRoot(scroll);
        }

        // Animations
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(600),
                root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(600), root);
        tt.setFromX(50);
        tt.setToX(0);
        tt.play();
    }

    private VBox createCard(String title) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 8px; -fx-background-color: white; -fx-background-radius: 8px;");
        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.web(DARK_GREEN));
        card.getChildren().add(lblTitle);
        return card;
    }

    private TextField styleTextField(TextField tf) {
        tf.setStyle("-fx-background-radius: 6px; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 6px; -fx-padding: 12px; -fx-font-size: 14px;");
        return tf;
    }

    public void displayStatus(String msg) {
        System.out.println("[STATUS] " + msg);
    }

    public void success(String msg) {
        System.out.println("[SUCCESS] " + msg);
    }

    public void error(String msg) {
        System.out.println("[ERROR] " + msg);
    }
}
