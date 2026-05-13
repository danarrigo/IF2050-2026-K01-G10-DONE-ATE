package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VerificationUI extends UI {

    private TextField[] pinFields = new TextField[6];
    private HBox successBox;
    private Button btnKonfirmasi;
    private static boolean jfxInitialized = false;
    private Stage stage;

    public VerificationUI(User user) {
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
        Platform.runLater(() -> start(new Stage()));
    }

    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("DONE-ATE - Verifikasi");
        Scene scene = new Scene(createContent(stage), 1920, 1080);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    public Parent createContent(Stage stage) {
        this.stage = stage;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9FAFB;");

        // --- TOP HEADER ---
        HBox header = new HBox();
        header.setStyle("-fx-background-color: white; -fx-border-color: #60A5FA; -fx-border-width: 0 0 4 0;");
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label logoLabel = new Label("DONE-ATE");
        logoLabel.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 20px;");
        header.getChildren().add(logoLabel);
        root.setTop(header);

        // --- MAIN CONTENT CONTAINER ---
        HBox mainContent = new HBox(25);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(30));

        // --- LEFT COLUMN (Detail Serah Terima) ---
        VBox leftCol = new VBox(10);
        leftCol.setPrefWidth(350);
        leftCol.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 10; -fx-background-radius: 10;");
        leftCol.setPadding(new Insets(20));

        Label leftTitle = new Label("Detail Serah Terima");
        leftTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Image Placeholder
        VBox imageBox = new VBox();
        imageBox.setPrefHeight(150);
        imageBox.setStyle("-fx-background-color: #0F766E; -fx-background-radius: 8;");

        // Tags
        HBox tagsBox = new HBox(10);
        Label tag1 = new Label("Perishable");
        tag1.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-padding: 3 8 3 8; -fx-background-radius: 15; -fx-font-size: 11px;");
        Label tag2 = new Label("Donasi #TRX-9821");
        tag2.setStyle("-fx-background-color: #BBF7D0; -fx-text-fill: #166534; -fx-padding: 3 8 3 8; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
        tagsBox.getChildren().addAll(tag1, tag2);

        Label foodName = new Label("Paket Bahan Pangan Segar");
        foodName.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Label donatorName = new Label("Donatur: Restoran Hijau Sejahtera");
        donatorName.setStyle("-fx-text-fill: #6B7280;");

        VBox locationTimeBox = new VBox(5);
        locationTimeBox.setPadding(new Insets(10, 0, 0, 0));
        Label locLabel = new Label("📍 Jl. Melati No. 45, Jakarta Pusat");
        locLabel.setStyle("-fx-text-fill: #4B5563;");
        Label timeLabel = new Label("🕒 Hari ini, 14:00 - 16:00 WIB");
        timeLabel.setStyle("-fx-text-fill: #4B5563;");
        locationTimeBox.getChildren().addAll(locLabel, timeLabel);

        leftCol.getChildren().addAll(leftTitle, imageBox, tagsBox, foodName, donatorName, locationTimeBox);

        // --- RIGHT COLUMN ---
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(450);

        // 1. Verifikasi Card
        VBox verifikasiCard = new VBox(15);
        verifikasiCard.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 10; -fx-background-radius: 10;");
        verifikasiCard.setPadding(new Insets(25));

        Label rightTitle = new Label("Verifikasi Serah Terima");
        rightTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #166534; -fx-font-size: 22px;");
        
        Text descText = new Text("Input Kode Verifikasi yang diterima oleh pihak Donatur atau Penerima untuk menyelesaikan proses.");
        descText.setFill(javafx.scene.paint.Color.web("#6b7280"));
        descText.setWrappingWidth(400);

        Label pinLabel = new Label("Masukkan 6 Digit Kode");
        pinLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");

        // PIN Inputs
        HBox pinBox = new HBox(10);
        pinBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 6; i++) {
            pinFields[i] = new TextField();
            pinFields[i].setPrefSize(50, 50);
            pinFields[i].setAlignment(Pos.CENTER);
            pinFields[i].setStyle("-fx-font-size: 20px; -fx-border-radius: 5;");
            pinBox.getChildren().add(pinFields[i]);
        }
        setupPinLogic(); // Attach listeners

        // Success Box (Hidden initially)
        successBox = new HBox(10);
        successBox.setStyle("-fx-background-color: #DCFCE7; -fx-border-color: #BBF7D0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10;");
        successBox.setAlignment(Pos.CENTER_LEFT);
        Label successLabel = new Label("✅ Kode lengkap. Tekan Konfirmasi untuk verifikasi.");
        successLabel.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        successBox.getChildren().add(successLabel);
        successBox.setVisible(false);
        successBox.setManaged(false);

        // Submit Button
        btnKonfirmasi = new Button("Konfirmasi Terima");
        btnKonfirmasi.setMaxWidth(Double.MAX_VALUE);
        btnKonfirmasi.setStyle("-fx-background-color: #14532D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12;");
        btnKonfirmasi.setOnAction(e -> handleKonfirmasi());

        HBox resendBox = new HBox();
        resendBox.setAlignment(Pos.CENTER);
        Label resendLabel = new Label("Kirim Ulang Kode (59s)");
        resendLabel.setStyle("-fx-text-fill: #6B7280;");
        resendBox.getChildren().add(resendLabel);

        verifikasiCard.getChildren().addAll(rightTitle, descText, pinLabel, pinBox, successBox, btnKonfirmasi, resendBox);

        // 2. Panduan Keamanan Card
        VBox panduanCard = new VBox(10);
        panduanCard.setStyle("-fx-background-color: #F3F4F6; -fx-border-color: #166534; -fx-border-width: 0 0 0 4; -fx-background-radius: 0 8 8 0;");
        panduanCard.setPadding(new Insets(15, 20, 15, 20));
        
        Label panduanTitle = new Label("Panduan Keamanan");
        panduanTitle.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        
        Label p1 = new Label("• Pastikan kondisi barang sesuai dengan deskripsi sebelum konfirmasi.");
        p1.setWrapText(true);
        p1.setStyle("-fx-text-fill: #4B5563;");
        
        Label p2 = new Label("• Jangan membagikan kode verifikasi kepada siapapun selain sistem.");
        p2.setWrapText(true);
        p2.setStyle("-fx-text-fill: #4B5563;");
        
        panduanCard.getChildren().addAll(panduanTitle, p1, p2);

        rightCol.getChildren().addAll(verifikasiCard, panduanCard);

        // Add both columns to main content
        mainContent.getChildren().addAll(leftCol, rightCol);
        root.setCenter(mainContent);
        
        return root;
    }

    private void setupPinLogic() {
        for (int i = 0; i < 6; i++) {
            final int index = i;
            TextField currentField = pinFields[i];
            
            currentField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1) {
                    currentField.setText(newVal.substring(0, 1)); // Limit to 1 character
                }
                // Auto advance focus to next box
                if (newVal.length() == 1 && index < 5) {
                    pinFields[index + 1].requestFocus();
                }
                checkIfPinComplete();
            });
        }
    }

    private void checkIfPinComplete() {
        boolean isComplete = true;
        for (TextField field : pinFields) {
            if (field.getText().isEmpty()) {
                isComplete = false;
                break;
            }
        }
        successBox.setVisible(isComplete);
        successBox.setManaged(isComplete);
    }

    private void handleKonfirmasi() {
        StringBuilder fullPin = new StringBuilder();
        for (TextField field : pinFields) {
            fullPin.append(field.getText());
        }

        if (fullPin.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Error", "Silakan lengkapi 6 digit kode verifikasi.");
            return;
        }

        try {
            int transactionCode = Integer.parseInt(fullPin.toString());
            
            // Disable button to prevent multiple clicks
            btnKonfirmasi.setDisable(true);
            btnKonfirmasi.setText("Memverifikasi...");
            
            String token = SessionManager.getInstance().getToken();
            HttpClient client = HttpClient.newHttpClient();
            String jsonPayload = "{\"inputCode\":" + transactionCode + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/claims/verify"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        btnKonfirmasi.setDisable(false);
                        btnKonfirmasi.setText("Konfirmasi Terima");
                        
                        if (response.statusCode() == 200) {
                            // Show Success UI
                            VerificationSuccessUI successUI = new VerificationSuccessUI(getUser());
                            stage.getScene().setRoot(successUI.createContent(stage));
                            stage.setFullScreen(true);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", response.body()); 
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        btnKonfirmasi.setDisable(false);
                        btnKonfirmasi.setText("Konfirmasi Terima");
                        showAlert(Alert.AlertType.ERROR, "Error System", "Gagal menghubungi server: " + ex.getMessage());
                    });
                    return null;
                });
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Kode verifikasi harus berupa angka.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        new VerificationUI(null).showUI();
    }
}
