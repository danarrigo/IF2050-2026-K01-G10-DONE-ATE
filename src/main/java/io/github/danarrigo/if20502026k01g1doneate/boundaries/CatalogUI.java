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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CatalogUI extends UI {

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String BG_COLOR     = "#F2F5F2";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String TEXT_GRAY    = "#666666";
    private static final String BASE_URL     = "http://localhost:8080";

    private VBox catalogList;
    private Label statusLabel;

    public CatalogUI(User user) {
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

        VBox outer = new VBox(0);
        outer.setStyle("-fx-background-color: " + BG_COLOR + ";");
        outer.getChildren().add(buildNavbar(stage));

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        VBox centerContent = new VBox(24);
        centerContent.setPadding(new Insets(20, 20, 20, 20));
        centerContent.setStyle("-fx-background-color: " + BG_COLOR + ";");
        centerContent.getChildren().addAll(
                buildHeroBanner(stage, username),
                buildQuickServices(stage),
                statusLabel,
                buildCatalogSection(stage)
        );

        ScrollPane scroll = new ScrollPane(centerContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG_COLOR + "; -fx-background: " + BG_COLOR + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        outer.getChildren().add(scroll);

        HBox bottomNav = Navigator.createBottomNav(stage, getUser(), "HOME");
        outer.getChildren().add(bottomNav);

        Button fab = buildFAB(stage);
        StackPane.setAlignment(fab, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fab, new Insets(0, 90, 90, 0));

        StackPane root = new StackPane(outer, fab);

        playAnimation(centerContent);
        loadCatalog(username, stage);

        return root;
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
        Scene scene = new Scene(getSceneContent(stage));
        stage.setScene(scene);
        stage.setFullScreen(true);
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
        dialog.setScene(new Scene(content));
        dialog.show();
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

        Label leaf = new Label("🌿");
        leaf.setStyle("-fx-font-size: 64px; -fx-opacity: 0.25;");
        StackPane.setAlignment(leaf, Pos.CENTER_RIGHT);
        StackPane.setMargin(leaf, new Insets(0, 20, 0, 0));

        bannerStack.getChildren().addAll(banner, leaf);

        return new VBox(bannerStack);
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

    // ─── Catalog Section ───────────────────────────────────────────────────────

    private VBox buildCatalogSection(Stage stage) {
        VBox section = new VBox(14);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Donasi Saya");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #111;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Tambah Donasi");
        addBtn.setStyle(
                "-fx-background-color: " + DARK_GREEN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 20px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 18 8 18;"
        );
        addBtn.setOnAction(e -> Navigator.navigate(stage, new InputDonationUI(getUser())));

        header.getChildren().addAll(title, spacer, addBtn);

        catalogList = new VBox(12);
        Label loading = new Label("Memuat donasi...");
        loading.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");
        catalogList.getChildren().add(loading);

        section.getChildren().addAll(header, catalogList);
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

    // ─── Load catalog from API ─────────────────────────────────────────────────

    private void loadCatalog(String username, Stage stage) {
        new Thread(() -> {
            try {
                String token = SessionManager.getInstance().getToken();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/donator/" + username))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    catalogList.getChildren().clear();
                    if (response.statusCode() == 200) {
                        parseAndRenderCatalog(response.body(), stage);
                    } else {
                        showStatus("Gagal memuat katalog.", true);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showStatus("Tidak dapat terhubung ke server.", true));
            }
        }).start();
    }

    private void parseAndRenderCatalog(String json, Stage stage) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> items = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});

            if (items.isEmpty()) {
                showEmptyCatalog("Belum ada donasi. Mulai donasi sekarang!");
                return;
            }

            for (Map<String, Object> item : items) {
                String donationId = String.valueOf(item.get("donationId"));
                String dishName   = String.valueOf(item.get("dishName"));
                String status     = String.valueOf(item.get("status"));
                String timeAdded  = String.valueOf(item.get("timeAdded"));
                String expiresIn  = String.valueOf(item.get("expiresInMinutes"));
                String imagePath  = String.valueOf(item.get("imagePath"));
                String timeCooked = String.valueOf(item.get("timeCooked"));
                boolean taken     = Boolean.parseBoolean(String.valueOf(item.getOrDefault("taken", false)));

                catalogList.getChildren().add(
                        buildCatalogCard(stage, donationId, dishName, status, timeAdded, expiresIn, imagePath, timeCooked, taken));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Gagal memproses data katalog.", true);
        }
    }

    private void showEmptyCatalog(String msg) {
        Label label = new Label(msg);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_GRAY + ";");
        catalogList.getChildren().add(label);
    }

    private VBox buildCatalogCard(Stage stage, String donationId, String dishName, String status,
            String timeAdded, String expiresIn, String imagePath, String timeCooked, boolean taken) {
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
        imgStack.setPrefHeight(120);
        imgStack.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12px 12px 0 0;");

        boolean loaded = false;
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                Image img = imagePath.startsWith("http")
                        ? new Image(imagePath, 800, 120, true, true, true)
                        : new Image("file:" + imagePath, 800, 120, true, true, true);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitHeight(120);
                    iv.setPreserveRatio(false);
                    iv.setFitWidth(800);
                    Rectangle clip = new Rectangle(800, 120);
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

        // Card info + actions
        VBox info = new VBox(8);
        info.setPadding(new Insets(12, 14, 14, 14));

        Label name = new Label(nvl(dishName, "Makanan"));
        name.setFont(Font.font("System", FontWeight.BOLD, 15));
        name.setStyle("-fx-text-fill: #111;");
        name.setWrapText(true);

        String statusColor = switch (nvl(status, "")) {
            case "QC Passed" -> "#1a7a1a";
            case "QC Failed" -> "#c62828";
            case "Removed"   -> "#888";
            default          -> "#e67e00";
        };
        Label statusBadge = new Label(nvl(status, "QC Pending"));
        statusBadge.setStyle(
                "-fx-background-color: rgba(0,0,0,0.05);" +
                "-fx-text-fill: " + statusColor + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 2 10 2 10;"
        );

        String addedText = (timeAdded != null && !timeAdded.equals("null"))
                ? "Ditambahkan: " + timeAdded.replace("T", " ").substring(0, Math.min(16, timeAdded.length()))
                : "";
        Label timeLabel = new Label(addedText);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_GRAY + ";");

        Label takenLabel = new Label(taken ? "● Sudah diklaim" : "● Belum diklaim");
        takenLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (taken ? "#1a7a1a" : TEXT_GRAY) + ";");

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = new Button("Edit");
        editBtn.setStyle(
                "-fx-background-color: " + LIGHT_GREEN + ";" +
                "-fx-text-fill: " + DARK_GREEN + ";" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 16 6 16;"
        );
        editBtn.setOnAction(e -> {
            // Reconstruct Donation object for Detail UI
            io.github.danarrigo.if20502026k01g1doneate.entities.Dish dish = new io.github.danarrigo.if20502026k01g1doneate.entities.Dish();
            dish.setName(dishName);
            dish.setImagePath(imagePath);
            if (expiresIn != null && !expiresIn.equals("null")) {
                dish.setExpiresIn(java.time.Duration.ofMinutes(Long.parseLong(expiresIn)));
            }

            io.github.danarrigo.if20502026k01g1doneate.entities.Donation d = new io.github.danarrigo.if20502026k01g1doneate.entities.Donation();
            d.setDonationId(UUID.fromString(donationId));
            d.setDish(dish);
            d.setStatus(status);
            d.setTaken(taken);
            if (timeCooked != null && !timeCooked.equals("null")) {
                d.setTimeCooked(LocalDateTime.parse(timeCooked));
            }
            if (timeAdded != null && !timeAdded.equals("null")) {
                d.setTimeAdded(LocalDateTime.parse(timeAdded));
            }
            
            io.github.danarrigo.if20502026k01g1doneate.entities.Donator donator = new io.github.danarrigo.if20502026k01g1doneate.entities.Donator();
            donator.setUsername(resolveUsername());
            d.setDonator(donator);

            Navigator.navigate(stage, new DonationDetailUI(getUser(), d));
        });

        Button removeBtn = new Button("Hapus");
        removeBtn.setStyle(
                "-fx-background-color: #FADBD8;" +
                "-fx-text-fill: #c62828;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 16 6 16;"
        );
        removeBtn.setOnAction(e -> removeDonation(stage, donationId));

        actions.getChildren().addAll(editBtn, removeBtn);
        info.getChildren().addAll(name, statusBadge, timeLabel, takenLabel, actions);
        card.getChildren().addAll(imgStack, info);
        return card;
    }


    // ─── API calls ─────────────────────────────────────────────────────────────

    private void removeDonation(Stage stage, String donationId) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/catalog/" + donationId))
                        .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        showStatus("Donasi berhasil dihapus.", false);
                        loadCatalog(resolveUsername(), stage);
                    } else {
                        showStatus("Gagal menghapus donasi.", true);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showStatus("Tidak dapat terhubung ke server.", true));
            }
        }).start();
    }


    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String computeTimeLeft(String timeCooked, String expiresInMinutes) {
        if (timeCooked == null || expiresInMinutes == null) return "Waktu ?";
        try {
            LocalDateTime cooked = LocalDateTime.parse(timeCooked, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            long expMins         = Long.parseLong(expiresInMinutes.trim());
            LocalDateTime expiry = cooked.plusMinutes(expMins);
            long remaining       = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiry);
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
        return s != null ? s : "";
    }

    private String getAvatarInitials() {
        String u = resolveUsername();
        return u.length() >= 2 ? u.substring(0, 2).toUpperCase() : u.toUpperCase();
    }

    private void showStatus(String msg, boolean isError) {
        if (statusLabel == null) return;
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (isError ? "#c62828" : DARK_GREEN) + ";");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private String nvl(String s, String fallback) {
        return (s == null || s.isEmpty() || s.equals("null")) ? fallback : s;
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
        new CatalogUI(null).showUI();
    }
}
