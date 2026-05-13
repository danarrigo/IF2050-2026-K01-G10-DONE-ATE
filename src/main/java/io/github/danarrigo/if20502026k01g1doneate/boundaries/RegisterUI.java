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
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.Parent;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class RegisterUI extends UI {

    private static final String BASE_URL   = "http://localhost:8080";
    private static final String GREEN_DARK = "#2a5f2a";
    // private static final String GREEN_LIGHT = "#1a4d1a";

    private static boolean jfxInitialized = false;

    private TextField     usernameField;
    private PasswordField passwordField;
    private TextField     emailField;
    private TextField     addressField;
    private TextField     phoneField;

    private ComboBox<String> donatorTypeBox;
    private VBox             donatorFields;

    private TextField        fullNameField;
    private TextField        opStartField;
    private TextField        opEndField;
    private ComboBox<String> recipientTypeBox;
    private VBox             recipientFields;

    private Label   errorLabel;
    private Button  registerButton;
    private boolean isDonator = true;

    public RegisterUI() {
        super(null);
    }

    @Override
    public void showUI() {
        if (!jfxInitialized) {
            try {
                Platform.startup(() -> {
                });
                jfxInitialized = true;
            } catch (IllegalStateException e) {
                jfxInitialized = true;
            }
        }
        Platform.runLater(() -> start(new Stage()));
    }

    public void start(Stage stage) {
        Scene scene = new Scene(new Pane(), 1920, 1080); // Placeholder
        stage.setTitle("DONE-ATE — Daftar");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        showWithAnimation(stage, createContent(stage));
    }

    public Parent createContent(Stage stage) {
        HBox root = new HBox();
        root.setPrefSize(1920, 1080);
        root.getChildren().addAll(buildLeftPanel(), buildRightPanel(stage));
        return root;
    }

    private void showWithAnimation(Stage stage, Parent newRoot) {
        Parent oldRoot = stage.getScene().getRoot();
        if (oldRoot != null && oldRoot instanceof Pane && !((Pane) oldRoot).getChildren().isEmpty()) {
            FadeTransition out = new FadeTransition(Duration.millis(300), oldRoot);
            out.setFromValue(1.0);
            out.setToValue(0.0);
            out.setOnFinished(e -> {
                stage.getScene().setRoot(newRoot);
                newRoot.setOpacity(0);
                FadeTransition in = new FadeTransition(Duration.millis(300), newRoot);
                in.setFromValue(0.0);
                in.setToValue(1.0);
                in.play();
            });
            out.play();
        } else {
            stage.getScene().setRoot(newRoot);
            newRoot.setOpacity(0);
            FadeTransition in = new FadeTransition(Duration.millis(300), newRoot);
            in.setFromValue(0.0);
            in.setToValue(1.0);
            in.play();
        }
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

        Label desc = new Label("Daftarkan diri Anda sebagai donator atau\npenerima makanan dari komunitas kami.");
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

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefSize(960, 1080);

        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60, 0, 60, 0));
        content.setMaxWidth(560);
        content.getChildren().addAll(buildFormHeader(), buildToggle(), buildCard(), buildFooter(stage));

        StackPane scrollContent = new StackPane(content);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scroll.setContent(scrollContent);
        panel.getChildren().add(scroll);
        return panel;
    }

    private VBox buildFormHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Buat Akun Baru");
        title.setFont(Font.font("System", FontWeight.BOLD, 34));
        title.setStyle("-fx-text-fill: #111;");

        Label sub = new Label("Pilih tipe akun dan isi data diri Anda.");
        sub.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");

        header.getChildren().addAll(title, sub);
        return header;
    }

    // ─── Toggle ────────────────────────────────────────────────────────────────

    private HBox buildToggle() {
        HBox toggleBar = new HBox(0);
        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setStyle(
                "-fx-background-color: #e8f0e8;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 5;"
        );

        ToggleButton donatorBtn   = new ToggleButton("Donator");
        ToggleButton recipientBtn = new ToggleButton("Recipient");

        ToggleGroup group = new ToggleGroup();
        donatorBtn.setToggleGroup(group);
        recipientBtn.setToggleGroup(group);
        donatorBtn.setSelected(true);

        String activeStyle =
                "-fx-background-color: " + GREEN_DARK + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 40 10 40;" +
                "-fx-cursor: hand;";
        String inactiveStyle =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #555;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 40 10 40;" +
                "-fx-cursor: hand;";

        donatorBtn.setStyle(activeStyle);
        recipientBtn.setStyle(inactiveStyle);

        donatorBtn.selectedProperty().addListener((obs, old, selected) -> {
            if (selected) {
                donatorBtn.setStyle(activeStyle);
                recipientBtn.setStyle(inactiveStyle);
                isDonator = true;
                donatorFields.setVisible(true);
                donatorFields.setManaged(true);
                recipientFields.setVisible(false);
                recipientFields.setManaged(false);
                hideMessage();
            }
        });

        recipientBtn.selectedProperty().addListener((obs, old, selected) -> {
            if (selected) {
                recipientBtn.setStyle(activeStyle);
                donatorBtn.setStyle(inactiveStyle);
                isDonator = false;
                donatorFields.setVisible(false);
                donatorFields.setManaged(false);
                recipientFields.setVisible(true);
                recipientFields.setManaged(true);
                hideMessage();
            }
        });

        HBox.setHgrow(donatorBtn, Priority.ALWAYS);
        HBox.setHgrow(recipientBtn, Priority.ALWAYS);
        donatorBtn.setMaxWidth(Double.MAX_VALUE);
        recipientBtn.setMaxWidth(Double.MAX_VALUE);

        toggleBar.getChildren().addAll(donatorBtn, recipientBtn);
        return toggleBar;
    }

    // ─── Card ──────────────────────────────────────────────────────────────────

    private VBox buildCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(32, 30, 32, 30));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 30, 0, 0, 6);"
        );

        usernameField = new TextField();
        usernameField.setPromptText("Masukkan username");
        applyInputStyle(usernameField);

        passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        applyInputStyle(passwordField);

        emailField = new TextField();
        emailField.setPromptText("nama@contoh.com");
        applyInputStyle(emailField);

        addressField = new TextField();
        addressField.setPromptText("Jl. Contoh No. 1");
        applyInputStyle(addressField);

        phoneField = new TextField();
        phoneField.setPromptText("08xxxxxxxxxx");
        applyInputStyle(phoneField);

        donatorTypeBox = new ComboBox<>();
        donatorTypeBox.getItems().addAll("HOTEL", "RESTAURANT", "HAWKER", "CAFE", "BAKERY", "OTHER");
        donatorTypeBox.setPromptText("Pilih tipe donator");
        donatorTypeBox.setMaxWidth(Double.MAX_VALUE);
        applyComboStyle(donatorTypeBox);

        donatorFields = new VBox(16);
        donatorFields.getChildren().add(fieldGroup("Tipe Donator", donatorTypeBox));

        fullNameField = new TextField();
        fullNameField.setPromptText("Nama lengkap");
        applyInputStyle(fullNameField);

        opStartField = new TextField();
        opStartField.setPromptText("08:00");
        applyInputStyle(opStartField);

        opEndField = new TextField();
        opEndField.setPromptText("17:00");
        applyInputStyle(opEndField);

        recipientTypeBox = new ComboBox<>();
        recipientTypeBox.getItems().addAll("INDIVIDUAL", "ORGANIZATION");
        recipientTypeBox.setPromptText("Pilih tipe penerima");
        recipientTypeBox.setMaxWidth(Double.MAX_VALUE);
        applyComboStyle(recipientTypeBox);

        Label startLabel = new Label("Jam Buka");
        startLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        VBox startBox = new VBox(6, startLabel, opStartField);

        Label endLabel = new Label("Jam Tutup");
        endLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        VBox endBox = new VBox(6, endLabel, opEndField);

        HBox opTimeRow = new HBox(12, startBox, endBox);
        HBox.setHgrow(startBox, Priority.ALWAYS);
        HBox.setHgrow(endBox, Priority.ALWAYS);

        recipientFields = new VBox(16);
        recipientFields.setVisible(false);
        recipientFields.setManaged(false);
        recipientFields.getChildren().addAll(
                fieldGroup("Nama Lengkap", fullNameField),
                fieldGroup("Tipe Penerima", recipientTypeBox),
                fieldGroup("Jam Operasional", opTimeRow)
        );

        errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #c62828;");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        registerButton = new Button("Daftar  ➜");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setFont(Font.font("System", FontWeight.BOLD, 17));
        registerButton.setStyle(
                "-fx-background-color: " + GREEN_DARK + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 16 0 16 0;" +
                "-fx-cursor: hand;"
        );
        registerButton.setOnMouseEntered(e ->
                registerButton.setStyle(registerButton.getStyle().replace(GREEN_DARK, "#1e4a1e")));
        registerButton.setOnMouseExited(e ->
                registerButton.setStyle(registerButton.getStyle().replace("#1e4a1e", GREEN_DARK)));
        registerButton.setOnAction(e -> handleRegister());

        card.getChildren().addAll(
                fieldGroup("Username", usernameField),
                fieldGroup("Password", passwordField),
                fieldGroup("Email", emailField),
                fieldGroup("Alamat", addressField),
                fieldGroup("Nomor Telepon", phoneField),
                donatorFields,
                recipientFields,
                errorLabel,
                registerButton
        );
        return card;
    }

    private VBox fieldGroup(String labelText, javafx.scene.Node input) {
        VBox group = new VBox(7);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        group.getChildren().addAll(label, input);
        return group;
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

    private void applyComboStyle(ComboBox<?> combo) {
        combo.setStyle(
                "-fx-background-color: #f6f6f6;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: transparent;" +
                "-fx-border-radius: 12;" +
                "-fx-font-size: 15px;" +
                "-fx-padding: 4 0 4 0;"
        );
    }

    // ─── Footer ────────────────────────────────────────────────────────────────

    private HBox buildFooter(Stage stage) {
        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER);

        Label text = new Label("Sudah punya akun?");
        text.setStyle("-fx-font-size: 15px; -fx-text-fill: #999;");

        Hyperlink loginLink = new Hyperlink("Masuk sekarang");
        loginLink.setStyle(
                "-fx-font-size: 15px; -fx-text-fill: " + GREEN_DARK + ";" +
                "-fx-font-weight: bold; -fx-border-color: transparent; -fx-padding: 0;"
        );
        loginLink.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            showWithAnimation(stage, loginUI.createContent(stage));
        });

        footer.getChildren().addAll(text, loginLink);
        return footer;
    }

    // ─── Register logic ────────────────────────────────────────────────────────

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email    = emailField.getText().trim();
        String address  = addressField.getText().trim();
        String phone    = phoneField.getText().trim();

        hideMessage();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()
                || address.isEmpty() || phone.isEmpty()) {
            showError("Semua field wajib diisi.");
            return;
        }

        if (isDonator) {
            if (donatorTypeBox.getValue() == null) {
                showError("Pilih tipe donator.");
                return;
            }
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"," +
                    "\"address\":\"%s\",\"phoneNumber\":\"%s\",\"donatorType\":\"%s\"}",
                    esc(username), esc(password), esc(email),
                    esc(address), esc(phone), donatorTypeBox.getValue()
            );
            sendRequest("/api/auth/register/donator", body);

        } else {
            String fullName = fullNameField.getText().trim();
            String opStart  = opStartField.getText().trim();
            String opEnd    = opEndField.getText().trim();

            if (fullName.isEmpty() || opStart.isEmpty() || opEnd.isEmpty()
                    || recipientTypeBox.getValue() == null) {
                showError("Semua field wajib diisi.");
                return;
            }

            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\"," +
                    "\"address\":\"%s\",\"phoneNumber\":\"%s\",\"fullName\":\"%s\"," +
                    "\"operationalTimeStart\":\"%s\",\"operationalTimeEnd\":\"%s\"," +
                    "\"recipientType\":\"%s\"}",
                    esc(username), esc(password), esc(email),
                    esc(address), esc(phone), esc(fullName),
                    opStart + ":00", opEnd + ":00",
                    recipientTypeBox.getValue()
            );
            sendRequest("/api/auth/register/recipient", body);
        }
    }

    private void sendRequest(String endpoint, String body) {
        setLoading(true);
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + endpoint))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    setLoading(false);
                    if (response.statusCode() == 201) {
                        onRegisterSuccess();
                    } else {
                        showError("Registrasi gagal. Username mungkin sudah terdaftar.");
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

    private void onRegisterSuccess() {
        showSuccess("Registrasi berhasil! Mengalihkan ke halaman login...");
        registerButton.setDisable(true);
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                Stage currentStage = (Stage) registerButton.getScene().getWindow();
                LoginUI loginUI = new LoginUI();
                showWithAnimation(currentStage, loginUI.createContent(currentStage));
            });
        }).start();
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void setLoading(boolean loading) {
        registerButton.setDisable(loading);
        registerButton.setText(loading ? "Memuat..." : "Daftar  ➜");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #c62828;");
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void showSuccess(String msg) {
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + GREEN_DARK + ";");
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideMessage() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    public static void main(String[] args) {
        new RegisterUI().showUI();
    }
}
