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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class VerificationUI extends UI {

    private TextField[] pinFields = new TextField[6];
    private HBox successBox;
    private Button btnKonfirmasi;
    private Stage stage;
    private UUID donationId;

    // UI elements to update dynamically
    private Label foodNameLabel;
    private Label donatorNameLabel;
    private Label locationLabel;
    private Label timeLabel;
    private Label trxTagLabel;
    private ImageView foodImageView;

    public VerificationUI(User user) {
        super(user);
    }

    public VerificationUI(User user, UUID donationId) {
        super(user);
        this.donationId = donationId;
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        return createContent(stage);
    }

    @Override
    public void showUI() {
        initJFX();
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
        HBox header = new HBox(15);
        header.setStyle("-fx-background-color: white; -fx-border-color: #60A5FA; -fx-border-width: 0 0 4 0;");
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label backArrow = new Label("←");
        backArrow.setFont(Font.font("System", FontWeight.BOLD, 22));
        backArrow.setStyle("-fx-text-fill: #16A34A; -fx-cursor: hand;");
        backArrow.setOnMouseClicked(e -> {
            Navigator.navigate(stage, new RecipientCatalogUI(getUser()));
        });

        Label logoLabel = new Label("DONE-ATE");
        logoLabel.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 20px;");
        header.getChildren().addAll(backArrow, logoLabel);
        root.setTop(header);

        // --- MAIN CONTENT CONTAINER ---
        HBox mainContent = new HBox(25);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(30));

        // --- LEFT COLUMN (Detail Serah Terima) ---
        VBox leftCol = new VBox(15);
        leftCol.setPrefWidth(400);
        leftCol.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 10; -fx-background-radius: 10;");
        leftCol.setPadding(new Insets(25));

        Label leftTitle = new Label("Detail Serah Terima");
        leftTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Image Container
        foodImageView = new ImageView();
        foodImageView.setFitWidth(350);
        foodImageView.setFitHeight(200);
        foodImageView.setPreserveRatio(true);
        
        StackPane imageWrapper = new StackPane(foodImageView);
        imageWrapper.setPrefSize(350, 200);
        imageWrapper.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 8;");

        // Tags
        HBox tagsBox = new HBox(10);
        Label tag1 = new Label("Perishable");
        tag1.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-padding: 3 8 3 8; -fx-background-radius: 15; -fx-font-size: 11px;");
        
        trxTagLabel = new Label("Donasi #" + (donationId != null ? donationId.toString().substring(0, 8).toUpperCase() : "TRX-9821"));
        trxTagLabel.setStyle("-fx-background-color: #BBF7D0; -fx-text-fill: #166534; -fx-padding: 3 8 3 8; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
        tagsBox.getChildren().addAll(tag1, trxTagLabel);

        foodNameLabel = new Label("Memuat data...");
        foodNameLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        foodNameLabel.setWrapText(true);
        
        donatorNameLabel = new Label("Donatur: -");
        donatorNameLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px;");

        VBox locationTimeBox = new VBox(8);
        locationTimeBox.setPadding(new Insets(10, 0, 0, 0));
        locationLabel = new Label("📍 Lokasi penjemputan...");
        locationLabel.setStyle("-fx-text-fill: #4B5563;");
        locationLabel.setWrapText(true);
        
        timeLabel = new Label("🕒 Waktu...");
        timeLabel.setStyle("-fx-text-fill: #4B5563;");
        locationTimeBox.getChildren().addAll(locationLabel, timeLabel);

        leftCol.getChildren().addAll(leftTitle, imageWrapper, tagsBox, foodNameLabel, donatorNameLabel, locationTimeBox);

        // --- RIGHT COLUMN ---
        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(500);

        // 1. Verifikasi Card
        VBox verifikasiCard = new VBox(20);
        verifikasiCard.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 10; -fx-background-radius: 10;");
        verifikasiCard.setPadding(new Insets(30));

        Label rightTitle = new Label("Verifikasi Serah Terima");
        rightTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #166534; -fx-font-size: 24px;");
        
        Text descText = new Text("Silakan masukkan 6 digit kode verifikasi yang diberikan oleh Donatur saat serah terima makanan dilakukan.");
        descText.setFill(javafx.scene.paint.Color.web("#6B7280"));
        descText.setWrappingWidth(440);
        descText.setFont(Font.font(14));

        Label pinLabel = new Label("KODE VERIFIKASI");
        pinLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 12px;");

        // PIN Inputs
        HBox pinBox = new HBox(12);
        pinBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 6; i++) {
            pinFields[i] = new TextField();
            pinFields[i].setPrefSize(55, 60);
            pinFields[i].setAlignment(Pos.CENTER);
            pinFields[i].setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-background-radius: 8;");
            pinBox.getChildren().add(pinFields[i]);
        }
        setupPinLogic();

        // Success Box
        successBox = new HBox(10);
        successBox.setStyle("-fx-background-color: #DCFCE7; -fx-border-color: #BBF7D0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
        successBox.setAlignment(Pos.CENTER_LEFT);
        Label successLabel = new Label("✅ Kode lengkap. Silakan tekan konfirmasi.");
        successLabel.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        successBox.getChildren().add(successLabel);
        successBox.setVisible(false);
        successBox.setManaged(false);

        // Submit Button
        btnKonfirmasi = new Button("Konfirmasi Terima Donasi");
        btnKonfirmasi.setMaxWidth(Double.MAX_VALUE);
        btnKonfirmasi.setCursor(javafx.scene.Cursor.HAND);
        btnKonfirmasi.setStyle("-fx-background-color: #166534; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15; -fx-background-radius: 8;");
        btnKonfirmasi.setOnAction(e -> handleKonfirmasi());

        verifikasiCard.getChildren().addAll(rightTitle, descText, pinLabel, pinBox, successBox, btnKonfirmasi);

        // 2. Panduan Card
        VBox panduanCard = new VBox(12);
        panduanCard.setStyle("-fx-background-color: #F0FDF4; -fx-border-color: #166534; -fx-border-width: 0 0 0 4; -fx-background-radius: 0 8 8 0;");
        panduanCard.setPadding(new Insets(20));
        
        Label panduanTitle = new Label("🛡 Panduan Keamanan");
        panduanTitle.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold; -fx-font-size: 15px;");
        
        Label p1 = new Label("1. Periksa kesesuaian dan kualitas makanan sebelum menginput kode.");
        p1.setWrapText(true);
        p1.setStyle("-fx-text-fill: #166534; -fx-font-size: 13px;");
        
        Label p2 = new Label("2. Kode verifikasi ini bersifat rahasia dan hanya digunakan untuk menyelesaikan transaksi.");
        p2.setWrapText(true);
        p2.setStyle("-fx-text-fill: #166534; -fx-font-size: 13px;");
        
        panduanCard.getChildren().addAll(panduanTitle, p1, p2);

        rightCol.getChildren().addAll(verifikasiCard, panduanCard);

        mainContent.getChildren().addAll(leftCol, rightCol);
        root.setCenter(mainContent);
        
        if (donationId != null) {
            fetchDonationData();
        }
        
        return root;
    }

    private void fetchDonationData() {
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/catalog/" + donationId))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(response.body());
                    
                    String name = node.get("dishName").asText();
                    String donator = node.get("donatorUsername").asText();
                    String address = node.get("donatorAddress").asText();
                    String imagePath = node.has("imagePath") ? node.get("imagePath").asText() : null;
                    
                    Platform.runLater(() -> {
                        foodNameLabel.setText(name);
                        donatorNameLabel.setText("Donatur: " + donator);
                        locationLabel.setText("📍 " + address);
                        trxTagLabel.setText("Donasi #" + donationId.toString().substring(0, 8).toUpperCase());
                        
                        if (imagePath != null && !imagePath.isEmpty()) {
                            try {
                                Image img = imagePath.startsWith("http") 
                                    ? new Image(imagePath, 350, 200, true, true, true)
                                    : new Image("file:" + imagePath, 350, 200, true, true, true);
                                if (!img.isError()) {
                                    foodImageView.setImage(img);
                                }
                            } catch (Exception e) {}
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupPinLogic() {
        for (int i = 0; i < 6; i++) {
            final int index = i;
            TextField currentField = pinFields[i];
            
            currentField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1) {
                    currentField.setText(newVal.substring(0, 1));
                }
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
                        btnKonfirmasi.setText("Konfirmasi Terima Donasi");
                        
                        if (response.statusCode() == 200) {
                            Navigator.navigate(stage, new VerificationSuccessUI(getUser()));
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Verifikasi Gagal", "Kode yang Anda masukkan salah atau sudah tidak aktif."); 
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        btnKonfirmasi.setDisable(false);
                        btnKonfirmasi.setText("Konfirmasi Terima Donasi");
                        showAlert(Alert.AlertType.ERROR, "Error Sistem", "Gagal menghubungi server.");
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
}
