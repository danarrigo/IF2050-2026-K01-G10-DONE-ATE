package io.github.danarrigo.if20502026k01g1doneate.boundaries;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class LoginUI extends Application {

    private static final String BASE_URL   = "http://localhost:8080";
    private static final String GREEN_DARK = "#2a5f2a";
    private static final String GREEN_LIGHT = "#1a4d1a";

    private TextField     usernameField;
    private PasswordField passwordField;
    private Label         errorLabel;
    private Button        loginButton;

    @Override
    public void start(Stage stage) {
        HBox root = new HBox();
        root.setPrefSize(1920, 1080);
        root.getChildren().addAll(buildLeftPanel(), buildRightPanel(stage));

        Scene scene = new Scene(root, 1920, 1080);
        stage.setTitle("DONE-ATE");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // ─── Left Panel (Branding) ─────────────────────────────────────────────────

    private StackPane buildLeftPanel() {
        StackPane panel = new StackPane();
        panel.setPrefSize(960, 1080);
        panel.setMinSize(960, 1080);

        Canvas canvas = new Canvas(960, 1080);
        drawLeftBackground(canvas.getGraphicsContext2D(), 960, 1080);

        VBox branding = new VBox(32);
        branding.setAlignment(Pos.CENTER);

        StackPane logoBox = new StackPane();
        Rectangle rect = new Rectangle(110, 110);
        rect.setArcWidth(28);
        rect.setArcHeight(28);
        rect.setFill(Color.web("#ffffff", 0.15));
        rect.setStroke(Color.web("#ffffff", 0.4));
        rect.setStrokeWidth(2);
        Label icon = new Label("🍴");
        icon.setStyle("-fx-font-size: 52px;");
        logoBox.getChildren().addAll(rect, icon);

        Label appName = new Label("DONE-ATE");
        appName.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 60));
        appName.setStyle("-fx-text-fill: white; -fx-letter-spacing: 4;");

        Label tagline = new Label("Solusi Berbagi Makanan untuk Komunitas");
        tagline.setStyle("-fx-font-size: 22px; -fx-text-fill: rgba(255,255,255,0.75);");

        Separator sep = new Separator();
        sep.setMaxWidth(140);
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.3);");

        Label desc = new Label("Bergabunglah bersama ribuan donator dan\npenerima manfaat di seluruh Indonesia.");
        desc.setStyle("-fx-font-size: 17px; -fx-text-fill: rgba(255,255,255,0.6); -fx-text-alignment: center;");
        desc.setTextAlignment(TextAlignment.CENTER);

        branding.getChildren().addAll(logoBox, appName, tagline, sep, desc);
        panel.getChildren().addAll(canvas, branding);
        return panel;
    }

    private void drawLeftBackground(GraphicsContext gc, double w, double h) {
        LinearGradient grad = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1a4d1a")),
                new Stop(1, Color.web("#2a5f2a"))
        );
        gc.setFill(grad);
        gc.fillRect(0, 0, w, h);

        double cx = w / 2.0, cy = h / 2.0;
        gc.setStroke(Color.web("#ffffff", 0.05));
        gc.setLineWidth(1.2);
        int[] radii = {400, 340, 280, 220, 160, 100, 50};
        for (int r : radii) gc.strokeOval(cx - r, cy - r, r * 2, r * 2);

        for (int deg = 0; deg < 180; deg += 15) {
            double rad = Math.toRadians(deg);
            gc.strokeLine(cx + 400 * Math.cos(rad), cy + 400 * Math.sin(rad),
                          cx - 400 * Math.cos(rad), cy - 400 * Math.sin(rad));
        }

        gc.setStroke(Color.web("#ffffff", 0.03));
        for (int i = 0; i < 12; i++) {
            double rad = Math.toRadians(i * 30);
            gc.strokeOval(cx + 330 * Math.cos(rad) - 24, cy + 330 * Math.sin(rad) - 42, 48, 84);
        }
        for (int i = 0; i < 12; i++) {
            double rad = Math.toRadians(i * 30);
            gc.strokeOval(cx + 220 * Math.cos(rad) - 18, cy + 220 * Math.sin(rad) - 30, 36, 60);
        }
    }

    // ─── Right Panel (Form) ────────────────────────────────────────────────────

    private StackPane buildRightPanel(Stage stage) {
        StackPane panel = new StackPane();
        panel.setPrefSize(960, 1080);
        panel.setStyle("-fx-background-color: #f4f7f4;");

        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(520);
        content.getChildren().addAll(buildFormHeader(), buildCard(), buildFooter(stage));

        panel.getChildren().add(content);
        return panel;
    }

    private VBox buildFormHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Selamat Datang Kembali");
        title.setFont(Font.font("System", FontWeight.BOLD, 34));
        title.setStyle("-fx-text-fill: #111;");

        Label sub = new Label("Masukkan detail akun Anda untuk melanjutkan.");
        sub.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");

        header.getChildren().addAll(title, sub);
        return header;
    }

    private VBox buildCard() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(36, 32, 36, 32));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 30, 0, 0, 6);"
        );

        VBox usernameGroup = new VBox(8);
        Label usernameLabel = new Label("Email atau Username");
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        usernameField = new TextField();
        usernameField.setPromptText("nama@contoh.com");
        applyInputStyle(usernameField);
        usernameGroup.getChildren().addAll(usernameLabel, usernameField);

        VBox passwordGroup = new VBox(8);
        HBox pwdLabelRow = new HBox();
        pwdLabelRow.setAlignment(Pos.CENTER_LEFT);
        Label pwdLabel = new Label("Password");
        pwdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Hyperlink forgot = new Hyperlink("Lupa password?");
        forgot.setStyle("-fx-font-size: 13px; -fx-text-fill: " + GREEN_DARK + "; -fx-border-color: transparent; -fx-padding: 0;");
        pwdLabelRow.getChildren().addAll(pwdLabel, spacer, forgot);

        passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        applyInputStyle(passwordField);
        passwordGroup.getChildren().addAll(pwdLabelRow, passwordField);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #c62828;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        loginButton = new Button("Masuk  ➜");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 17));
        loginButton.setStyle(
                "-fx-background-color: " + GREEN_DARK + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 16 0 16 0;" +
                "-fx-cursor: hand;"
        );
        loginButton.setOnMouseEntered(e ->
                loginButton.setStyle(loginButton.getStyle().replace(GREEN_DARK, "#1e4a1e")));
        loginButton.setOnMouseExited(e ->
                loginButton.setStyle(loginButton.getStyle().replace("#1e4a1e", GREEN_DARK)));
        loginButton.setOnAction(e -> handleLogin());

        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());

        card.getChildren().addAll(usernameGroup, passwordGroup, errorLabel, loginButton);
        return card;
    }

    private void applyInputStyle(TextInputControl input) {
        String base =
                "-fx-background-color: #f6f6f6;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: transparent;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 14 16 14 16;" +
                "-fx-font-size: 15px;";
        String focused =
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + GREEN_DARK + ";" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 14 16 14 16;" +
                "-fx-font-size: 15px;";
        input.setStyle(base);
        input.setMaxWidth(Double.MAX_VALUE);
        input.focusedProperty().addListener((obs, old, isFocused) ->
                input.setStyle(isFocused ? focused : base));
    }

    // ─── Footer ────────────────────────────────────────────────────────────────

    private HBox buildFooter(Stage stage) {
        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER);

        Label text = new Label("Belum punya akun?");
        text.setStyle("-fx-font-size: 15px; -fx-text-fill: #999;");

        Hyperlink register = new Hyperlink("Daftar sekarang");
        register.setStyle(
                "-fx-font-size: 15px; -fx-text-fill: " + GREEN_DARK + ";" +
                "-fx-font-weight: bold; -fx-border-color: transparent; -fx-padding: 0;"
        );
        register.setOnAction(e -> {
            stage.close();
            Stage registerStage = new Stage();
            new RegisterUI().start(registerStage);
        });

        footer.getChildren().addAll(text, register);
        return footer;
    }

    // ─── Login logic ───────────────────────────────────────────────────────────

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        hideError();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong.");
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                String body = String.format(
                        "{\"username\":\"%s\",\"password\":\"%s\"}",
                        username.replace("\"", "\\\""),
                        password.replace("\"", "\\\"")
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/auth/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    setLoading(false);
                    if (response.statusCode() == 200) {
                        onLoginSuccess(response.body());
                    } else {
                        showError("Username atau password salah.");
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError("Tidak dapat terhubung ke server. Pastikan aplikasi berjalan.");
                });
            }
        }).start();
    }

    private void onLoginSuccess(String responseBody) {
        // TODO: parse role dari responseBody lalu buka home window
        showError("Login berhasil! (halaman home belum diimplementasi)");
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        loginButton.setText(loading ? "Memuat..." : "Masuk  ➜");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
