package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryUI extends UI {

    private final String DARK_GREEN   = "#0F5B21";
    private final String LIGHT_GREEN  = "#D2F4D6";
    private final String TEXT_GRAY    = "#757575";
    private final String BORDER_COLOR = "#E0E0E0";
    private final String BADGE_RED    = "#FEE2E2";
    private final String BADGE_GREEN  = "#D1FAE5";
    private final String BADGE_GRAY   = "#F3F4F6";
    private final String BG_COLOR     = "#F8FAFC";
    private final String BASE_URL     = "http://localhost:8080";

    private VBox historyContainer;
    private Label statsNum;
    private Stage stage;
    private TextField dp1;
    private TextField dp2;

    public HistoryUI(User user) {
        super(user);
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        this.stage = stage;
        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- 1. TOP BAR ---
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: white;");

        Label logoLabel = new Label("🍴 DONE-ATE"); 
        logoLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        logoLabel.setTextFill(Color.web(DARK_GREEN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Circle profileCircle = new Circle(14, Color.web("#4A4A4A"));
        topBar.getChildren().addAll(logoLabel, spacer, profileCircle);

        // --- 2. CONTENT AREA ---
        VBox scrollContent = new VBox(20);
        scrollContent.setPadding(new Insets(20));

        // Header
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
        downloadBtn.setOnAction(e -> handleDownloadReport()); 
        
        headerBox.getChildren().addAll(titleBox, headerSpacer, downloadBtn);

        // Stats & Filter Section
        HBox statsFilterBox = new HBox(10);
        statsFilterBox.setAlignment(Pos.CENTER);

        VBox filterCard = new VBox(10);
        filterCard.setPadding(new Insets(15));
        filterCard.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");
        HBox.setHgrow(filterCard, Priority.ALWAYS);

        HBox dateInputs = new HBox(10);
        dateInputs.setAlignment(Pos.CENTER_LEFT);
        
        VBox startBox = new VBox(2);
        Label startLbl = new Label("Tanggal Mulai");
        startLbl.setFont(Font.font(9));
        startLbl.setTextFill(Color.web(TEXT_GRAY));
        dp1 = new TextField(""); 
        dp1.setPromptText("yyyy-MM-dd");
        dp1.setPrefWidth(110);
        startBox.getChildren().addAll(startLbl, dp1);

        Label arrowLbl = new Label("→");
        arrowLbl.setTextFill(Color.web(TEXT_GRAY));

        VBox endBox = new VBox(2);
        Label endLbl = new Label("Tanggal Selesai");
        endLbl.setFont(Font.font(9));
        endLbl.setTextFill(Color.web(TEXT_GRAY));
        dp2 = new TextField("");
        dp2.setPromptText("yyyy-MM-dd");
        dp2.setPrefWidth(110);
        endBox.getChildren().addAll(endLbl, dp2);

        // Quick Action Buttons
        Button weekBtn = new Button("1 Minggu Lalu");
        styleQuickBtn(weekBtn);
        weekBtn.setOnAction(e -> {
            dp1.setText(LocalDate.now().minusWeeks(1).toString());
        });

        Button todayBtn = new Button("Hari Ini");
        styleQuickBtn(todayBtn);
        todayBtn.setOnAction(e -> {
            dp2.setText(LocalDate.now().toString());
        });

        dateInputs.getChildren().addAll(startBox, arrowLbl, endBox, new Region(), weekBtn, todayBtn);
        filterCard.getChildren().add(dateInputs);

        VBox statsCard = new VBox(5);
        statsCard.setPadding(new Insets(15));
        statsCard.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 8px;");
        
        Label statsTitle = new Label("Total Donasi Selesai");
        statsTitle.setFont(Font.font(10));
        statsTitle.setTextFill(Color.web(TEXT_GRAY));
        
        HBox statsValueBox = new HBox(3);
        statsValueBox.setAlignment(Pos.BOTTOM_LEFT);
        statsNum = new Label("0");
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

        // History Container
        historyContainer = new VBox(10);
        
        scrollContent.getChildren().addAll(headerBox, statsFilterBox, historyContainer);

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");

        HBox bottomNav = Navigator.createBottomNav(stage, getUser(), "HISTORY");

        root.getChildren().addAll(topBar, scrollPane, bottomNav);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        fetchHistory();
        
        return root;
    }

    private void styleQuickBtn(Button btn) {
        btn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 15px; -fx-cursor: hand;");
    }

    private void fetchHistory() {
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                String url = BASE_URL + "/api/catalog/donator/" + getUser().getUsername();
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    renderHistory(response.body());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderHistory(String json) {
        Platform.runLater(() -> {
            try {
                historyContainer.getChildren().clear();
                
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> items = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
                
                if (items.isEmpty()) {
                    historyContainer.getChildren().add(new Label("Belum ada riwayat donasi."));
                    return;
                }

                int countSelesai = 0;
                for (Map<String, Object> item : items) {
                    String dishName = String.valueOf(item.get("dishName"));
                    String status   = String.valueOf(item.get("status"));
                    String time     = String.valueOf(item.get("timeAdded"));
                    String dateStr  = (time != null && time.length() >= 10) ? time.substring(0, 10) : "-";

                    String icon = "✓";
                    String badgeBg = BADGE_GREEN;
                    String textCol = DARK_GREEN;

                    if ("Selesai".equalsIgnoreCase(status) || "QC Passed".equalsIgnoreCase(status)) {
                        countSelesai++;
                    } else if ("Dibatalkan".equalsIgnoreCase(status) || "QC Failed".equalsIgnoreCase(status) || "Removed".equalsIgnoreCase(status)) {
                        icon = "⊗";
                        badgeBg = BADGE_RED;
                        textCol = "#D32F2F";
                    } else {
                        icon = "⌛";
                        badgeBg = BADGE_GRAY;
                        textCol = TEXT_GRAY;
                    }

                    historyContainer.getChildren().add(
                        createFigmaHistoryItem(icon, dishName, "Donasi pada " + dateStr, status, badgeBg, textCol)
                    );
                }
                statsNum.setText(String.valueOf(countSelesai));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private HBox createFigmaHistoryItem(String iconTxt, String title, String subtitle, String status, String badgeBg, String badgeTextCol) {
        HBox item = new HBox(12);
        item.setPadding(new Insets(15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px;");

        StackPane iconPane = new StackPane();
        Circle bgCircle = new Circle(18, Color.web(badgeBg));
        Label icon = new Label(iconTxt);
        icon.setTextFill(Color.web(badgeTextCol));
        icon.setFont(Font.font("System", FontWeight.BOLD, 14));
        iconPane.getChildren().addAll(bgCircle, icon);

        VBox textPane = new VBox(3);
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.NORMAL, 14));
        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font(11));
        subLbl.setTextFill(Color.web(TEXT_GRAY));
        textPane.getChildren().addAll(titleLbl, subLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(status);
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 10));
        statusBadge.setPadding(new Insets(4, 10, 4, 10));
        statusBadge.setStyle("-fx-background-color: " + badgeBg + "; -fx-background-radius: 12px; -fx-text-fill: " + badgeTextCol + ";");

        item.getChildren().addAll(iconPane, textPane, spacer, statusBadge);
        return item;
    }

    private void handleDownloadReport() {
        if (getUser() == null) return;
        
        String username = getUser().getUsername();
        String url = BASE_URL + "/api/reports/download/user/" + username;
        String token = SessionManager.getInstance().getToken();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Donasi");
        fileChooser.setInitialFileName("Laporan_Donasi_" + username + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            new Thread(() -> {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Authorization", "Bearer " + token)
                            .GET()
                            .build();

                    HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                    if (response.statusCode() == 200) {
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(response.body());
                        }
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Sukses");
                            alert.setHeaderText("Laporan Berhasil Diunduh");
                            alert.setContentText("File disimpan di: " + file.getAbsolutePath());
                            alert.show();
                        });
                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setContentText("Gagal mengunduh laporan dari server. (Status: " + response.statusCode() + ")");
                            alert.show();
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("DONE-ATE - Riwayat Donasi");
            Scene scene = new Scene(getSceneContent(stage), 1920, 1080);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }
}