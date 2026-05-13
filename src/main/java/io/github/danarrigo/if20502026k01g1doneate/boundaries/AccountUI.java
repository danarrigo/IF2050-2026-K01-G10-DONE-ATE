package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.session.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AccountUI extends UI {

    private static boolean jfxInitialized = false;

    private static final String DARK_GREEN   = "#0F5B21";
    private static final String LIGHT_GREEN  = "#D2F4D6";
    private static final String TEXT_GRAY    = "#555555";
    private static final String BORDER_COLOR = "#E0E0E0";
    private static final String BG_COLOR     = "#FAFAFA";

    public AccountUI(User user) {
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
        Stage stage = new Stage();
        stage.setTitle("DONE-ATE - Akun Saya");
        stage.setMaximized(true);
        showAccountScene(stage);
        stage.show();
    }

    private void showAccountScene(Stage stage) {
        SessionManager session = SessionManager.getInstance();
        String username = session.getUsername() != null ? session.getUsername()
                : (getUser() != null ? getUser().getUsername() : "Pengguna");
        String role = session.getRole() != null ? session.getRole() : "-";

        User user = getUser();
        String email   = user != null ? user.getEmail()       : "-";
        String phone   = user != null ? user.getPhoneNumber() : "-";
        String address = user != null ? user.getAddress()     : "-";

        HBox root = new HBox();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        VBox sidebar = buildSidebar(stage, username, role);
        ScrollPane contentScroll = buildContent(username, role, email, phone, address);
        HBox.setHgrow(contentScroll, Priority.ALWAYS);

        root.getChildren().addAll(sidebar, contentScroll);

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
        } else {
            scene.setRoot(root);
        }

        playAnimation(root);
    }

    // ─── Sidebar ───────────────────────────────────────────────────────────────

    private VBox buildSidebar(Stage stage, String username, String role) {
        VBox sidebar = new VBox(24);
        sidebar.setPrefWidth(300);
        sidebar.setMinWidth(300);
        sidebar.setPadding(new Insets(60, 30, 40, 30));
        sidebar.setStyle("-fx-background-color: " + DARK_GREEN + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // Avatar circle with initials
        StackPane avatar = new StackPane();
        Circle circle = new Circle(50);
        circle.setFill(Color.web("#ffffff", 0.2));
        circle.setStroke(Color.web("#ffffff", 0.5));
        circle.setStrokeWidth(2);
        String initials = username.length() >= 2
                ? username.substring(0, 2).toUpperCase()
                : username.toUpperCase();
        Label initialsLabel = new Label(initials);
        initialsLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        initialsLabel.setStyle("-fx-text-fill: white;");
        avatar.getChildren().addAll(circle, initialsLabel);

        Label usernameLabel = new Label(username);
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        usernameLabel.setStyle("-fx-text-fill: white;");
        usernameLabel.setWrapText(true);
        usernameLabel.setAlignment(Pos.CENTER);

        Label roleBadge = new Label(formatRole(role));
        roleBadge.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 4 16 4 16;"
        );

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        sep.setMaxWidth(Double.MAX_VALUE);

        HBox profileMenu = buildMenuEntry("Profil Saya", true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Keluar");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 14 0 14 0;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutBtn.getStyle().replace("0.15", "0.30")));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutBtn.getStyle().replace("0.30", "0.15")));
        logoutBtn.setOnAction(e -> handleLogout(stage));

        sidebar.getChildren().addAll(avatar, usernameLabel, roleBadge, sep, profileMenu, spacer, logoutBtn);
        return sidebar;
    }

    private HBox buildMenuEntry(String text, boolean active) {
        HBox entry = new HBox(12);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPadding(new Insets(12, 16, 12, 16));
        entry.setMaxWidth(Double.MAX_VALUE);
        entry.setStyle(
                "-fx-background-color: " + (active ? "rgba(255,255,255,0.15)" : "transparent") + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;"
        );
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;" + (active ? " -fx-font-weight: bold;" : ""));
        entry.getChildren().add(label);
        return entry;
    }

    // ─── Main Content ──────────────────────────────────────────────────────────

    private ScrollPane buildContent(String username, String role, String email, String phone, String address) {
        VBox content = new VBox(32);
        content.setPadding(new Insets(60, 80, 60, 80));
        content.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label pageTitle = new Label("Profil Saya");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 36));
        pageTitle.setStyle("-fx-text-fill: #111;");

        Label pageSubtitle = new Label("Informasi akun dan data profil Anda.");
        pageSubtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: " + TEXT_GRAY + ";");

        VBox profileCard = buildInfoCard("Informasi Akun", new String[][]{
                {"Username",        username},
                {"Peran",           formatRole(role)},
                {"Email",           email},
                {"Nomor Telepon",   phone},
                {"Alamat",          address}
        });

        content.getChildren().addAll(pageTitle, pageSubtitle, profileCard);

        VBox roleCard = buildRoleCard(role);
        if (roleCard != null) content.getChildren().add(roleCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + BG_COLOR + "; -fx-background: " + BG_COLOR + ";");
        return scroll;
    }

    private VBox buildInfoCard(String title, String[][] rows) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 20, 0, 0, 4);"
        );

        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        cardTitle.setStyle("-fx-text-fill: " + DARK_GREEN + ";");

        Separator sep = new Separator();

        VBox fields = new VBox(16);
        for (String[] row : rows) {
            HBox fieldRow = new HBox(20);
            fieldRow.setAlignment(Pos.TOP_LEFT);

            Label key = new Label(row[0]);
            key.setPrefWidth(160);
            key.setMinWidth(160);
            key.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

            String val = (row[1] != null && !row[1].isEmpty() && !row[1].equals("null")) ? row[1] : "-";
            Label value = new Label(val);
            value.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_GRAY + ";");
            value.setWrapText(true);
            HBox.setHgrow(value, Priority.ALWAYS);

            fieldRow.getChildren().addAll(key, value);
            fields.getChildren().add(fieldRow);
        }

        card.getChildren().addAll(cardTitle, sep, fields);
        return card;
    }

    private VBox buildRoleCard(String role) {
        if (role == null) return null;
        return switch (role.toUpperCase()) {
            case "DONATOR" -> buildInfoCard("Informasi Donator", new String[][]{
                    {"Tipe Donator", "Individu / Organisasi"},
                    {"Riwayat Donasi", "Lihat di menu Riwayat"}
            });
            case "RECIPIENT" -> buildInfoCard("Informasi Penerima", new String[][]{
                    {"Tipe Penerima", "-"},
                    {"Jam Operasional", "-"}
            });
            default -> null;
        };
    }

    // ─── Logout ────────────────────────────────────────────────────────────────

    private void handleLogout(Stage stage) {
        SessionManager.getInstance().clearSession();
        stage.close();
        new LoginUI().showUI();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String formatRole(String role) {
        if (role == null) return "-";
        return switch (role.toUpperCase()) {
            case "DONATOR"   -> "Donator";
            case "RECIPIENT" -> "Penerima";
            default          -> role;
        };
    }

    private void playAnimation(HBox root) {
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
        new AccountUI(null).showUI();
    }
}
