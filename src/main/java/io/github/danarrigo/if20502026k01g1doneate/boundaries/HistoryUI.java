package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class HistoryUI extends UI {

    private Stage stage;

    // Design Tokens Figma
    private final String DARK_GREEN = "#3B7D3B"; // Disesuaikan dengan warna Figma
    private final String LIGHT_GREEN = "#E8F0E8";
    private final String BADGE_GREEN = "#E8F5E9";
    private final String BADGE_RED = "#FFEBEE";
    private final String BADGE_GRAY = "#F5F5F5";
    private final String TEXT_GRAY = "#757575";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BG_COLOR = "#FAFAFA"; // Lebih putih sesuai desain

    public HistoryUI(User user) {
        super(user);
    }

    public static void main(String[] args) {
        Donator mockDonator = new Donator();
        // Menggunakan UUID sesuai keputusanmu
        mockDonator.setDonatorId(UUID.randomUUID());
        
        HistoryUI ui = new HistoryUI(mockDonator);
        ui.showUI();
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(this::createAndShowStage);
    }

    private void createAndShowStage() {
        stage = new Stage();
        stage.setTitle("DONE-ATE - Riwayat Donasi");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- 1. TOP BAR ---
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: white;");

        Label logoLabel = new Label("🍴 DONE-ATE"); // Menggunakan emoji sebagai ikon sementara
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        logoLabel.setTextFill(Color.web(DARK_GREEN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Circle profileCircle = new Circle(14, Color.web("#4A4A4A"));
        topBar.getChildren().addAll(logoLabel, spacer, profileCircle);

        // --- 2. CONTENT AREA ---
        VBox scrollContent = new VBox(20);
        scrollContent.setPadding(new Insets(20));

        // Header: Title, Subtitle, dan Tombol Unduh
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(2);
        Label titleLbl = new Label("Riwayat Donasi");
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label subTitleLbl = new Label("Lacak kontribusi dan dampak sosial Anda.");
        subTitleLbl.setFont(Font.font(12));
        subTitleLbl.setTextFill(Color.web(TEXT_GRAY));
        titleBox.getChildren().addAll(titleLbl, subTitleLbl);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button downloadBtn = new Button("📄 Unduh Laporan");
        downloadBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        downloadBtn.setOnAction(e -> handleDownloadReport()); // Panggil API
        
        headerBox.getChildren().addAll(titleBox, headerSpacer, downloadBtn);

        // Stats & Filter Section
        HBox statsFilterBox = new HBox(10);
        statsFilterBox.setAlignment(Pos.CENTER);

        // Kiri: Date Filter Card
        VBox filterCard = new VBox(5);
        filterCard.setPadding(new Insets(15));
        filterCard.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        HBox.setHgrow(filterCard, Priority.ALWAYS);

        HBox dateInputs = new HBox(10);
        dateInputs.setAlignment(Pos.CENTER);
        
        VBox startBox = new VBox(2);
        Label startLbl = new Label("Tanggal Mulai");
        startLbl.setFont(Font.font(9));
        startLbl.setTextFill(Color.web(TEXT_GRAY));
        TextField dp1 = new TextField("mm/dd/yyyy"); // Pakai TextField meniru mockup
        dp1.setPrefWidth(90);
        startBox.getChildren().addAll(startLbl, dp1);

        Label arrowLbl = new Label("→");
        arrowLbl.setTextFill(Color.web(TEXT_GRAY));

        VBox endBox = new VBox(2);
        Label endLbl = new Label("Tanggal Selesai");
        endLbl.setFont(Font.font(9));
        endLbl.setTextFill(Color.web(TEXT_GRAY));
        TextField dp2 = new TextField("mm/dd/yyyy");
        dp2.setPrefWidth(90);
        endBox.getChildren().addAll(endLbl, dp2);

        dateInputs.getChildren().addAll(startBox, arrowLbl, endBox);
        filterCard.getChildren().add(dateInputs);

        // Kanan: Total Donasi Card
        VBox statsCard = new VBox(5);
        statsCard.setPadding(new Insets(15));
        statsCard.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 8px;");
        
        Label statsTitle = new Label("Total Donasi Selesai");
        statsTitle.setFont(Font.font(10));
        statsTitle.setTextFill(Color.web(TEXT_GRAY));
        
        HBox statsValueBox = new HBox(3);
        statsValueBox.setAlignment(Pos.BOTTOM_LEFT);
        Label statsNum = new Label("124");
        statsNum.setFont(Font.font("System", FontWeight.BOLD, 24));
        statsNum.setTextFill(Color.web(DARK_GREEN));
        Label statsUnit = new Label("Paket");
        statsUnit.setFont(Font.font(10));
        statsUnit.setTextFill(Color.web(TEXT_GRAY));
        statsUnit.setPadding(new Insets(0,0,5,0));
        statsValueBox.getChildren().addAll(statsNum, statsUnit);

        Rectangle progressBar = new Rectangle(80, 4, Color.web(DARK_GREEN));
        progressBar.setArcWidth(4);
        progressBar.setArcHeight(4);

        statsCard.getChildren().addAll(statsTitle, statsValueBox, progressBar);
        statsFilterBox.getChildren().addAll(filterCard, statsCard);

        // History List
        VBox historySection = new VBox(10);
        historySection.getChildren().addAll(
            createFigmaHistoryItem("⊗", "Roti Artisan Sisa Hari", "Donasi 24 Pcs Pastry Mix • 08 Okt 2023", "Dibatalkan", BADGE_RED, "#D32F2F", "TX-99185", "Lihat Alasan"),
            createFigmaHistoryItem("⊘", "Sayuran Segar Organik", "Donasi 5kg Sayur Campur • 05 Okt 2023", "Kadaluwarsa", BADGE_GRAY, TEXT_GRAY, "TX-99042", "Donasi Ulang"),
            createFigmaHistoryItem("✓", "Lauk Pauk Siap Saji", "Donasi 20 Porsi Ayam Penyet • 02 Okt 2023", "Selesai", BADGE_GREEN, DARK_GREEN, "TX-98912", "Lihat Detail")
        );

        // Tombol Muat Lebih Banyak
        Button loadMoreBtn = new Button("Muat Lebih Banyak");
        loadMoreBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + DARK_GREEN + "; -fx-border-radius: 20px; -fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold;");
        loadMoreBtn.setPrefWidth(150);
        HBox loadMoreBox = new HBox(loadMoreBtn);
        loadMoreBox.setAlignment(Pos.CENTER);
        loadMoreBox.setPadding(new Insets(10, 0, 0, 0));

        scrollContent.getChildren().addAll(headerBox, statsFilterBox, historySection, loadMoreBox);

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");

        HBox bottomNav = Navigator.createBottomNav(stage, getUser(), "HISTORY");

        root.getChildren().addAll(topBar, scrollPane, bottomNav);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 400, 800);
        stage.setScene(scene);
        stage.show();
    }

    // Pembuatan Card Item persis Figma
    private HBox createFigmaHistoryItem(String iconTxt, String title, String subtitle, String status, String badgeBg, String badgeTextCol, String txId, String actionTxt) {
        HBox item = new HBox(12);
        item.setPadding(new Insets(15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        // Ikon Kiri
        StackPane iconPane = new StackPane();
        Circle bgCircle = new Circle(18, Color.web(badgeBg));
        Label icon = new Label(iconTxt);
        icon.setTextFill(Color.web(badgeTextCol));
        icon.setFont(Font.font("System", FontWeight.BOLD, 14));
        iconPane.getChildren().addAll(bgCircle, icon);

        // Teks Tengah
        VBox textPane = new VBox(3);
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.NORMAL, 13));
        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font(10));
        subLbl.setTextFill(Color.web(TEXT_GRAY));
        textPane.getChildren().addAll(titleLbl, subLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status & Aksi Kanan
        VBox rightPane = new VBox(5);
        rightPane.setAlignment(Pos.CENTER_RIGHT);
        
        Label statusBadge = new Label(status);
        statusBadge.setFont(Font.font(9));
        statusBadge.setPadding(new Insets(2, 8, 2, 8));
        statusBadge.setStyle("-fx-background-color: " + badgeBg + "; -fx-background-radius: 10px;");
        statusBadge.setTextFill(Color.web(badgeTextCol));

        VBox idActionBox = new VBox(0);
        idActionBox.setAlignment(Pos.CENTER_RIGHT);
        Label idLbl = new Label("ID: " + txId);
        idLbl.setFont(Font.font("System", FontWeight.BOLD, 9));
        Label actionLbl = new Label(actionTxt);
        actionLbl.setFont(Font.font(9));
        actionLbl.setTextFill(Color.web(DARK_GREEN));
        idActionBox.getChildren().addAll(idLbl, actionLbl);

        rightPane.getChildren().addAll(statusBadge, idActionBox);

        item.getChildren().addAll(iconPane, textPane, spacer, rightPane);
        return item;
    }


    // Fungsi Panggil API menggunakan UUID
    private void handleDownloadReport() {
        if (getUser() == null || !(getUser() instanceof Donator)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Anda harus login sebagai Donatur.");
            return;
        }

        // Ekstraksi UUID
        UUID donatorId = ((Donator) getUser()).getDonatorId();

        // Convert UUID ke String untuk URL
        String url = "http://localhost:8080/api/reports/download/" + donatorId.toString();

        String token = SessionManager.getInstance().getToken();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            // Show file chooser to save PDF
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Simpan Laporan PDF");
                            fileChooser.setInitialFileName("Laporan_Donasi_" + donatorId + ".pdf");
                            fileChooser.getExtensionFilters().add(
                                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                            );
                            
                            File file = fileChooser.showSaveDialog(stage);
                            if (file != null) {
                                try {
                                    Path path = file.toPath();
                                    Files.write(path, response.body());
                                    showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                                        "Laporan berhasil disimpan ke: " + file.getAbsolutePath());
                                } catch (Exception e) {
                                    showAlert(Alert.AlertType.ERROR, "Gagal", 
                                        "Gagal menyimpan file: " + e.getMessage());
                                }
                            }
                        } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                            showAlert(Alert.AlertType.ERROR, "Gagal", "Sesi berakhir atau tidak memiliki izin.");
                        } else if (response.statusCode() == 404) {
                            showAlert(Alert.AlertType.ERROR, "Gagal", "Donatur tidak ditemukan.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengunduh laporan.");
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Koneksi ke server gagal."));
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