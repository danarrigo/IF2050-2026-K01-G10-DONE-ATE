package io.github.danarrigo.if20502026k01g1doneate.boundaries;

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

import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DonatorHomeUI extends UI {

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String BG_COLOR     = "#F2F5F2";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String TEXT_GRAY    = "#666666";
    private static final String BASE_URL     = "http://localhost:8080";

    private VBox recentDonationsBox;

    public DonatorHomeUI(User user) {
        super(user);
    }

    @Override
    public void showUI() {
        initJFX();
        Platform.runLater(this::createAndShowStage);
    }

    @Override
    public Parent getSceneContent(Stage stage) {
        String username = resolveUsername();

        // Outer VBox: navbar + scroll + bottom nav
        VBox outer = new VBox(0);
        outer.setStyle("-fx-background-color: " + BG_COLOR + ";");

        outer.getChildren().add(buildNavbar(stage));

        // Scrollable center content
        VBox centerContent = new VBox(24);
        centerContent.setPadding(new Insets(20, 20, 20, 20));
        centerContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        centerContent.getChildren().addAll(
                buildHeroBanner(stage, username),
                buildQuickServices(stage),
                buildRecentSection(stage, username)
        );

        ScrollPane scroll = new ScrollPane(centerContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG_COLOR + "; -fx-background: " + BG_COLOR + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        outer.getChildren().add(scroll);

        HBox bottomNav = Navigator.createBottomNav(stage, getUser(), "HOME");
        outer.getChildren().add(bottomNav);

        // FAB overlay via StackPane
        Button fab = buildFAB(stage);
        StackPane.setAlignment(fab, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fab, new Insets(0, 90, 90, 0));

        StackPane root = new StackPane(outer, fab);

        playAnimation(centerContent);
        loadRecentDonations(username);

        return root;
    }

    private void createAndShowStage() {
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Home");
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

        Button bellBtn = new Button("🔔");
        bellBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 4 10 4 10;");

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

        navbar.getChildren().addAll(logo, spacer, bellBtn, accountBtn);
        return navbar;
    }

    // ─── Hero Banner ───────────────────────────────────────────────────────────

    private VBox buildHeroBanner(Stage stage, String username) {
        StackPane bannerStack = new StackPane();

        VBox banner = new VBox(14);
        banner.setPadding(new Insets(28, 140, 28, 28));
        banner.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-background-radius: 16px;"
        );

        Label greeting = new Label("Selamat Datang, " + username + "!");
        greeting.setFont(Font.font("System", FontWeight.BOLD, 24));
        greeting.setStyle("-fx-text-fill: white;");
        greeting.setWrapText(true);

        Label subtitle = new Label(
                "Bersama, kita telah menyelamatkan lebih dari 500 porsi\nmakanan minggu ini. Teruskan aksi baikmu!"
        );
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.82);");
        subtitle.setWrapText(true);

        Button donateBtn = new Button("🤲  Donasi Sekarang");
        donateBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-border-color: rgba(255,255,255,0.7);" +
                "-fx-border-radius: 20px;" +
                "-fx-background-radius: 20px;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 9 20 9 20;"
        );
        donateBtn.setOnMouseEntered(e -> donateBtn.setStyle(donateBtn.getStyle().replace("transparent", "rgba(255,255,255,0.15)")));
        donateBtn.setOnMouseExited(e -> donateBtn.setStyle(donateBtn.getStyle().replace("rgba(255,255,255,0.15)", "transparent")));
        donateBtn.setOnAction(e -> Navigator.navigate(stage, new InputDonationUI(getUser())));

        banner.getChildren().addAll(greeting, subtitle, donateBtn);

        // Leaf decoration (top-right)
        Label leaf = new Label("🌿");
        leaf.setStyle("-fx-font-size: 64px; -fx-opacity: 0.25;");
        StackPane.setAlignment(leaf, Pos.CENTER_RIGHT);
        StackPane.setMargin(leaf, new Insets(0, 20, 0, 0));

        bannerStack.getChildren().addAll(banner, leaf);
        bannerStack.setStyle("-fx-background-radius: 16px;");

        VBox wrapper = new VBox(bannerStack);
        return wrapper;
    }

    // ─── Quick Services ────────────────────────────────────────────────────────

    private VBox buildQuickServices(Stage stage) {
        VBox section = new VBox(14);

        Label title = new Label("Layanan Cepat");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #111;");

        HBox tiles = new HBox(12);
        tiles.setAlignment(Pos.CENTER);

        String[][] services = {
                {"☰", "Katalog\nDonasi"},
                {"✉", "Inbox"},
                {"⟳", "Riwayat\nDonasi"},
                {"👤", "Akun"},
                {"•••", "Lainnya"}
        };

        for (String[] svc : services) {
            VBox tile = buildServiceTile(svc[0], svc[1], stage);
            HBox.setHgrow(tile, Priority.ALWAYS);
            tiles.getChildren().add(tile);
        }

        section.getChildren().addAll(title, tiles);
        return section;
    }

    private VBox buildServiceTile(String emoji, String label, Stage stage) {
        VBox tile = new VBox(8);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(16, 6, 14, 6));
        tile.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-cursor: hand;"
        );

        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(42, 42);
        iconPane.setMinSize(42, 42);
        iconPane.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12px;");
        Label iconLabel = new Label(emoji);
        iconLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + DARK_GREEN + ";");
        iconPane.getChildren().add(iconLabel);

        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #333; -fx-font-weight: bold;");
        textLabel.setWrapText(true);
        textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        tile.getChildren().addAll(iconPane, textLabel);

        tile.setOnMouseClicked(e -> {
            UI target = switch (label) {
                case "Katalog\nDonasi" -> new CatalogUI(getUser());
                case "Inbox"           -> new InboxUI(getUser());
                case "Riwayat\nDonasi" -> new HistoryUI(getUser());
                case "Akun"            -> new AccountUI(getUser());
                default                -> null;
            };
            if (target != null) Navigator.navigate(stage, target);
        });
        tile.setOnMouseEntered(e -> tile.setStyle(
                "-fx-background-color: " + LIGHT_GREEN + ";" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: " + DARK_GREEN + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-cursor: hand;"
        ));
        tile.setOnMouseExited(e -> tile.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-cursor: hand;"
        ));

        return tile;
    }

    // ─── Recent Donations ──────────────────────────────────────────────────────

    private VBox buildRecentSection(Stage stage, String username) {
        VBox section = new VBox(14);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Donasi Terakhir");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #111;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink viewAll = new Hyperlink("Lihat Semua");
        viewAll.setStyle(
                "-fx-text-fill: " + DARK_GREEN + ";" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: transparent;" +
                "-fx-font-size: 13px;"
        );
        viewAll.setOnAction(e -> Navigator.navigate(stage, new CatalogUI(getUser())));

        header.getChildren().addAll(title, spacer, viewAll);

        recentDonationsBox = new VBox(10);
        Label loading = new Label("Memuat donasi terakhir...");
        loading.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");
        recentDonationsBox.getChildren().add(loading);

        section.getChildren().addAll(header, recentDonationsBox);
        return section;
    }

    // ─── FAB ───────────────────────────────────────────────────────────────────

    private Button buildFAB(Stage stage) {
        Button fab = new Button("+");
        fab.setFont(Font.font("System", FontWeight.BOLD, 26));
        fab.setPrefSize(60, 60);
        fab.setMinSize(60, 60);
        fab.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 30px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);"
        );
        fab.setOnMouseEntered(e -> fab.setStyle(fab.getStyle().replace(DARK_GREEN, "#0a4218")));
        fab.setOnMouseExited(e -> fab.setStyle(fab.getStyle().replace("#0a4218", DARK_GREEN)));
        fab.setOnAction(e -> Navigator.navigate(stage, new InputDonationUI(getUser())));
        return fab;
    }

    // ─── Load & Render Recent Donations ────────────────────────────────────────

    private void loadRecentDonations(String username) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/donator/" + username))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    recentDonationsBox.getChildren().clear();
                    if (response.statusCode() == 200) {
                        renderCards(response.body());
                    } else {
                        showEmptyRecent("Tidak dapat memuat donasi.");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showEmptyRecent("Tidak dapat terhubung ke server."));
            }
        }).start();
    }

    private void renderCards(String json) {
        if (json == null || json.equals("[]") || json.isEmpty()) {
            showEmptyRecent("Belum ada donasi. Mulai donasi sekarang!");
            return;
        }

        HBox row = new HBox(14);
        row.setAlignment(Pos.TOP_LEFT);

        String[] entries = json.split("\\{\"donationId\":");
        int count = 0;
        for (int i = 1; i < entries.length && count < 3; i++) {
            String entry = "{\"donationId\":" + entries[i];
            if (entry.endsWith(",")) entry = entry.substring(0, entry.length() - 1);
            if (entry.endsWith("]")) entry = entry.substring(0, entry.length() - 1);

            VBox card = buildDonationCard(
                    extractValue(entry, "dishName"),
                    extractValue(entry, "imagePath"),
                    extractValue(entry, "expiresInMinutes"),
                    extractValue(entry, "timeCooked"),
                    extractValue(entry, "status")
            );
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
            count++;
        }

        if (count == 0) {
            showEmptyRecent("Belum ada donasi aktif.");
        } else {
            recentDonationsBox.getChildren().add(row);
        }
    }

    private void showEmptyRecent(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");
        recentDonationsBox.getChildren().add(label);
    }

    private VBox buildDonationCard(String dishName, String imagePath,
                                   String expiresIn, String timeCooked, String status) {
        VBox card = new VBox(0);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 2, 2);"
        );

        // Image with time badge overlay
        StackPane imgStack = new StackPane();
        imgStack.setPrefHeight(110);
        imgStack.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12px 12px 0 0;");

        boolean loaded = false;
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                Image img = imagePath.startsWith("http")
                        ? new Image(imagePath, 250, 110, true, true, true)
                        : new Image("file:" + imagePath, 250, 110, true, true, true);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(250);
                    iv.setFitHeight(110);
                    iv.setPreserveRatio(false);
                    Rectangle clip = new Rectangle(250, 110);
                    clip.setArcWidth(24);
                    clip.setArcHeight(24);
                    iv.setClip(clip);
                    imgStack.getChildren().add(iv);
                    loaded = true;
                }
            } catch (Exception ignored) {}
        }
        if (!loaded) {
            Label ph = new Label("🍽");
            ph.setStyle("-fx-font-size: 36px;");
            imgStack.getChildren().add(ph);
        }

        // Time badge
        String timeText = computeTimeLeft(timeCooked, expiresIn);
        Label timeBadge = new Label(timeText);
        timeBadge.setStyle(
                "-fx-background-color: rgba(0,0,0,0.58);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6px;" +
                "-fx-padding: 3 8 3 8;"
        );
        StackPane.setAlignment(timeBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(timeBadge, new Insets(8, 8, 0, 0));
        imgStack.getChildren().add(timeBadge);

        // Card info
        VBox info = new VBox(6);
        info.setPadding(new Insets(10, 12, 12, 12));

        HBox nameRow = new HBox(6);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(nvl(dishName, "Makanan"));
        name.setFont(Font.font("System", FontWeight.BOLD, 14));
        name.setStyle("-fx-text-fill: #111;");
        name.setWrapText(true);
        HBox.setHgrow(name, Priority.ALWAYS);
        Label heart = new Label("♡");
        heart.setStyle("-fx-font-size: 15px; -fx-text-fill: #bbb;");
        nameRow.getChildren().addAll(name, heart);

        String statusColor = switch (nvl(status, "")) {
            case "QC Passed" -> "#1a7a1a";
            case "QC Failed" -> "#c62828";
            case "Removed"   -> "#888";
            default          -> "#e67e00";
        };
        Label statusLabel = new Label(nvl(status, "QC Pending"));
        statusLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.05);" +
                "-fx-text-fill: " + statusColor + ";" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 2 8 2 8;"
        );

        info.getChildren().addAll(nameRow, statusLabel);
        card.getChildren().addAll(imgStack, info);
        return card;
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String computeTimeLeft(String timeCooked, String expiresInMinutes) {
        if (timeCooked == null || expiresInMinutes == null) return "Waktu ?";
        try {
            LocalDateTime cooked  = LocalDateTime.parse(timeCooked, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            long expMins          = Long.parseLong(expiresInMinutes.trim());
            LocalDateTime expiry  = cooked.plusMinutes(expMins);
            long remaining        = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiry);
            if (remaining <= 0) return "Kedaluwarsa";
            if (remaining < 60) return "Sisa " + remaining + " Menit";
            long h = remaining / 60, m = remaining % 60;
            return m > 0 ? "Sisa " + h + " Jam " + m + " Mnt" : "Sisa " + h + " Jam";
        } catch (Exception ignored) {
            return "Waktu ?";
        }
    }

    private String resolveUsername() {
        if (getUser() != null && getUser().getUsername() != null) return getUser().getUsername();
        String s = SessionManager.getInstance().getUsername();
        return s != null ? s : "Donator";
    }

    private String getAvatarInitials() {
        String u = resolveUsername();
        return u.length() >= 2 ? u.substring(0, 2).toUpperCase() : u.toUpperCase();
    }

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
        new DonatorHomeUI(null).showUI();
    }
}
