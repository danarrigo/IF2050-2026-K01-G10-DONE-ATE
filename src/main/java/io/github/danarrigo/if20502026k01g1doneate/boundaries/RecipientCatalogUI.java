package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RecipientCatalogUI extends UI {

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String TEXT_GRAY    = "#666666";
    private static final String BORDER_COLOR = "#E8E8E8";
    private static final String BG_COLOR     = "#F5F5F5";
    private static final String BASE_URL     = "http://localhost:8080";

    private VBox catalogList;
    private final List<Map<String, String>> allItems = new ArrayList<>();
    private String activeFilter = "Semua";

    public RecipientCatalogUI(User user) {
        super(user);
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(this::createAndShowStage);
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        root.setTop(buildNavbar(stage));

        catalogList = new VBox(12);
        catalogList.setPadding(new Insets(0, 0, 24, 0));

        VBox centerContent = new VBox(0);
        centerContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        centerContent.getChildren().addAll(buildPageHeader(), buildFilterBar());

        VBox listWrapper = new VBox(catalogList);
        listWrapper.setPadding(new Insets(16, 24, 24, 24));
        listWrapper.setMaxWidth(960);
        listWrapper.setAlignment(Pos.TOP_CENTER);

        StackPane listPane = new StackPane(listWrapper);
        listPane.setStyle("-fx-background-color: " + BG_COLOR + ";");
        centerContent.getChildren().add(listPane);

        ScrollPane scroll = new ScrollPane(centerContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG_COLOR + "; -fx-background: " + BG_COLOR + ";");

        root.setCenter(scroll);
        root.setBottom(Navigator.createBottomNav(stage, getUser(), "HOME"));

        playAnimation(centerContent);
        loadCatalog();
        return root;
    }

    private void createAndShowStage() {
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Katalog Donasi");
        stage.setMaximized(true);
        Scene scene = new Scene(getSceneContent(stage));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    // ─── Top Navbar ────────────────────────────────────────────────────────────

    private HBox buildNavbar(Stage stage) {
        HBox navbar = new HBox();
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(14, 28, 14, 28));
        navbar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        // Logo
        Label icon = new Label("🍴");
        icon.setStyle("-fx-font-size: 20px;");
        Label appName = new Label("DONE-ATE");
        appName.setFont(Font.font("System", FontWeight.BOLD, 20));
        appName.setStyle("-fx-text-fill: " + DARK_GREEN + ";");
        HBox logo = new HBox(8, icon, appName);
        logo.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search button
        Button searchBtn = new Button("🔍");
        searchBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12 6 12;"
        );

        // Account avatar
        String initials = getAvatarInitials();
        Label avatarLabel = new Label(initials);
        avatarLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane avatarPane = new StackPane(avatarLabel);
        avatarPane.setPrefSize(38, 38);
        avatarPane.setMinSize(38, 38);
        avatarPane.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-background-radius: 19px;"
        );
        Button accountBtn = new Button();
        accountBtn.setGraphic(avatarPane);
        accountBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        accountBtn.setOnAction(e -> new AccountUI(getUser()).showUI());

        navbar.getChildren().addAll(logo, spacer, searchBtn, accountBtn);
        return navbar;
    }

    private String getAvatarInitials() {
        String u = SessionManager.getInstance().getUsername();
        if (u == null && getUser() != null) u = getUser().getUsername();
        if (u == null || u.isEmpty()) return "?";
        return u.length() >= 2 ? u.substring(0, 2).toUpperCase() : u.toUpperCase();
    }

    // ─── Page Header ───────────────────────────────────────────────────────────

    private VBox buildPageHeader() {
        VBox header = new VBox(6);
        header.setPadding(new Insets(32, 28, 16, 28));
        header.setStyle("-fx-background-color: white;");

        Label title = new Label("Katalog Donasi");
        title.setFont(Font.font("System", FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill: #111;");

        Label subtitle = new Label("Daftar makanan yang tersedia untuk segera didistribusikan.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    // ─── Filter Tabs ───────────────────────────────────────────────────────────

    private HBox buildFilterBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(14, 28, 14, 28));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        String[] filters = {"Semua", "Nasi & Lauk", "Roti & Kue", "Sayuran"};
        rebuildFilterButtons(bar, filters);
        return bar;
    }

    private void rebuildFilterButtons(HBox bar, String[] filters) {
        bar.getChildren().clear();
        for (String filter : filters) {
            Button btn = buildFilterButton(filter, filter.equals(activeFilter));
            btn.setOnAction(e -> {
                activeFilter = filter;
                rebuildFilterButtons(bar, filters);
                applyFilter();
            });
            bar.getChildren().add(btn);
        }
    }

    private Button buildFilterButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + (active ? DARK_GREEN : "white") + ";" +
                "-fx-text-fill: " + (active ? "white" : "#444") + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (active ? "bold" : "normal") + ";" +
                "-fx-background-radius: 20px;" +
                "-fx-border-color: " + (active ? DARK_GREEN : BORDER_COLOR) + ";" +
                "-fx-border-radius: 20px;" +
                "-fx-padding: 7 18 7 18;" +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    private void applyFilter() {
        catalogList.getChildren().clear();

        List<Map<String, String>> filtered;
        if ("Semua".equals(activeFilter)) {
            filtered = allItems;
        } else {
            Map<String, List<String>> keywordMap = Map.of(
                    "Nasi & Lauk", List.of("nasi", "lauk", "ayam", "ikan", "daging", "soto", "rendang", "opor", "mie", "bakso"),
                    "Roti & Kue",  List.of("roti", "kue", "cake", "croissant", "donat", "bolu", "cookies", "pie"),
                    "Sayuran",     List.of("sayur", "salad", "vegetarian", "tofu", "tempe", "tahu", "gado", "pecel")
            );
            List<String> keywords = keywordMap.getOrDefault(activeFilter, List.of(activeFilter.toLowerCase()));
            filtered = allItems.stream()
                    .filter(item -> {
                        String name = nvl(item.get("dishName"), "").toLowerCase();
                        return keywords.stream().anyMatch(name::contains);
                    })
                    .toList();
        }

        if (filtered.isEmpty()) {
            Label empty = new Label("Tidak ada donasi tersedia untuk kategori ini.");
            empty.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
            empty.setPadding(new Insets(24, 0, 0, 0));
            catalogList.getChildren().add(empty);
        } else {
            for (Map<String, String> item : filtered) {
                catalogList.getChildren().add(buildCatalogCard(item));
            }
        }
    }

    // ─── Load Catalog from API ─────────────────────────────────────────────────

    private void loadCatalog() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        parseResponse(response.body());
                    } else {
                        showEmptyState("Gagal memuat katalog donasi.");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showEmptyState("Tidak dapat terhubung ke server."));
            }
        }).start();
    }

    private void parseResponse(String json) {
        allItems.clear();
        if (json == null || json.equals("[]") || json.isEmpty()) {
            showEmptyState("Belum ada donasi yang tersedia saat ini.");
            return;
        }

        String[] entries = json.split("\\{\"donationId\":");
        for (int i = 1; i < entries.length; i++) {
            String entry = "{\"donationId\":" + entries[i];
            if (entry.endsWith(",")) entry = entry.substring(0, entry.length() - 1);
            if (entry.endsWith("]")) entry = entry.substring(0, entry.length() - 1);

            Map<String, String> item = new HashMap<>();
            item.put("donationId",       extractValue(entry, "donationId"));
            item.put("dishName",         extractValue(entry, "dishName"));
            item.put("imagePath",        extractValue(entry, "imagePath"));
            item.put("expiresInMinutes", extractValue(entry, "expiresInMinutes"));
            item.put("timeCooked",       extractValue(entry, "timeCooked"));
            item.put("donatorUsername",  extractValue(entry, "donatorUsername"));
            item.put("status",           extractValue(entry, "status"));
            allItems.add(item);
        }
        applyFilter();
    }

    private void showEmptyState(String message) {
        catalogList.getChildren().clear();
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 15px; -fx-text-fill: " + TEXT_GRAY + ";");
        label.setPadding(new Insets(32, 0, 0, 0));
        catalogList.getChildren().add(label);
    }

    // ─── Catalog Card ──────────────────────────────────────────────────────────

    private HBox buildCatalogCard(Map<String, String> item) {
        HBox card = new HBox(18);
        card.setPadding(new Insets(18));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 12, 0, 0, 3);"
        );

        // Food image
        StackPane imgPane = buildImagePane(item.get("imagePath"), item.get("dishName"));

        // Right: info
        VBox info = new VBox(8);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Row 1: dish name + category badge
        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label dishName = new Label(nvl(item.get("dishName"), "Makanan Tanpa Nama"));
        dishName.setFont(Font.font("System", FontWeight.BOLD, 17));
        dishName.setStyle("-fx-text-fill: #111;");
        dishName.setWrapText(true);
        HBox.setHgrow(dishName, Priority.ALWAYS);

        Label badge = buildCategoryBadge(item.get("dishName"));
        nameRow.getChildren().addAll(dishName, badge);

        // Row 2: donator
        Label donatorLabel = new Label("• " + nvl(item.get("donatorUsername"), "Donatur Anonim"));
        donatorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");

        // Row 3: time remaining
        HBox timeRow = buildTimeRow(item.get("timeCooked"), item.get("expiresInMinutes"));

        // Row 4: detail button (right-aligned)
        Button detailBtn = new Button("Lihat Detail");
        detailBtn.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 9 22 9 22;"
        );
        detailBtn.setOnMouseEntered(e -> detailBtn.setStyle(detailBtn.getStyle().replace(DARK_GREEN, "#0a4218")));
        detailBtn.setOnMouseExited(e -> detailBtn.setStyle(detailBtn.getStyle().replace("#0a4218", DARK_GREEN)));

        HBox btnRow = new HBox();
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.getChildren().add(detailBtn);

        info.getChildren().addAll(nameRow, donatorLabel, timeRow, btnRow);
        card.getChildren().addAll(imgPane, info);
        return card;
    }

    private StackPane buildImagePane(String imagePath, String dishName) {
        StackPane pane = new StackPane();
        pane.setPrefSize(130, 130);
        pane.setMinSize(130, 130);
        pane.setMaxSize(130, 130);

        Rectangle clip = new Rectangle(130, 130);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        pane.setClip(clip);
        pane.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12px;");

        boolean loaded = false;
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                Image img = imagePath.startsWith("http")
                        ? new Image(imagePath, 130, 130, true, true, true)
                        : new Image("file:" + imagePath, 130, 130, true, true, true);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(130);
                    iv.setFitHeight(130);
                    iv.setPreserveRatio(false);
                    pane.getChildren().add(iv);
                    loaded = true;
                }
            } catch (Exception ignored) {}
        }

        if (!loaded) {
            Label placeholder = new Label("🍽");
            placeholder.setStyle("-fx-font-size: 44px;");
            pane.getChildren().add(placeholder);
        }

        return pane;
    }

    private Label buildCategoryBadge(String dishName) {
        String name = dishName != null ? dishName.toLowerCase() : "";
        String category;
        String textColor;

        if (name.contains("nasi") || name.contains("ayam") || name.contains("ikan")
                || name.contains("daging") || name.contains("lauk") || name.contains("mie")) {
            category = "Nasi & Lauk"; textColor = "#c97a00";
        } else if (name.contains("roti") || name.contains("kue") || name.contains("cake")
                || name.contains("bolu") || name.contains("croissant")) {
            category = "Bakery"; textColor = "#7a4200";
        } else if (name.contains("sayur") || name.contains("salad")
                || name.contains("vegetarian") || name.contains("tofu") || name.contains("tempe")) {
            category = "Vegetarian"; textColor = "#1a6b1a";
        } else {
            category = "Perishable"; textColor = "#b52020";
        }

        Label badge = new Label(category);
        badge.setStyle(
                "-fx-background-color: rgba(0,0,0,0.06);" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 4 12 4 12;"
        );
        return badge;
    }

    private HBox buildTimeRow(String timeCooked, String expiresInMinutes) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER_LEFT);

        Label clockIcon = new Label("⏱");
        clockIcon.setStyle("-fx-font-size: 14px;");

        String timeText = "Waktu tidak diketahui";
        boolean urgent = false;

        if (timeCooked != null && expiresInMinutes != null) {
            try {
                LocalDateTime cooked  = LocalDateTime.parse(timeCooked, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                long expMins          = Long.parseLong(expiresInMinutes.trim());
                LocalDateTime expiry  = cooked.plusMinutes(expMins);
                long remaining        = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiry);

                if (remaining <= 0) {
                    timeText = "Sudah kedaluwarsa";
                    urgent = true;
                } else if (remaining < 60) {
                    timeText = "Sisa waktu: " + remaining + " menit";
                    urgent = true;
                } else {
                    long h = remaining / 60, m = remaining % 60;
                    timeText = m > 0
                            ? "Sisa waktu: " + h + " jam " + m + " menit"
                            : "Sisa waktu: " + h + " jam";
                }
            } catch (Exception ignored) {}
        }

        Label timeLabel = new Label(timeText);
        timeLabel.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + (urgent ? "#c62828" : "#333") + ";"
        );

        row.getChildren().addAll(clockIcon, timeLabel);
        return row;
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int start = idx + search.length();
        if (start >= json.length()) return null;
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return end == -1 ? null : json.substring(start + 1, end);
        }
        int end = json.indexOf(',', start);
        if (end == -1) end = json.indexOf('}', start);
        return end == -1 ? null : json.substring(start, end).trim();
    }

    private String nvl(String s, String fallback) {
        return (s == null || s.isEmpty() || s.equals("null")) ? fallback : s;
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

    public static void main(String[] args) {
        new RecipientCatalogUI(null).showUI();
    }
}
