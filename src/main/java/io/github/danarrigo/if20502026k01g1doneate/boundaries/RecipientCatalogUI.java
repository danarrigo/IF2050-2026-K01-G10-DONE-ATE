package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RecipientCatalogUI extends UI {

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String BG_COLOR     = "#F5F5F5";
    private static final String BORDER_COLOR = "#E8E8E8";
    private static final String TEXT_GRAY    = "#757575";
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
        VBox outer = new VBox(0);
        outer.setStyle("-fx-background-color: " + BG_COLOR + ";");
        outer.getChildren().add(buildNavbar(stage));

        VBox centerContent = new VBox(0);
        centerContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        centerContent.getChildren().addAll(buildPageHeader(), buildFilterBar());

        catalogList = new VBox(12);
        catalogList.setPadding(new Insets(16, 24, 24, 24));

        Label loading = new Label("Memuat katalog donasi...");
        loading.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
        loading.setPadding(new Insets(12, 0, 0, 0));
        catalogList.getChildren().add(loading);

        centerContent.getChildren().add(catalogList);

        ScrollPane scroll = new ScrollPane(centerContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG_COLOR + "; -fx-background: " + BG_COLOR + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        outer.getChildren().add(scroll);

        HBox bottomNav = Navigator.createBottomNav(stage, getUser(), "CATALOG");
        outer.getChildren().add(bottomNav);

        playAnimation(centerContent);
        loadCatalog();

        return outer;
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

    // ─── Navbar ────────────────────────────────────────────────────────────────

    private HBox buildNavbar(Stage stage) {
        HBox navbar = new HBox();
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(14, 24, 14, 24));
        navbar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label icon = new Label("🍴");
        icon.setStyle("-fx-font-size: 20px;");
        Label appName = new Label("DONE-ATE");
        appName.setFont(Font.font("System", FontWeight.BOLD, 20));
        appName.setStyle("-fx-text-fill: " + DARK_GREEN + ";");
        HBox logo = new HBox(8, icon, appName);
        logo.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button searchBtn = new Button("🔍");
        searchBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4 10 4 10;"
        );

        String initials = getAvatarInitials();
        Label avatarLabel = new Label(initials);
        avatarLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane avatarPane = new StackPane(avatarLabel);
        avatarPane.setPrefSize(38, 38);
        avatarPane.setMinSize(38, 38);
        avatarPane.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-background-radius: 19px;");
        Button accountBtn = new Button();
        accountBtn.setGraphic(avatarPane);
        accountBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        accountBtn.setOnAction(e -> Navigator.navigate(stage, new AccountUI(getUser())));

        navbar.getChildren().addAll(logo, spacer, searchBtn, accountBtn);
        return navbar;
    }

    // ─── Page Header ───────────────────────────────────────────────────────────

    private VBox buildPageHeader() {
        VBox header = new VBox(6);
        header.setPadding(new Insets(28, 24, 16, 24));
        header.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label title = new Label("Katalog Donasi");
        title.setFont(Font.font("System", FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill: #111;");

        Label subtitle = new Label("Daftar makanan yang tersedia untuk segera didistribusikan.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    // ─── Filter Chips ──────────────────────────────────────────────────────────

    private HBox buildFilterBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(4, 24, 18, 24));
        bar.setStyle("-fx-background-color: " + BG_COLOR + ";");
        bar.setAlignment(Pos.CENTER_LEFT);

        String[] filters = {"Semua", "Nasi & Lauk", "Roti & Kue", "Sayuran"};
        rebuildFilterChips(bar, filters);
        return bar;
    }

    private void rebuildFilterChips(HBox bar, String[] filters) {
        bar.getChildren().clear();
        for (String filter : filters) {
            boolean active = filter.equals(activeFilter);
            Button chip = new Button(filter);
            chip.setStyle(
                    "-fx-background-color: " + (active ? DARK_GREEN : "white") + ";" +
                    "-fx-text-fill: " + (active ? "white" : "#555") + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: " + (active ? "bold" : "normal") + ";" +
                    "-fx-background-radius: 20px;" +
                    "-fx-border-color: " + (active ? DARK_GREEN : BORDER_COLOR) + ";" +
                    "-fx-border-radius: 20px;" +
                    "-fx-padding: 7 18 7 18;" +
                    "-fx-cursor: hand;"
            );
            chip.setOnAction(e -> {
                activeFilter = filter;
                rebuildFilterChips(bar, filters);
                renderList(stage(chip));
            });
            bar.getChildren().add(chip);
        }
    }

    // helper: find the Stage from any node inside the scene
    private Stage stage(javafx.scene.Node node) {
        if (node == null || node.getScene() == null) return null;
        return (Stage) node.getScene().getWindow();
    }

    // ─── Load & Render ─────────────────────────────────────────────────────────

    private void loadCatalog() {
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog"))
                        .GET();
                if (token != null) builder.header("Authorization", "Bearer " + token);

                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(builder.build(), HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        parseResponse(response.body());
                    } else {
                        showEmpty("Gagal memuat katalog donasi.");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showEmpty("Tidak dapat terhubung ke server."));
            }
        }).start();
    }

    private void parseResponse(String json) {
        allItems.clear();
        if (json == null || json.equals("[]") || json.isEmpty()) {
            showEmpty("Belum ada donasi yang tersedia saat ini.");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode array = mapper.readTree(json);

            if (!array.isArray() || array.isEmpty()) {
                showEmpty("Belum ada donasi yang tersedia saat ini.");
                return;
            }

            for (JsonNode node : array) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("donationId",       textOrNull(node, "donationId"));
                item.put("dishName",         textOrNull(node, "dishName"));
                item.put("imagePath",        textOrNull(node, "imagePath"));
                item.put("expiresInMinutes", node.has("expiresInMinutes")
                        ? String.valueOf(node.get("expiresInMinutes").asLong()) : null);
                item.put("timeCooked",       textOrNull(node, "timeCooked"));
                item.put("donatorUsername",  textOrNull(node, "donatorUsername"));
                item.put("status",           textOrNull(node, "status"));
                allItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmpty("Gagal memproses data katalog.");
            return;
        }
        renderList(null);
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode n = node.get(field);
        if (n == null || n.isNull()) return null;
        return n.asText();
    }

    private void renderList(Stage stage) {
        catalogList.getChildren().clear();

        List<Map<String, String>> filtered = getFilteredItems();
        if (filtered.isEmpty()) {
            showEmpty("Tidak ada donasi tersedia untuk kategori ini.");
            return;
        }
        for (Map<String, String> item : filtered) {
            catalogList.getChildren().add(buildCard(item, stage));
        }
    }

    private List<Map<String, String>> getFilteredItems() {
        if ("Semua".equals(activeFilter)) return allItems;
        Map<String, List<String>> kws = Map.of(
                "Nasi & Lauk", List.of("nasi", "lauk", "ayam", "ikan", "daging", "soto", "rendang", "mie", "bakso", "opor"),
                "Roti & Kue",  List.of("roti", "kue", "cake", "croissant", "donat", "bolu", "cookies", "biskuit"),
                "Sayuran",     List.of("sayur", "salad", "vegetarian", "tofu", "tempe", "tahu", "gado", "kangkung")
        );
        List<String> keys = kws.getOrDefault(activeFilter, List.of(activeFilter.toLowerCase()));
        return allItems.stream()
                .filter(item -> {
                    String n = nvl(item.get("dishName"), "").toLowerCase();
                    return keys.stream().anyMatch(n::contains);
                })
                .toList();
    }

    private void showEmpty(String message) {
        catalogList.getChildren().clear();
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
        label.setPadding(new Insets(24, 0, 0, 0));
        catalogList.getChildren().add(label);
    }

    // ─── Catalog Card ──────────────────────────────────────────────────────────

    private HBox buildCard(Map<String, String> item, Stage stage) {
        HBox card = new HBox(0);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14px;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 14px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 3);"
        );

        // Left image
        StackPane imgPane = buildImagePane(item.get("imagePath"));
        card.getChildren().add(imgPane);

        // Right info
        VBox info = new VBox(0);
        info.setPadding(new Insets(16, 18, 16, 16));
        HBox.setHgrow(info, Priority.ALWAYS);

        // Row 1: dish name (left) + category badge (right)
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(nvl(item.get("dishName"), "Makanan Tanpa Nama"));
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setStyle("-fx-text-fill: #111;");
        nameLabel.setWrapText(true);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        Label badge = buildCategoryBadge(item.get("dishName"));
        titleRow.getChildren().addAll(nameLabel, badge);

        // Row 2: portions + donator name
        Label subtitleLabel = new Label("Tersedia  •  " + nvl(item.get("donatorUsername"), "Donatur"));
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");
        subtitleLabel.setPadding(new Insets(6, 0, 6, 0));

        // Row 3: time remaining
        HBox timeRow = buildTimeRow(item.get("timeCooked"), item.get("expiresInMinutes"));

        // Row 4: action button right-aligned
        Region actionSpacer = new Region();
        VBox.setVgrow(actionSpacer, Priority.ALWAYS);
        Button detailBtn = new Button("Lihat Detail");
        detailBtn.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10 22 10 22;"
        );
        detailBtn.setOnMouseEntered(e -> detailBtn.setStyle(detailBtn.getStyle().replace(DARK_GREEN, "#0a4218")));
        detailBtn.setOnMouseExited(e -> detailBtn.setStyle(detailBtn.getStyle().replace("#0a4218", DARK_GREEN)));
        detailBtn.setOnAction(e -> {
            Stage s = stage != null ? stage : stage(detailBtn);
            if (s != null) Navigator.navigate(s, new DonationDetailUI(getUser(), buildDonation(item)));
        });
        HBox btnRow = new HBox();
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.getChildren().add(detailBtn);
        btnRow.setPadding(new Insets(10, 0, 0, 0));

        info.getChildren().addAll(titleRow, subtitleLabel, timeRow, btnRow);
        card.getChildren().add(info);
        return card;
    }

    private StackPane buildImagePane(String imagePath) {
        StackPane pane = new StackPane();
        pane.setPrefSize(160, 160);
        pane.setMinSize(160, 160);
        pane.setMaxSize(160, 160);
        pane.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 14px 0 0 14px;");

        boolean loaded = false;
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                Image img = imagePath.startsWith("http")
                        ? new Image(imagePath, 160, 160, true, true, true)
                        : new Image("file:" + imagePath, 160, 160, true, true, true);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(160);
                    iv.setFitHeight(160);
                    iv.setPreserveRatio(false);
                    // Clip with left-rounded rectangle
                    Rectangle clip = new Rectangle(160, 160);
                    clip.setArcWidth(28);
                    clip.setArcHeight(28);
                    iv.setClip(clip);
                    pane.getChildren().add(iv);
                    loaded = true;
                }
            } catch (Exception ignored) {}
        }
        if (!loaded) {
            Label ph = new Label("🍽");
            ph.setStyle("-fx-font-size: 40px;");
            pane.getChildren().add(ph);
        }
        return pane;
    }

    private Label buildCategoryBadge(String dishName) {
        String name = dishName != null ? dishName.toLowerCase() : "";
        String category;
        String bgColor;
        String fgColor;

        if (name.contains("nasi") || name.contains("ayam") || name.contains("ikan")
                || name.contains("daging") || name.contains("lauk") || name.contains("mie")) {
            category = "Nasi & Lauk"; bgColor = "#FFF3E0"; fgColor = "#c97a00";
        } else if (name.contains("roti") || name.contains("kue") || name.contains("cake")
                || name.contains("bolu") || name.contains("croissant") || name.contains("cookies")) {
            category = "Bakery"; bgColor = "#F3E5D8"; fgColor = "#7a4200";
        } else if (name.contains("sayur") || name.contains("salad")
                || name.contains("vegetarian") || name.contains("tofu") || name.contains("tempe")) {
            category = "Vegetarian"; bgColor = LIGHT_GREEN; fgColor = DARK_GREEN;
        } else {
            category = "Perishable"; bgColor = "#E8F5E9"; fgColor = "#388e3c";
        }

        Label badge = new Label(category);
        badge.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + fgColor + ";" +
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
        row.setPadding(new Insets(2, 0, 0, 0));

        String timeText;
        boolean urgent = false;

        if (timeCooked != null && !timeCooked.equals("null")
                && expiresInMinutes != null && !expiresInMinutes.equals("null")) {
            try {
                LocalDateTime cooked = LocalDateTime.parse(timeCooked, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                long expMins         = Long.parseLong(expiresInMinutes.trim());
                long remaining       = ChronoUnit.MINUTES.between(LocalDateTime.now(), cooked.plusMinutes(expMins));

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
            } catch (Exception ignored) {
                timeText = "Waktu tidak diketahui";
            }
        } else {
            timeText = "Waktu tidak diketahui";
        }

        Label clockIcon = new Label("⏱");
        clockIcon.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (urgent ? "#c62828" : "#444") + ";");

        Label timeLabel = new Label(timeText);
        timeLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + (urgent ? "#c62828" : "#333") + ";"
        );

        row.getChildren().addAll(clockIcon, timeLabel);
        return row;
    }

    // ─── Build Donation for DonationDetailUI ───────────────────────────────────

    private Donation buildDonation(Map<String, String> item) {
        Dish dish = new Dish(
                nvl(item.get("dishName"), "Makanan"),
                item.get("imagePath") != null && !item.get("imagePath").equals("null") ? item.get("imagePath") : null
        );

        Donation donation = new Donation();
        try {
            String id = item.get("donationId");
            if (id != null && !id.equals("null")) donation.setDonationId(java.util.UUID.fromString(id));
        } catch (Exception ignored) {}

        donation.setDish(dish);
        donation.setStatus(nvl(item.get("status"), "QC Passed"));

        String tc = item.get("timeCooked");
        if (tc != null && !tc.equals("null")) {
            try {
                donation.setTimeCooked(LocalDateTime.parse(tc, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception ignored) {}
        }
        return donation;
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String resolveUsername() {
        if (getUser() != null && getUser().getUsername() != null) return getUser().getUsername();
        String s = SessionManager.getInstance().getUsername();
        return s != null ? s : "Penerima";
    }

    private String getAvatarInitials() {
        String u = resolveUsername();
        return u.length() >= 2 ? u.substring(0, 2).toUpperCase() : u.toUpperCase();
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
